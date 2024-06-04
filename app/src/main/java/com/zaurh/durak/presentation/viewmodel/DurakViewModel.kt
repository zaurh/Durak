package com.zaurh.durak.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.durak.R
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.CardPair
import com.zaurh.durak.data.remote.durak.DurakData
import com.zaurh.durak.data.remote.durak.PlaceOnTable
import com.zaurh.durak.data.remote.durak.PlayerData
import com.zaurh.durak.data.remote.durak.Rules
import com.zaurh.durak.data.repository.DurakRepo
import com.zaurh.durak.presentation.screen.showRemainingCardsToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DurakViewModel @Inject constructor(
    private val durakRepo: DurakRepo
) : ViewModel() {

    val durakDataState = durakRepo.durak
    val remainingCardsRepo = durakRepo.remainingCards
    val durakTables = durakRepo.durakTables
    private val currentPlayer = mutableStateOf<PlayerData?>(null)
    val dropCard = mutableStateOf<CardPair?>(null)
    val isDragging = mutableStateOf(false)
    val buttonsState = mutableStateOf(true)
    private var job: Job? = null

    var cardsNotAttacked = mutableStateOf<List<CardPair?>>(listOf())


    private var isSearchStarting = true
    private var initialDurakData = listOf<DurakData>()


    init {
        viewModelScope.launch {
            durakRepo.durak.collect {
                currentPlayer.value =
                    durakDataState.value?.playerData?.find { it.userData?.userId == durakRepo.getCurrentUserId() }
            }
        }
    }

    fun startGame(
        title: String,
        rules: Rules,
        entryPriceCash: Long,
        entryPriceCoin: Long,
        context: Context
    ) {
        val currentUser =
            durakRepo.allUsers.value.find { it.userId == durakRepo.getCurrentUserId() }

        val durakTablesFiltered = durakTables.value.filter { !it.finished }
        val userInGame =
            durakTablesFiltered.any { it.playerData?.any { it.userData?.userId == currentUser?.userId } == true }


        if (!userInGame) {
            durakRepo.startGame(
                durakData = DurakData(title = title),
                rules = rules,
                entryPriceCash = entryPriceCash,
                entryPriceCoin = entryPriceCoin
            )
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.youAreOnOtherTable),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun putCardOnTable(
        selectedCard: CardPair,
        rotate: Boolean = false,
        changeAttacker: Boolean = false,
        placeOnTable: PlaceOnTable,
        perevodCards: List<CardPair> = emptyList(),
        onSuccess: () -> Unit
    ) {
        job?.cancel()
        buttonsState.value = false
        val durakData = durakDataState.value

        val yourCardIsKozr = selectedCard.suit == durakData?.kozrSuit

        val cardIsKozr = if (yourCardIsKozr) selectedCard.copy(
            number = selectedCard.number?.plus(15)
        ) else selectedCard

        durakRepo.putCardOnTable(
            selectedCard = cardIsKozr,
            rotate = rotate,
            changeAttacker = changeAttacker,
            placeOnTable = placeOnTable,
            perevodCards = perevodCards,
            onSuccess = onSuccess
        )
        viewModelScope.launch {
            delay(5000)
            buttonsState.value = true
        }
        job = timeDone()
    }


    private fun timeDone(): Job {

        val durakData = durakDataState.value
        val players = durakData?.playerData

        return viewModelScope.launch {
            delay(20000) // 10 seconds delay
            durakRepo.timeDone() {
                val startingPlayer =
                    players?.find { it.userData?.email == durakData.startingPlayer }

                val nextPlayer =
                    getNextPlayer(
                        allPlayers = players,
                        currentPlayer = startingPlayer ?: PlayerData()
                    )
                if (durakData?.attacker != null) {
                    finishGame(
                        winner = nextPlayer?.userData ?: UserData(),
                        loser = startingPlayer?.userData ?: UserData()
                    )
                }
            }
        }
    }


    fun putCardOnTableConditions(
        selectedCard: CardPair,
        cardOnTable: CardPair,
        context: Context,
        placeOnTable: PlaceOnTable,
        perevod: Boolean = false,
        onSuccess: () -> Unit
    ) {
        val currentUser =
            durakRepo.allUsers.value.find { it.userId == durakRepo.getCurrentUserId() }

        val durakData = durakDataState.value

        val selectedCardNumber = selectedCard.number ?: 0

        val kozrSuit = durakData?.kozrSuit

        val yourCardIsKozr = selectedCard.suit == kozrSuit
        val cardOnTableIsKozr = cardOnTable.suit == kozrSuit

        val cardCheckKozr = if (yourCardIsKozr) selectedCard.copy(
            number = selectedCard.number?.plus(15)
        ) else selectedCard

        val cardOnTableCheckKozr = if (cardOnTableIsKozr) cardOnTable.copy(
            number = cardOnTable.number?.plus(15)
        ) else cardOnTable

        val isAttacker = durakData?.attacker == currentUser?.email
        val isYourTurn = durakData?.startingPlayer == currentUser?.email
        val allSelected = durakData?.playerData?.flatMap { it.selectedCard ?: listOf() }

        val allSelectedCards =
            durakData?.playerData?.flatMap { it.selectedCard ?: emptyList() }
                ?: emptyList()

        val tableIsEmpty = allSelectedCards.isEmpty()

        val sameNumberOnTable = allSelected?.any {
            it.number == selectedCardNumber || it.number?.minus(15) == selectedCardNumber
        }
        val attackCardIsHigherThanTable =
            (cardCheckKozr.number ?: 0) > (cardOnTableCheckKozr.number ?: 0)

        val attackCardIsSameSuitWithTable = cardCheckKozr.suit == cardOnTableCheckKozr.suit

        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == currentUser?.userId }

        val currentSelected = currentPlayer?.selectedCard

        val selectedExceptCurrent =
            allSelected?.filterNot { currentSelected?.contains(it) == true } ?: emptyList()

        val isOneCardLeft =
            selectedExceptCurrent.size - (currentSelected?.size ?: 0) <= 1

        cardsNotAttacked.value = filterCardPairsWithoutAttack(placeOnTable)


        //Hücumcu sənsən. Sıra səndədir. Əlində 6 kart var. Yerdə kart yoxdur. Kozr var.
        if (isAttacker && isYourTurn && tableIsEmpty) {
            putCardOnTable(
                selectedCard = selectedCard,
                rotate = true,
                placeOnTable = placeOnTable,
                onSuccess = onSuccess
            )
        }
        //Hücumcu sənsən. Sıra səndədir. Yerdəki kartın rəqəmini düş. Kozr var
        else if (isAttacker && isYourTurn && sameNumberOnTable == true) {
            putCardOnTable(
                selectedCard = selectedCard,
                rotate = true,
                placeOnTable = placeOnTable,
                onSuccess = onSuccess
            )
        }
        //Hücumcu sənsən. Sıra səndə deyil. Eyni rəqəmli kartı düş. Kozr var
        else if (isAttacker && !isYourTurn && sameNumberOnTable == true) {
            val nextPlayer =
                getNextPlayer(
                    durakData.playerData,
                    currentPlayer ?: PlayerData()
                )
            if ((nextPlayer?.cards?.size ?: 0) <= cardsNotAttacked.value.size) {
                showRemainingCardsToast(
                    context,
                    nextPlayer?.cards?.size ?: 0
                )
            } else {
                putCardOnTable(
                    selectedCard = selectedCard,
                    rotate = false,
                    placeOnTable = placeOnTable,
                    onSuccess = onSuccess
                )
            }
        }
        //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var.
        else if (!isAttacker && isYourTurn && attackCardIsHigherThanTable && attackCardIsSameSuitWithTable) {
            if (isOneCardLeft) {
                putCardOnTable(
                    rotate = true,
                    selectedCard = selectedCard,
                    placeOnTable = placeOnTable,
                    onSuccess = onSuccess
                )
            } else {
                putCardOnTable(
                    rotate = false,
                    selectedCard = selectedCard,
                    placeOnTable = placeOnTable,
                    onSuccess = onSuccess
                )
            }
        }
        //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəki ilə eynidir. Perevod var
        else if (perevod && !isAttacker && isYourTurn && durakData?.rules?.perevod == true && currentSelected?.isEmpty() == true) {
            val nextPlayer =
                getNextPlayer(
                    allPlayers = durakData.playerData,
                    currentPlayer = currentPlayer
                )
            if ((nextPlayer?.cards?.size ?: 0) <= cardsNotAttacked.value.size) {
                showRemainingCardsToast(
                    context = context,
                    remainingCards = nextPlayer?.cards?.size ?: 0
                )
            } else {
                putCardOnTable(
                    rotate = true,
                    selectedCard = selectedCard,
                    changeAttacker = true,
                    placeOnTable = placeOnTable,
                    onSuccess = onSuccess,
                    perevodCards = allSelectedCards.plus(selectedCard)
                )
            }
        }
        //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu.
        else if (!isAttacker && isYourTurn && yourCardIsKozr && attackCardIsHigherThanTable) {
            if (isOneCardLeft) {
                putCardOnTable(
                    rotate = true,
                    selectedCard = selectedCard,
                    placeOnTable = placeOnTable,
                    onSuccess = onSuccess
                )
            } else {
                putCardOnTable(
                    rotate = false,
                    selectedCard = selectedCard,
                    placeOnTable = placeOnTable,
                    onSuccess = onSuccess
                )
            }
        }
    }


    fun takeCardsToHand() {
        val allSelectedCards = this.durakDataState.value?.selectedCards
        val selectedCards = allSelectedCards?.map {
            if (it.number!! > 15) {
                CardPair(number = it.number - 15, suit = it.suit)
            } else {
                CardPair(number = it.number, suit = it.suit)
            }
        }
        durakRepo.takeCardsToHand(
            selectedCards = selectedCards ?: listOf()
        )
        viewModelScope.launch {
            delay(1000)
            takeCardsAfterRound()
        }
    }

    fun getDurakData(gameId: String) {
        durakRepo.getDurakData(gameId)
    }

    fun getAttacker(): String? {
        return durakDataState.value?.attacker
    }


    fun sitDown(durakData: DurakData, playerData: PlayerData, tableNumber: Int) {
        durakRepo.sitDown(durakData, playerData, tableNumber) {
            val tableData = durakDataState.value?.tableData
            if (tableData?.firstTable != null && tableData.secondTable != null) {
                distributeCardsToPlayers()
            }
        }
    }

    fun standUp() {
        val durakData = durakDataState.value

        durakRepo.standUp(
            durakData = durakData ?: DurakData(),
        )
    }

    private fun updateDurakData(update: (DurakData) -> DurakData) {
        durakRepo.updateDurakData(update)
    }


    private fun distributeCardsToPlayers() {
        val durakData = durakDataState.value
        val cards = mutableStateOf(listOf<CardPair>())
        val remainingCards = mutableStateOf(listOf<CardPair>())
        val numPlayers = durakData?.playerData?.size ?: 0
        val cardsPerPlayer = when (numPlayers) {
            1 -> 6
            2 -> 6
            3 -> 6
            else -> 0
        }
        cards.value = durakData?.cards?.subList(0, cardsPerPlayer * numPlayers) ?: listOf()
        remainingCards.value = (durakData?.cards ?: listOf()) - cards.value.toSet()

        val lastCard = remainingCards.value.lastOrNull()

        if (durakData?.tableData?.secondTable != null && durakData.started != true) {
            durakRepo.distributeCardsToPlayers(
                durakData = durakData,
                playerData = durakData.playerData ?: listOf(),
                originalList = cards.value,
                kozr = lastCard ?: CardPair(),
                remainingCards = remainingCards.value,
                onComplete = { updatedRemainingCards ->
                    updateDurakData { durakData ->
                        durakData.copy(cards = updatedRemainingCards.toMutableList())
                    }
                },
                onSuccess = {
                    updateTurn()
                    job?.cancel()
                    job = timeDone()
                })
        }
    }


    private fun takeCardsAfterRound() {
        val mutableRemainingCards = remainingCardsRepo.value?.toMutableList()

        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == durakRepo.getCurrentUserId() }

        val currentPlayerCardSize = currentPlayer?.cards?.size

        val durakData = durakDataState.value
        val allPlayers = durakDataState.value?.playerData

        val cardsToTake =
            if ((currentPlayerCardSize
                    ?: 0) > 5
            ) emptyList() else remainingCardsRepo.value?.take(6 - (currentPlayerCardSize ?: 0))

        mutableRemainingCards?.removeAll(cardsToTake ?: emptyList())
        remainingCardsRepo.value = mutableRemainingCards
        val nextPlayer = getNextPlayer(allPlayers, currentPlayer ?: PlayerData())
        val nextPlayerCardSize = nextPlayer?.cards?.size

        val cardsToTakeNext =
            if ((nextPlayerCardSize
                    ?: 0) > 5
            ) emptyList() else remainingCardsRepo.value?.take(6 - (nextPlayerCardSize ?: 0))


        mutableRemainingCards?.removeAll(cardsToTakeNext ?: emptyList())
        remainingCardsRepo.value = mutableRemainingCards

        durakRepo.takeCardsAfterRound(
            durakData = durakData ?: DurakData(),
            player = currentPlayer ?: PlayerData(),
            nextPlayer = nextPlayer ?: PlayerData(),
            playerDataList = allPlayers ?: mutableListOf(),
            card = cardsToTake ?: emptyList(),
            nextPlayerCard = cardsToTakeNext ?: emptyList(),
            remainingCards = mutableRemainingCards ?: mutableListOf()
        )
    }


    fun passToBita() {
        durakRepo.passToBita()

        viewModelScope.launch {
            delay(1000)
            takeCardsAfterRound()
        }
    }

    fun deleteAllGames() {
        durakRepo.deleteAllGames()
    }

    fun startListeningForStartingPlayer(
        durakData: DurakData
    ) {
        durakRepo.startListeningForStartingPlayer(
            durakData = durakData,
            onChanged = {
                job?.cancel()
                job = timeDone()
            }
        )
    }

    fun startListeningForDurakUpdates(
        durakData: DurakData
    ) {
        durakRepo.startListeningForDurakUpdates(durakData)
    }


    private fun updateTurn() {
        val durakData = durakDataState.value
        val allPlayers = durakData?.playerData

        val kozrSuit = durakData?.kozrSuit

        allPlayers?.map { it.cards }?.let {
            val minNumbers = allPlayers.map { player ->
                player.cards?.filter { it.suit == kozrSuit }
                    ?.minByOrNull { it.number ?: 0 }?.number
                    ?: Int.MAX_VALUE
            }

            val startingPlayerIndex =
                minNumbers.indexOf(minNumbers.minOrNull())

            if (startingPlayerIndex != -1) {
                val startingPlayer = allPlayers[startingPlayerIndex]
                val startingPlayerUsername =
                    startingPlayer.userData?.email

                allPlayers.map {
                    it.cards?.forEach { card ->
                        if (durakData.cardsOnHands != true) {
                            if (kozrSuit == card.suit) {
                                durakRepo.setPlayerTurn(
                                    durakData = durakData,
                                    startingPlayer = startingPlayerUsername ?: "",
                                    starterTableNumber = startingPlayer.tableNumber ?: 0
                                ) {

                                }
                            }
                        }
                    }

                }
            }

        }
        //If no one has kozr then pick random player.
        viewModelScope.launch {
            delay(3000)
            val attacker = getAttacker()
            if (attacker == null) {
                val randomPlayer = allPlayers?.random()
                val randomPlayerUsername = randomPlayer?.userData?.email
                val randomPlayerTableNumber = randomPlayer?.tableNumber
                durakRepo.setPlayerTurn(
                    durakData = durakData ?: DurakData(),
                    startingPlayer = randomPlayerUsername ?: "",
                    starterTableNumber = randomPlayerTableNumber ?: 0
                ) {

                }
            }
        }
    }


    fun finishGame(winner: UserData, loser: UserData) {
        val durakData = durakDataState.value
        if (durakData?.started != null) {
            durakRepo.finishGame(
                loser = loser,
                winner = winner,
                durakData = durakData,
                onSuccess = {
                    job?.cancel()
                }
            )
            viewModelScope.launch {
                delay(8000)
                deleteGame()
            }
        }
    }


    private fun deleteGame() {
        val durakData = durakDataState.value
        if (durakData?.finished == true) {
            durakRepo.deleteGame(
                durakData = durakDataState.value ?: DurakData()
            )
        }
    }


    //Search durak tables
    fun searchList(query: String) {
        val listToSearch = if (isSearchStarting) {
            durakTables.value
        } else {
            initialDurakData
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                durakTables.value = initialDurakData
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.tableOwner?.email?.contains(
                    query.trim(),
                    ignoreCase = true
                ) == true || it.title!!.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
            if (isSearchStarting) {
                initialDurakData = durakTables.value
                isSearchStarting = false
            }
            durakTables.value = results
        }
    }

    fun clearSearch() {
        durakTables.value = initialDurakData
        isSearchStarting = true
    }


    private fun getNextPlayer(
        allPlayers: MutableList<PlayerData>?,
        currentPlayer: PlayerData
    ): PlayerData? {
        return allPlayers?.let { players ->
            val currentPlayerIndex = players.indexOf(currentPlayer)
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            players.getOrNull(nextPlayerIndex)
        }
    }


    private fun filterCardPairsWithoutAttack(placeOnTable: PlaceOnTable): List<CardPair> {
        return listOfNotNull(
            placeOnTable.first.takeIf { placeOnTable.firstAttack == null },
            placeOnTable.second.takeIf { placeOnTable.secondAttack == null },
            placeOnTable.third.takeIf { placeOnTable.thirdAttack == null },
            placeOnTable.fourth.takeIf { placeOnTable.fourthAttack == null },
            placeOnTable.fifth.takeIf { placeOnTable.fifthAttack == null },
            placeOnTable.sixth.takeIf { placeOnTable.sixthAttack == null }
        )
    }

    //    fun refreshCards(
//        cardAscending: Boolean
//    ) {
//        val currentUser = durakRepo.allUsers.value.find { it.userId == currentUserId }
//
//        val cards = currentPlayer.value?.cards ?: listOf()
//        val kozrSuit = durakDataState.value?.kozrSuit
//
//        val ascendingCard =
//            cards.sortedBy { if (it.suit == kozrSuit) it.number?.plus(15) else it.number }
////        val shuffledCard = cards.shuffled()
//
//        durakRepo.refreshCards(
//            durakData = durakDataState.value ?: DurakData(),
//            userData = it.copy(
//                durakSettings = it.durakSettings?.copy(
//                    cardAscending = cardAscending
//                )
//            ),
//            playerData = currentPlayer.value ?: PlayerData(),
//            card = if (cardAscending) ascendingCard else cards,
//            onSort = {
//
//            }
//        )
//    }
}