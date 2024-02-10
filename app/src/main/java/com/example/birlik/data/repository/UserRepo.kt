package com.example.birlik.data.repository

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.example.birlik.R
import com.example.birlik.common.entryPriceCalculate
import com.example.birlik.data.remote.GameHistory
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.PlaceOnTable
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.data.remote.durak.Rules
import com.example.birlik.data.remote.durak.Skin
import com.example.birlik.data.remote.durak.SkinSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    auth: FirebaseAuth,
) {
    var decreaseSecond = MutableLiveData(10)

    val isUserLoading = mutableStateOf(false)

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    private val _playerData = MutableStateFlow<PlayerData?>(null)
    val playerData: StateFlow<PlayerData?> = _playerData

    private val _durak = MutableStateFlow<DurakData?>(null)
    val durak: StateFlow<DurakData?> = _durak

    val durakTables = mutableStateOf<List<DurakData>>(emptyList())
    val remainingCards = MutableLiveData<List<CardPair>>(listOf())

    val players = mutableStateOf<MutableList<PlayerData>>(mutableListOf())

    val usersData = mutableStateOf<List<UserData>>(emptyList())
    val onlineUsers = mutableStateOf<List<UserData>>(emptyList())

    init {
        getAllUsers()
        getOnlineUsers()
        getDurakTables()
    }


    fun addUser(userData: UserData) {
        firestore.collection("user").document(userData.userId ?: "").set(userData)
            .addOnSuccessListener {
                isUserLoading.value = false
            }
            .addOnFailureListener {
                isUserLoading.value = false
            }
    }

    fun getUserData(userId: String) {
        firestore.collection("user").document(userId).addSnapshotListener { value, error ->
            value?.let {
                this._userData.value = it.toObject<UserData>()
            }
        }
    }


    private fun getAllUsers() {
        firestore.collection("user")
            .addSnapshotListener { value, _ ->
                value?.let {
                    usersData.value =
                        it.toObjects()
                }
            }
    }

    fun updateUser(
        userData: UserData
    ) {
        firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
            .addOnSuccessListener {
                this._userData.value = userData
            }
    }

    fun rewardUser(
        userData: UserData
    ) {
        val updatedUserData = userData.copy(
            cash = userData.cash + 10000,
            gameHistory = userData.gameHistory?.apply {
                add(
                    GameHistory(
                        title = "Reklam izləmə",
                        moneyIcon = R.drawable.birlik_cash,
                        background = R.color.light_green,
                        amount = "+10000"
                    )
                )
            }
        )
        firestore.collection("user").document(userData.userId ?: "").update(updatedUserData.toMap())
            .addOnSuccessListener {
                this._userData.value = updatedUserData
            }
    }


    fun getOnlineUsers() {
        firestore.collection("user").whereEqualTo("status", "online")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    onlineUsers.value = value.toObjects()
                }
            }
    }


    fun oyuncularaKartPayla(
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
            2 -> 6 // For 2 players, each gets 6 cards
            3 -> 6 // For 3 players, each gets 6 cards
            else -> 0 // Handle other cases as needed
        }

        // Initialize a map to store cards for each player
        val playerCardsMap = mutableMapOf<PlayerData, List<CardPair>>()

        var startIndex = 0

        // Distribute cards among players
        for (player in playerData) {
            val endIndex = startIndex + cardsPerPlayer
            val endIndexLimited = minOf(endIndex, shuffledNewCards.size)

            // Get the cards for the current player
            val playerCards = shuffledNewCards.subList(startIndex, endIndexLimited)
            playerCardsMap[player] = playerCards

            // Move the startIndex for the next player
            startIndex = endIndexLimited
        }

        // Update player data with the assigned cards
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



    fun yerdenKartGotur(
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


    fun kozrGotur(durakData: DurakData) {
        val updateMap = mapOf(
            "kozr" to null,
        )
        firestore.collection("durak").document(durakData.gameId ?: "").update(updateMap)
    }


    fun bitayaGetsin(
        durakData: DurakData,
        playerData: List<PlayerData>,
        cards: List<CardPair>,
    ) {
        val updatedPlayerData = playerData.map { player ->
            player.copy(selectedCard = listOf())
        }

        // Append the new cards to the existing bita
        durakData.bita.addAll(cards)

        val updatedDurakData = durakData.copy(
            playerData = updatedPlayerData.toMutableList(),
            bita = durakData.bita,
            selectedCards = listOf(),
            placeOnTable = null
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())

        updateOyuncuSirasiveStoluCevir(updatedDurakData, true)
    }

    fun eldekiKartlariTemizle(
        durakData: DurakData,
        playerData: PlayerData,
        perevodKartlari: List<CardPair>,
        selectedCard: CardPair
    ) {
        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        for (index in playerDataList.indices) {
            // Clear the selectedCard for all players
            playerDataList[index] = playerDataList[index].copy(selectedCard = emptyList())
        }

        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.username == playerData.userData?.username }

        if (playerIndex != -1) {
            // Add perevodKartlari to the selectedCard list only for playerIndex
            playerDataList[playerIndex] = playerDataList[playerIndex].copy(
                selectedCard = playerDataList[playerIndex].selectedCard?.plus(perevodKartlari),
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

            // Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))
        }
    }


    fun oyunuBaslat(
        durakData: DurakData,
        tableOwner: UserData,
        rules: Rules,
        entryPriceCash: Long = 0,
        entryPriceCoin: Long = 0
    ) {
        val randomId = UUID.randomUUID().toString()

        val durak = durakData.copy(
            gameId = randomId,
            tableOwner = tableOwner,
            rules = rules,
            entryPriceCash = entryPriceCash,
            entryPriceCoin = entryPriceCoin
        )
        firestore.collection("durak").document(randomId).set(durak)
        stolaOtur(
            durakData.copy(gameId = randomId),
            PlayerData(userData = userData.value, cards = null),
            tableNumber = 1
        )

    }

    fun getDurakTables() {
        firestore.collection("durak").addSnapshotListener { value, error ->
            value?.let {
                durakTables.value = it.toObjects()
            }
        }
    }

    fun refreshCards(
        durakData: DurakData,
        userData: UserData,
        playerData: PlayerData,
        card: List<CardPair>
    ) {
        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        // Find the index of the playerData to update
        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.username == playerData.userData?.username }

        if (playerIndex != -1) {
            val updatedPlayerData = playerDataList[playerIndex].copy(
                cards = playerDataList[playerIndex].cards?.toMutableList()?.apply {
                    removeAll(card)
                    addAll(card)
                }
            )

            playerDataList[playerIndex] = updatedPlayerData

            // Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))

            firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
                .addOnSuccessListener {
                    this._userData.value = userData
                }
        }
    }


    fun attackFirst(
        durakData: DurakData,
        placeOnTable: PlaceOnTable
    ) {

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update("placeOnTable", placeOnTable.toMap())
    }

//
//    fun sampleFunction(
//        userData: UserData,
//        person: Person
//    ){
//        val updatedUserData = userData.copy(
//            username = "newOne",
//            //person == it is list, so I cannot pass it
//        )
//        //I do something on UserData, For example I change 'name' field in UserData
//        anotherFunction(userData, person)
//        //When I call "anotherFunction" and passes userData, it takes old userData. I want to take updated one
//    }
//
//    fun anotherFunction(
//        userData: UserData,
//        person: Person
//    ){
//        val currentPerson = userData.blockList.find{it.username == person.username}
//
//        //I do something to currentPerson
//
//        firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
//    }

    fun yereKartDus(
        durakData: DurakData,
        playerData: PlayerData,
        selectedCard: CardPair,
        rotate: Boolean = false,
        changeAttacker: Boolean = false,
        placeOnTable: PlaceOnTable,
        perevodKartlari: List<CardPair>
    ) {
        // Assuming playerDataList is mutable
        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        // Find the index of the playerData to update
        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.username == playerData.userData?.username }

        if (playerIndex != -1) {
            // Update the specific PlayerData instance
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
                playerData = playerDataList
            )

                // Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("placeOnTable", placeOnTable.toMap()).addOnSuccessListener {
                    if (rotate && changeAttacker) {
                        updateOyuncuSirasiveStoluCevir(updatedDurakData, perevod = true)
                        eldekiKartlariTemizle(updatedDurakData, playerData, perevodKartlari, selectedCard)
                    } else if (rotate) {
                        updateOyuncuSirasiveStoluCevir(updatedDurakData)
                    }
                    updateYerdekiKartlar(updatedDurakData, selectedCard)
                }
        }
    }


    fun eleYig(durakData: DurakData, playerData: PlayerData, selectedCards: List<CardPair>) {
        val playerDataList = durakData.playerData?.toMutableList() ?: mutableListOf()

        val playerIndex =
            playerDataList.indexOfFirst { it.userData?.username == playerData.userData?.username }

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
            placeOnTable = null,
            playerData = playerDataList
        )
            // Update only the playerData field in Firestore
        firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))
        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())

        updateOyuncuSirasiveStoluCevir(updatedDurakData, true)

    }


    fun getDurakData(gameId: String) {
        firestore.collection("durak").document(gameId)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    this._durak.value = value.toObject()
                }
            }
    }

    fun stolaOtur(durakData: DurakData, playerData: PlayerData, tableNumber: Int) {
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
            .update(updatedDurakData)
    }

    fun stoldanQalx(durakData: DurakData, userId: String) {
        val updatedPlayerList = durakData.playerData?.filterNot { it.userData?.userId == userId }
        val firstTableFull = durakData.tableData?.firstTable?.userId == userId
        val secondTableFull = durakData.tableData?.secondTable?.userId == userId
        val thirdTableFull = durakData.tableData?.thirdTable?.userId == userId

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
        } else {
            // Handle the case where no updates are needed
        }
    }


    fun setTimer(durakData: DurakData, timer: Int, onComplete: () -> Unit) {
        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(mapOf("timer" to timer)).addOnSuccessListener {
                onComplete()
            }
    }


    fun updateDurakData(update: (DurakData) -> DurakData) {
        _durak.value = _durak.value?.let { currentDurakData ->
            update(currentDurakData)
        }
    }

    fun updateDurakCards(durakData: DurakData, cards: MutableList<CardPair>) {
        val updatedDurakData = durakData.copy(
            cards = cards
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())
    }

    fun updateOyuncuSirasi(durakData: DurakData, startingPlayer: String, starterTableNumber: Int) {
        val updatedDurakData = durakData.copy(
            startingPlayer = startingPlayer,
            starterTableNumber = starterTableNumber,
            attacker = startingPlayer,
            choseAttacker = true,
            cardsOnHands = true
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())
    }

    fun updateHucumcu(durakData: DurakData, attacker: String) {
        val updateMap = mapOf(
            "attacker" to attacker
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun loseGame(durakData: DurakData, loser: UserData, winner: UserData, onSuccess: () -> Unit) {
        val updateMap = mapOf(
            "loser" to loser,
            "finished" to true
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap).addOnSuccessListener {
                onSuccess()
            }

        val ratingPrice =
            if (winner.rating - loser.rating > 10000) 10
            else if (winner.rating - loser.rating > 1000) 25
            else if (winner.rating - loser.rating >= 0) 50
            else if (winner.rating - loser.rating > -1000) 75
            else if (winner.rating - loser.rating > -10000) 100
            else 150

        val moneyIcon =
            if (durakData.entryPriceCash == 0.toLong() && durakData.entryPriceCoin == 0.toLong()) R.drawable.birlik_cash
            else if (durakData.entryPriceCash != 0.toLong()) R.drawable.birlik_cash
            else if (durakData.entryPriceCoin != 0.toLong()) R.drawable.birlik_coin
            else R.drawable.car

        val entryPrice = maxOf(durakData.entryPriceCash, durakData.entryPriceCoin, 0.toLong())

        val amount =
            if (durakData.entryPriceCash != 0L) "${entryPriceCalculate(durakData.entryPriceCash)}"
            else if (durakData.entryPriceCoin != 0L) "${entryPriceCalculate(durakData.entryPriceCoin)}"
            else ""

        val updatedWinnerData = winner.copy(
            cash = winner.cash + entryPriceCalculate(durakData.entryPriceCash),
            coin = winner.coin + entryPriceCalculate(durakData.entryPriceCoin),
            rating = winner.rating + ratingPrice,
            gameHistory = winner.gameHistory?.apply {
                add(
                    GameHistory(
                        title = "Qələbə",
                        winner = "${winner.username}",
                        loser = "${loser.username}",
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
                        title = "Məğlubiyyət",
                        winner = "${winner.username}",
                        loser = "${loser.username}",
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

        firestore.collection("user").document(winner.userId ?: "").update(
            updatedWinnerData.toMap()
        )

        firestore.collection("user").document(loser.userId ?: "").update(
            updatedLoserData.toMap()
        )
    }


    fun deleteGame(durakData: DurakData) {
        firestore.collection("durak").document(durakData.gameId ?: "")
            .delete()
    }

    fun updateYerdekiKartlar(durakData: DurakData, selectedCard: CardPair) {
        val updateMap = mapOf(
            "selectedCards" to durakData.selectedCards.plus(selectedCard)
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun updateOyuncuSirasiveStoluCevir(
        durakData: DurakData,
        updateHucumcu: Boolean = false,
        perevod: Boolean = false,
    ) {
        // Rotate the table number
        val nextTableNumber = (durakData.starterTableNumber ?: 0) % 2 + 1

        // Find the corresponding userData in the rotated table
        val nextStarter = when (nextTableNumber) {
            1 -> durakData.tableData?.firstTable?.username
            2 -> durakData.tableData?.secondTable?.username
            else -> null
        }

        val updatedDurakData = durakData.copy(
            startingPlayer = nextStarter,
            starterTableNumber = nextTableNumber
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updatedDurakData.toMap())

        if (updateHucumcu) {
            updateHucumcu(durakData, nextStarter ?: "")
        } else if (perevod) {
            updateHucumcu(durakData, durakData.startingPlayer ?: "")
        }
    }


    fun deleteAllGames() {
        firestore.collection("durak").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }

    fun changeSkin(userData: UserData) {
        firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
            .addOnSuccessListener {
                this._userData.value = userData
            }
    }


    private var listenerRegistration: ListenerRegistration? = null

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
}