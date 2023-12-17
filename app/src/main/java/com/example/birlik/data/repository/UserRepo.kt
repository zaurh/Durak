package com.example.birlik.data.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.PlayerData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    auth: FirebaseAuth,
) {

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
    val currentUserId = auth.currentUser?.uid


    init {
        getAllUsers()
        getOnlineUsers()
        getDurakTables()
    }

    fun addData(userData: UserData) {
        firestore.collection("user").document("asjdhasdkj").set(userData)
            .addOnSuccessListener {
                isUserLoading.value = false
            }
            .addOnFailureListener {
                isUserLoading.value = false
            }
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
                value?.let { it ->
                    usersData.value =
                        it.toObjects<UserData>().sortedBy { it.username }
                }
            }
    }

    fun updateUser(
        userData: UserData
    ) {
        if (currentUserId != null) {
            firestore.collection("user").document(currentUserId).update(userData.toMap())
                .addOnSuccessListener {
                    this._userData.value = userData
                }
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


    //
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

        val numPlayers = playerData.size
        val cardsPerPlayer = when (numPlayers) {
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
            player.copy(cards = playerCardsMap[player] ?: listOf(), selectedCard = listOf())
        }

        // Update the Firestore document
        val updateMap = mapOf(
            "playerData" to updatedPlayerData.map(PlayerData::toMap),
            "cards" to remainingCards,
            "kozr" to kozr,
            "kozrSuit" to kozr.suit,
            "started" to true
        )

        // Return the remaining cards via the onComplete callback
        onComplete(shuffledNewCards.subList(startIndex, shuffledNewCards.size))

        firestore.collection("durak").document(durakData.gameId ?: "").set(updateMap, SetOptions.merge()).addOnSuccessListener {
            onSuccess()
        }

    }


    fun yerdenKartGotur(
        durakData: DurakData,
        player: PlayerData,
        playerDataList: List<PlayerData>,
        card: CardPair
    ) {
        val updatedPlayerDataList = playerDataList.map { playerData ->
            if (playerData.userData?.userId == player.userData?.userId) {
                // Update the cards for the specific player
                val updatedCards = playerData.cards.orEmpty() + card
                playerData.copy(cards = updatedCards)
            } else {
                playerData
            }
        }

        val updateMap = mapOf(
            "playerData" to updatedPlayerDataList.map(PlayerData::toMap),
        )

        firestore.collection("durak").document(durakData.gameId ?: "").update(updateMap)
    }

    fun kozrGotur(durakData: DurakData){
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

        val updateMap = mapOf(
            "playerData" to updatedPlayerData.map(PlayerData::toMap),
            "bita" to durakData.bita,
            "selectedCards" to listOf<CardPair>()
        )
        firestore.collection("durak").document(durakData.gameId ?: "").update(updateMap)

        updateOyuncuSirasiveStoluCevir(durakData, true)
        updateRound(durakData, false)
    }


    fun oyunuBaslat(durakData: DurakData, tableOwner: UserData) {
        val randomId = UUID.randomUUID().toString()

        val durak = durakData.copy(
            gameId = randomId,
            tableOwner = tableOwner
        )
        firestore.collection("durak").document(randomId).set(durak)

    }

    fun getDurakTables() {
        firestore.collection("durak").addSnapshotListener { value, error ->
            value?.let {
                durakTables.value = it.toObjects()
            }
        }
    }

    fun yereKartDus(durakData: DurakData, playerData: PlayerData, selectedCard: CardPair, rotate: Boolean = false) {
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
                    val kozrSelectedCard = selectedCard.copy(number = selectedCard.number?.plus(15))
                    remove(if ((selectedCard.number ?: 0) > 14) selectedCard.copy(number = selectedCard.number?.minus(15)) else selectedCard )
                },
                lastDroppedCardNum = selectedCard
            )
            playerDataList[playerIndex] = updatedPlayerData

            // Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))
        }
        if (rotate){
            updateOyuncuSirasiveStoluCevir(durakData)
        }
        updateRound(durakData, true)
        updateYerdekiKartlar(durakData, selectedCard)
    }

    fun eleYig(durakData: DurakData, playerData: PlayerData, selectedCards: List<CardPair>){
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

            // Update only the playerData field in Firestore
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("playerData", playerDataList.map(PlayerData::toMap))
            firestore.collection("durak").document(durakData.gameId ?: "")
                .update("selectedCards", listOf<CardPair>())
        }
        updateOyuncuSirasiveStoluCevir(durakData, true)
        updateRound(durakData, false)
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
        val updatedPlayerData = playerData.copy(tableNumber = tableNumber, playerId = playerData.userData?.userId)

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





    fun updateDurakData(update: (DurakData) -> DurakData) {
        _durak.value = _durak.value?.let { currentDurakData ->
            update(currentDurakData)
        }
    }

    fun updateDurakCards(durakData: DurakData, cards: MutableList<CardPair>) {
        val updateMap = mapOf(
            "cards" to cards
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun updateOyuncuSirasi(durakData: DurakData, startingPlayer: String, starterTableNumber: Int){
        val updateMap = mapOf(
            "startingPlayer" to startingPlayer,
            "starterTableNumber" to starterTableNumber,
            "attacker" to startingPlayer,
            "choseAttacker" to true
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun updateHucumcu(durakData: DurakData, attacker: String){
        val updateMap = mapOf(
            "attacker" to attacker
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun winGame(durakData: DurakData, userData: UserData, onSuccess: () -> Unit){
        val updateMap = mapOf(
            "winner" to userData
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap).addOnSuccessListener {
                onSuccess()
            }
    }

    fun updateRound(durakData: DurakData, round: Boolean){
        val updateMap = mapOf(
            "round" to round
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun updateYerdekiKartlar(durakData: DurakData, selectedCard: CardPair){
        val updateMap = mapOf(
            "selectedCards" to durakData.selectedCards.plus(selectedCard)
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
    }

    fun updateOyuncuSirasiveStoluCevir(durakData: DurakData, updateHucumcu: Boolean = false) {
        // Rotate the table number
        val nextTableNumber = (durakData.starterTableNumber ?: 0) % 2 + 1

        // Find the corresponding userData in the rotated table
        val nextStarter = when (nextTableNumber) {
            1 -> durakData.tableData?.firstTable?.username
            2 -> durakData.tableData?.secondTable?.username
            else -> null
        }

        // Update the starter and the table number
        val updateMap = mapOf(
            "startingPlayer" to nextStarter,
            "starterTableNumber" to nextTableNumber
        )

        firestore.collection("durak").document(durakData.gameId ?: "")
            .update(updateMap)
        if (updateHucumcu){
            updateHucumcu(durakData, nextStarter ?: "")
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