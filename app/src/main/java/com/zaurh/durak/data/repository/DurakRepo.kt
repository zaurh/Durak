package com.zaurh.durak.data.repository

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.zaurh.durak.R
import com.zaurh.durak.common.entryPriceCalculate
import com.zaurh.durak.data.remote.GameHistory
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.CardPair
import com.zaurh.durak.data.remote.durak.DurakData
import com.zaurh.durak.data.remote.durak.PlaceOnTable
import com.zaurh.durak.data.remote.durak.PlayerData
import com.zaurh.durak.data.remote.durak.Rules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

class DurakRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepo: UserRepo,
    private val auth: FirebaseAuth
) {

    val allUsers = userRepo.usersData

    private val _durak = MutableStateFlow<DurakData?>(null)
    val durak: StateFlow<DurakData?> = _durak

    val durakTables = mutableStateOf<List<DurakData>>(emptyList())

    var decreaseSecond = MutableLiveData(10)

    val remainingCards = MutableLiveData<List<CardPair>>(listOf())


    init {
        getDurakTables()
    }


    //Get UserId from Auth.
    fun getCurrentUserId(): String?{
        return auth.currentUser?.uid
    }

    //When game starts distribute cards to players.
    fun distributeCardsToPlayers(
        durakData: DurakData,
        playerData: List<PlayerData>,
        originalList: List<CardPair>,
        kozr: CardPair,
        remainingCards: List<CardPair>,
        onComplete: (List<CardPair>) -> Unit,
        onSuccess: () -> Unit
    ) {
        val shuffledNewCards = originalList.shuffled()

        val cardsPerPlayer = when (playerData.size) {
            1 -> 6
            2 -> 6
            3 -> 6
            else -> 0
        }

        val playerCardsMap = mutableMapOf<PlayerData, List<CardPair>>()

        var startIndex = 0

        //Distribute cards among players
        for (player in playerData) {
            val endIndex = startIndex + cardsPerPlayer
            val endIndexLimited = minOf(endIndex, shuffledNewCards.size)

            //Get the cards for the current player
            val playerCards = shuffledNewCards.subList(startIndex, endIndexLimited)
            playerCardsMap[player] = playerCards

            //Move the startIndex for the next player
            startIndex = endIndexLimited
        }

        //Update player data with the assigned cards
        val updatedPlayerData = playerData.map { player ->
            player.copy(
                cards = playerCardsMap[player] ?: listOf(),
                selectedCard = listOf(),
            )
        }

        val updatedDurakData = durakData.copy(
            playerData = updatedPlayerData.toMutableList(),
            cards = remainingCards.toMutableList(),
            kozr = kozr,
            kozrSuit = kozr.suit,
            started = true
        )

        // Return the remaining cards via the onComplete callback
        onComplete(shuffledNewCards.subList(startIndex, shuffledNewCards.size))

        firestore.collection("durak").document(durakData.gameId ?: "")
            .set(updatedDurakData.toMap(), SetOptions.merge()).addOnSuccessListener {
                onSuccess()
            }

    }


    //When round finishes, take cards from remaining cards.
    fun takeCardsAfterRound(
        durakData: DurakData,
        player: PlayerData,
        nextPlayer: PlayerData,
        playerDataList: List<PlayerData>,
        card: List<CardPair>,
        nextPlayerCard: List<CardPair>,
        remainingCards: List<CardPair>,
    ) {
        val updatedPlayerDataList = playerDataList.map { playerData ->
            when (playerData.userData?.userId) {
                player.userData?.userId -> {
                    val updatedCards = player.cards.orEmpty() + card
                    player.copy(cards = updatedCards)
                }

                nextPlayer.userData?.userId -> {
                    val updatedCards = nextPlayer.cards.orEmpty() + nextPlayerCard
                    nextPlayer.copy(cards = updatedCards)
                }

                else -> playerData
            }
        }

        val updatedDurakData = durakData.copy(
            playerData = updatedPlayerDataList.toMutableList(),
            cards = remainingCards.toMutableList()
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())
    }


    //Pass cards on table to bita.
    fun passToBita() {
        val durakData = durak.value ?: DurakData()
        val playerData = durakData.playerData ?: listOf()
        val cards = durakData.playerData?.flatMap { it.selectedCard ?: listOf() } ?: listOf()

        val updatedPlayerData = playerData.map { player ->
            player.copy(selectedCard = listOf())
        }

        val nextTableNumber = (durakData.starterTableNumber ?: 0) % 2 + 1

        val nextStarter = when (nextTableNumber) {
            1 -> durakData.tableData?.firstTable?.email
            2 -> durakData.tableData?.secondTable?.email
            else -> ""
        } ?: ""

        //Append the new cards to the existing bita
        durakData.bita.addAll(cards)

        val updatedDurakData = durakData.copy(
            playerData = updatedPlayerData.toMutableList(),
            bita = durakData.bita,
            selectedCards = listOf(),
            placeOnTable = PlaceOnTable(),
            startingPlayer = nextStarter,
            starterTableNumber = nextTableNumber,
            attacker = nextStarter
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())
    }


    //When player "perevod" the card, it takes all selected cards to itself.
    private fun clearCardsOnHands(
        durakData: DurakData,
        perevodCards: List<CardPair>,
        selectedCard: CardPair
    ) {
        val currentPlayer =
            durakData.playerData?.find { it.userData?.userId == getCurrentUserId() } ?: PlayerData()

        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        for (index in playerDataList.indices) {
            // Clear the selectedCard for all players
            playerDataList[index] = playerDataList[index].copy(selectedCard = emptyList())
        }

        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.email == currentPlayer.userData?.email }

        if (playerIndex != -1) {
            // Add perevodCards to the selectedCard list only for playerIndex
            playerDataList[playerIndex] = playerDataList[playerIndex].copy(
                selectedCard = playerDataList[playerIndex].selectedCard?.plus(perevodCards),
                cards = playerDataList[playerIndex].cards?.toMutableList()?.apply {
                    // Remove the selected card from the cards list
                    remove(
                        if ((selectedCard.number
                                ?: 0) > 14
                        ) selectedCard.copy(number = selectedCard.number?.minus(15)) else selectedCard
                    )
                },
                lastDroppedCardNum = selectedCard

            )

            //Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))
        }
    }



    //Start the game.
    fun startGame(
        durakData: DurakData,
        rules: Rules,
        entryPriceCash: Long = 0,
        entryPriceCoin: Long = 0
    ) {
        val currentUser = allUsers.value.find { it.userId == getCurrentUserId() }

        val randomId = UUID.randomUUID().toString()

        val durak = durakData.copy(
            gameId = randomId,
            tableOwner = currentUser,
            rules = rules,
            entryPriceCash = entryPriceCash,
            entryPriceCoin = entryPriceCoin,
            placeOnTable = PlaceOnTable()
        )
        firestore.collection("durak").document(randomId).set(durak).addOnCanceledListener {

        }
        sitDown(
            durakData = durakData.copy(gameId = randomId),
            playerData = PlayerData(userData = currentUser, cards = null),
            tableNumber = 1
        ){

        }

    }


    //Get all durak tables.
    private fun getDurakTables() {
        firestore.collection("durak").addSnapshotListener { value, error ->
            value?.let {
                durakTables.value = it.toObjects()
            }
        }
    }




    //Push your card to table.
    fun putCardOnTable(
        selectedCard: CardPair,
        rotate: Boolean = false,
        changeAttacker: Boolean = false,
        placeOnTable: PlaceOnTable,
        perevodCards: List<CardPair>,
        onSuccess: () -> Unit
    ) {
        val durakData = durak.value ?: DurakData()

        val currentPlayer =
            durakData.playerData?.find { it.userData?.userId == getCurrentUserId() } ?: PlayerData()

        val nextTableNumber = (durakData.starterTableNumber ?: 0) % 2 + 1

        val nextStarter = when (nextTableNumber) {
            1 -> durakData.tableData?.firstTable?.email
            2 -> durakData.tableData?.secondTable?.email
            else -> ""
        } ?: ""

        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.email == currentPlayer.userData?.email }

        if (playerIndex != -1) {
            val updatedPlayerData = playerDataList[playerIndex].copy(
                selectedCard = playerDataList[playerIndex].selectedCard?.plus(selectedCard)
                    ?: listOf(),
                cards = playerDataList[playerIndex].cards?.toMutableList()?.apply {
                    // Remove the selected card from the cards list
                    remove(
                        if ((selectedCard.number
                                ?: 0) > 14
                        ) selectedCard.copy(number = selectedCard.number?.minus(15)) else selectedCard
                    )
                },
                lastDroppedCardNum = selectedCard
            )

            playerDataList[playerIndex] = updatedPlayerData

            val updatedDurakData = durakData.copy(
                placeOnTable = placeOnTable,
                playerData = playerDataList,
                selectedCards = durakData.selectedCards.plus(selectedCard),
                startingPlayer = if (rotate) nextStarter else durakData.startingPlayer,
                starterTableNumber = if (rotate) nextTableNumber else durakData.starterTableNumber,
                attacker = if (rotate && changeAttacker) durakData.startingPlayer else durakData.attacker
            )


            // Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap)).addOnSuccessListener{
                    firestore.collection("durak").document(durakData.gameId ?: "")
                        .update("placeOnTable", placeOnTable.toMap()).addOnSuccessListener {
                            if (rotate && changeAttacker) {
                                clearCardsOnHands(
                                    durakData = updatedDurakData,
                                    perevodCards = perevodCards,
                                    selectedCard = selectedCard
                                )
                            }
                            onSuccess()
                        }

                    firestore.collection("durak").document(durakData.gameId ?: "")
                        .update(updatedDurakData.toMap())
                }
        }
    }


    //Take all cards on table to your hand.
    fun takeCardsToHand(selectedCards: List<CardPair>) {
        val durakData = durak.value ?: DurakData()

        val currentPlayer =
            durakData.playerData?.find { it.userData?.userId == getCurrentUserId()} ?: PlayerData()

        val nextTableNumber = (durakData.starterTableNumber ?: 0) % 2 + 1

        val nextStarter = when (nextTableNumber) {
            1 -> durakData.tableData?.firstTable?.email
            2 -> durakData.tableData?.secondTable?.email
            else -> ""
        } ?: ""

        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.userId == currentPlayer.userData?.userId }

        for (index in playerDataList.indices) {
            val updatedPlayerData = playerDataList[index].copy(
                selectedCard = emptyList()
            )
            playerDataList[index] = updatedPlayerData
        }

        if (playerIndex != -1) {
            // Update the specific PlayerData instance
            val updatedPlayerData = playerDataList[playerIndex].copy(
                cards = playerDataList[playerIndex].cards?.plus(selectedCards)
                    ?: listOf()
            )
            playerDataList[playerIndex] = updatedPlayerData
        }
        val updatedDurakData = durakData.copy(
            selectedCards = listOf(),
            placeOnTable = PlaceOnTable(),
            playerData = playerDataList,
            startingPlayer = nextStarter,
            starterTableNumber = nextTableNumber
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap()).addOnSuccessListener {
                firestore.collection("durak").document(durakData.gameId ?: "")
                    .update("playerData", playerDataList.map(PlayerData::toMap))
            }

    }


    //Get specific durak game.
    fun getDurakData(gameId: String) {
        firestore.collection("durak").document(gameId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    this._durak.value = value.toObject()
                }
            }
    }

    //Sit down on the table.
    fun sitDown(
        durakData: DurakData,
        playerData: PlayerData,
        tableNumber: Int,
        onSuccess: () -> Unit
    ) {
        val updatedPlayerData =
            playerData.copy(tableNumber = tableNumber, playerId = playerData.userData?.userId)

        val updatedDurakData = hashMapOf<String, Any>(
            "playerData" to FieldValue.arrayUnion(updatedPlayerData)
        )

        when (tableNumber) {
            1 -> updatedDurakData["tableData.firstTable"] = playerData.userData ?: UserData()
            2 -> updatedDurakData["tableData.secondTable"] = playerData.userData ?: UserData()
            3 -> updatedDurakData["tableData.thirdTable"] = playerData.userData ?: UserData()
        }

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData).addOnSuccessListener {
                onSuccess()
            }
    }


    //Stand up from the table.
    fun standUp(durakData: DurakData) {
        val updatedPlayerList = durakData.playerData?.filterNot { it.userData?.userId == getCurrentUserId() }
        val firstTableFull = durakData.tableData?.firstTable?.userId == getCurrentUserId()
        val secondTableFull = durakData.tableData?.secondTable?.userId == getCurrentUserId()
        val thirdTableFull = durakData.tableData?.thirdTable?.userId == getCurrentUserId()

        val updateMap = mutableMapOf<String, Any?>()

        if (firstTableFull) {
            updateMap["tableData.firstTable"] = null
        } else if (secondTableFull) {
            updateMap["tableData.secondTable"] = null
        } else if (thirdTableFull) {
            updateMap["tableData.thirdTable"] = null
        }

        updateMap["playerData"] = updatedPlayerList

        val gameId = durakData.gameId ?: ""

        if (updateMap.isNotEmpty()) {
            firestore.collection("durak").document(gameId)
                .update(updateMap)
        }
    }



    //After changes update durak data.
    fun updateDurakData(update: (DurakData) -> DurakData) {
        _durak.value = _durak.value?.let { currentDurakData ->
            update(currentDurakData)
        }
    }


    //Set who is starting.
    fun setPlayerTurn(
        durakData: DurakData,
        startingPlayer: String,
        starterTableNumber: Int,
        onSuccess: () -> Unit
    ) {
        val updatedDurakData = durakData.copy(
            startingPlayer = startingPlayer,
            starterTableNumber = starterTableNumber,
            attacker = startingPlayer,
            choseAttacker = true,
            cardsOnHands = true
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap()).addOnSuccessListener {
                onSuccess()
                decreaseSecond.value = 10
            }
    }


    //Finish game.
    fun finishGame(durakData: DurakData, loser: UserData, winner: UserData, onSuccess: () -> Unit) {

        val ratingPrice =
            if (winner.rating - loser.rating > 10000) 10
            else if (winner.rating - loser.rating > 1000) 25
            else if (winner.rating - loser.rating >= 0) 50
            else if (winner.rating - loser.rating > -1000) 75
            else if (winner.rating - loser.rating > -10000) 100
            else 150

        val moneyIcon = if (durakData.entryPriceCash != 0.toLong()) {
            R.drawable.birlik_cash
        } else if (durakData.entryPriceCoin != 0.toLong()) {
            R.drawable.birlik_coin
        } else {
            R.drawable.birlik_cash
        }

        val entryPrice = maxOf(durakData.entryPriceCash, durakData.entryPriceCoin, 0.toLong())

        val amount =
            if (durakData.entryPriceCash != 0L) "${entryPriceCalculate(durakData.entryPriceCash)}"
            else if (durakData.entryPriceCoin != 0L) "${entryPriceCalculate(durakData.entryPriceCoin)}"
            else ""

        val updatedDurakData = durakData.copy(
            loser = loser,
            finished = true,
            rating = ratingPrice
        )

        val updatedWinnerData = winner.copy(
            cash = winner.cash + entryPriceCalculate(durakData.entryPriceCash),
            coin = winner.coin + entryPriceCalculate(durakData.entryPriceCoin),
            rating = winner.rating + ratingPrice,
            gameHistory = winner.gameHistory?.apply {
                add(
                    GameHistory(
                        title = "Win",
                        winner = "${winner.name}",
                        winnerId = "${winner.userId}",
                        loser = "${loser.name}",
                        loserId = "${loser.userId}",
                        game = "Durak",
                        moneyIcon = moneyIcon,
                        amount = if (amount.isEmpty()) "" else "+$amount",
                        background = R.color.light_green,
                        rating = "+$ratingPrice"
                    )
                )
            },
            durakWinCount = winner.durakWinCount + 1
        )
        val updatedLoserData = loser.copy(
            cash = loser.cash - durakData.entryPriceCash.toInt(),
            coin = loser.coin - durakData.entryPriceCoin.toInt(),
            rating = (if (loser.rating > 150) loser.rating - ratingPrice * 1.2.toInt() else loser.rating),
            gameHistory = loser.gameHistory?.apply {
                add(
                    GameHistory(
                        title = "Lose",
                        winner = "${winner.name}",
                        winnerId = "${winner.userId}",
                        loser = "${loser.name}",
                        loserId = "${loser.userId}",
                        game = "Durak",
                        moneyIcon = moneyIcon,
                        amount = if (entryPrice != 0.toLong()) "-$entryPrice" else "",
                        background = R.color.light_red,
                        rating = "-$ratingPrice"
                    )
                )
            },
            durakLoseCount = loser.durakLoseCount + 1
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap()).addOnSuccessListener {
                onSuccess()
            }

        firestore.collection("user").document(winner.userId ?: "").update(
            updatedWinnerData.toMap()
        )

        firestore.collection("user").document(loser.userId ?: "").update(
            updatedLoserData.toMap()
        )
    }


    //Delete game.
    fun deleteGame(durakData: DurakData) {
        firestore.collection("durak").document(durakData.gameId ?: "")
            .delete()
    }


    //Delete all games.
    fun deleteAllGames() {
        firestore.collection("durak").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }

    //Timer for players.
    fun timeDone(onComplete: () -> Unit) {
        val durakData = durak.value

        if (durakData != null) {
            val updatedDurakData = durakData.copy(
                timer = true
            )
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update(updatedDurakData.toMap())
                .addOnSuccessListener {
                    onComplete()
                }
        }
    }

    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningForStartingPlayer(
        durakData: DurakData,
        onChanged: () -> Unit
    ) {
        val docRef = firestore.collection("durak").document(durakData.gameId ?: "")
        listenerRegistration = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle errors
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val startingPlayer = snapshot.getString("startingPlayer")
                decreaseSecond.value = 10
                // Check if startingPlayer field has changed
                if (startingPlayer != null && startingPlayer != durakData.startingPlayer && durakData.started == true) {
                    onChanged()

                }
            }
        }
    }


    fun startListeningForDurakUpdates(
        durakData: DurakData
    ) {
        val docRef = firestore.collection("durak").document(durakData.gameId ?: "")

        listenerRegistration = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle errors
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Parse the updated game state, including remainingCards
                val updatedDurakData = snapshot.toObject(DurakData::class.java)

                // Update the local state with the new game state
                updateDurakData { durakData ->
                    durakData.copy(cards = updatedDurakData?.cards ?: mutableListOf())
                }

                // Update remainingCards in local state
                remainingCards.value = updatedDurakData?.cards ?: emptyList()
            }
        }
    }



    //    //Function for ascending cards on hand by its rank.
//    fun refreshCards(
//        durakData: DurakData,
//        playerData: PlayerData,
//        card: List<CardPair>,
//        onSort: () -> Unit
//    ) {
//        val currentUser = allUsers.value.find { it.userId == currentUserId } ?: UserData()
//        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()
//
//        val playerIndex =
//            playerDataList.indexOfFirst { it.userData?.email == playerData.userData?.email }
//
//        if (playerIndex != -1) {
//            val updatedPlayerData = playerDataList[playerIndex].copy(
//                cards = playerDataList[playerIndex].cards?.toMutableList()?.apply {
//                    removeAll(card)
//                    addAll(card)
//                }
//            )
//
//            playerDataList[playerIndex] = updatedPlayerData
//
//            firestore.collection("durak").document(durakData.gameId ?: "")
//                .update("playerData", playerDataList.map(PlayerData::toMap))
//
//            firestore.collection("user").document(currentUserId ?: "").update(currentUser.toMap())
//                .addOnSuccessListener {
//                    onSort()
////                    this._userData.value = userData
//                }
//        }
//    }

}