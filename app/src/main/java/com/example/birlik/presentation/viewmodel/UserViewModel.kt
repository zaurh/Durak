package com.example.birlik.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.PlaceOnTable
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.data.remote.durak.Rules
import com.example.birlik.data.repository.UserRepo
import com.example.birlik.presentation.screen.showRemainingCardsToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,

    ) : ViewModel() {

    val userDataState = userRepo.userData

    val durakDataState = userRepo.durak

    val remainingCardsRepo = userRepo.remainingCards

    var cardsNotAttacked = mutableStateOf<List<CardPair?>>(listOf())





    init {
        viewModelScope.launch {
            userRepo.durak.collect {
                if (it?.cardsOnHands == null){
                    oyuncularaKartPayla()
                    delay(1000)
                    updateOyuncuSirasi()
                }
            }
        }
    }

    val playerData = userRepo.playerData

    val onlineUsers = userRepo.onlineUsers

    val durakTables = userRepo.durakTables

    val allUsers = userRepo.usersData
    val players = userRepo.players


    private val _manyCardLeft = MutableStateFlow(false)
    val manyCardLeft: StateFlow<Boolean> = _manyCardLeft

    private val _selectedCard = MutableStateFlow<CardPair?>(null)
    val selectedCardState: StateFlow<CardPair?> = _selectedCard

    private var isSearchStarting = true
    private var initialDurakData = listOf<DurakData>()

    fun setSelectedCard(card: CardPair?) {
        _selectedCard.value = card
    }

    fun clearSelectedCard() {
        _selectedCard.value = null
    }

    fun setManyCardLeft() {
        viewModelScope.launch {
            _manyCardLeft.value = true
            delay(1000L)
            _manyCardLeft.value = false
        }
    }


    fun updateUserData(
        userData: UserData
    ) {
        userRepo.updateUser(
            userData
        )
    }

    fun rewardUser(
        userData: UserData
    ) {
        userRepo.rewardUser(userData)
    }

    fun getUserData(userId: String) {
        userRepo.getUserData(userId = userId)
    }


    fun oyunuBaslat(
        title: String,
        rules: Rules,
        entryPriceCash: Long,
        entryPriceCoin: Long,
        context: Context
    ) {
        val userInGame =
            durakTables.value.any { it.playerData?.any { it.userData?.userId == userDataState.value?.userId } == true }

        if (!userInGame) {
            userRepo.oyunuBaslat(
                durakData = DurakData(title = title),
                tableOwner = userDataState.value ?: UserData(),
                rules = rules,
                entryPriceCash = entryPriceCash,
                entryPriceCoin = entryPriceCoin
            )
        } else {
            Toast.makeText(context, "Başqa stolda oyun gedir.", Toast.LENGTH_SHORT).show()
        }

    }

    fun yereKartDus(
        rotate: Boolean = false,
        changeAttacker: Boolean = false,
        attack: Boolean = false,
        firstMove: Boolean = false
    ){
        val durakData = durakDataState.value
        val placeOnTable = durakData?.placeOnTable
        val selectedCard = selectedCardState.value

        val yourCardIsKozr = selectedCard?.suit == durakData?.kozrSuit

        val cardIsKozr = if (yourCardIsKozr) selectedCard?.copy(
            number = selectedCard.number?.plus(15)
        ) else selectedCard


        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == userDataState.value?.userId }

        val newPlaceOnTableAttack = placeOnTable?.copy(
            firstAttack = placeOnTable.firstAttack ?: cardIsKozr,
            secondAttack = if (placeOnTable.firstAttack != null && placeOnTable.secondAttack == null) cardIsKozr else placeOnTable.secondAttack,
            thirdAttack = if (placeOnTable.secondAttack != null && placeOnTable.thirdAttack == null) cardIsKozr else placeOnTable.thirdAttack,
            fourthAttack = if (placeOnTable.thirdAttack != null && placeOnTable.fourthAttack == null) cardIsKozr else placeOnTable.fourthAttack,
            fifthAttack = if (placeOnTable.fourthAttack != null && placeOnTable.fifthAttack == null) cardIsKozr else placeOnTable.fifthAttack,
            sixthAttack = if (placeOnTable.fifthAttack != null && placeOnTable.sixthAttack == null) cardIsKozr else placeOnTable.sixthAttack
        )

        var newPlaceOnTable = placeOnTable?.copy(
            second = if (placeOnTable.first != null && placeOnTable.second == null) cardIsKozr else placeOnTable.second,
            third = if (placeOnTable.second != null && placeOnTable.third == null) cardIsKozr else placeOnTable.third,
            fourth = if (placeOnTable.third != null && placeOnTable.fourth == null) cardIsKozr else placeOnTable.fourth,
            fifth = if (placeOnTable.fourth != null && placeOnTable.fifth == null) cardIsKozr else placeOnTable.fifth,
            sixth = if (placeOnTable.fifth != null && placeOnTable.sixth == null) cardIsKozr else placeOnTable.sixth
        )

        val newPlace =
            if (firstMove) PlaceOnTable(first = cardIsKozr)
            else if (attack) newPlaceOnTableAttack
        else newPlaceOnTable

        userRepo.yereKartDus(
            durakData = durakDataState.value ?: DurakData(),
            playerData = currentPlayer ?: PlayerData(),
            selectedCard = selectedCard ?: CardPair(),
            rotate = rotate,
            changeAttacker = changeAttacker,
            placeOnTable = newPlace ?: PlaceOnTable(),
            perevodKartlari = cardsNotAttacked.value.plus(cardIsKozr) as List<CardPair>
        )
    }

    fun yereKartDusConditions(
        context: Context,
        onPerevodAlert: () -> Unit = {},
        perevodKartlari: List<CardPair> = listOf()
    ) {
        val currentUsername = userDataState.value?.username
        val durakData = durakDataState.value

        val selectedCard = selectedCardState.value

        val isAttacker = durakData?.attacker == currentUsername
        val isYourTurn = durakData?.startingPlayer == currentUsername
        val sixCardsOnHands = durakData?.playerData?.all {
            (it.cards?.size ?: 0) >= 6
        }
        val allSelected = durakData?.playerData?.flatMap { it.selectedCard ?: listOf() }

        val allSelectedCards =
            durakData?.playerData?.flatMap { it.selectedCard ?: emptyList() }
                ?: emptyList()
        val tableIsEmpty = allSelectedCards.isEmpty()
        val kozr = durakData?.kozr
        val lastCardOnTable = durakData?.selectedCards?.lastOrNull()

        val sameNumberOnTable = allSelected?.any {
            it.number == selectedCard?.number || it.number?.minus(
                15
            ) == selectedCard?.number
        }
        val attackCardIsHigherThanTable =
            if (selectedCard?.suit == durakData?.kozrSuit) (selectedCard?.number?.plus(
                15
            ) ?: 0) > (lastCardOnTable?.number ?: 0) else (selectedCard?.number
                ?: 0) > (lastCardOnTable?.number ?: 0)

        val attackCardIsSameSuitWithTable = selectedCard?.suit == lastCardOnTable?.suit

        val userId = userDataState.value?.userId
        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == userId }

        val currentSelected = currentPlayer?.selectedCard


        val selectedExceptCurrent =
            allSelected?.filterNot { currentSelected?.contains(it) == true } ?: emptyList()

        val lastCardOnTableExceptCurrent = selectedExceptCurrent.lastOrNull()


        val isOneCardLeft = selectedExceptCurrent.size - (currentSelected?.size ?: 0) <= 1

        val cardNumberIsSameWithTable =
            selectedCard?.number == lastCardOnTableExceptCurrent?.number || selectedCard?.number == durakData?.selectedCards?.lastOrNull()?.number?.minus(
                15
            )

        //
//        cardsNotAttacked.value = generateCardPairList(placeOnTable ?: PlaceOnTable())



        val kozrSuit = durakData?.kozrSuit
        val yourCardIsKozr = selectedCard?.suit == kozrSuit

        val cardIsKozr = if (yourCardIsKozr) selectedCard?.copy(
            number = selectedCard.number?.plus(15)
        ) else selectedCard



        //Conditions
        var rotate = false
        var changeAttacker = false
        var newPlaceOnAttack = false
        var perevodAlert = false

        //Hücumcu sənsən. Sıra səndədir. Əlində 6 kart var. Yerdə kart yoxdur. Kozr var.
        if (isAttacker && isYourTurn && sixCardsOnHands == true && tableIsEmpty && kozr != null) {
            yereKartDus(
                rotate = true,
                firstMove = true
            )
//            rotate = true
//            newPlaceOnTable = PlaceOnTable(first = cardIsKozr)
        }
        //Hücumcu sənsən. Sıra səndədir. Yerdə kart yoxdur. Kozr götürülüb.
        else if (isAttacker && isYourTurn && tableIsEmpty && kozr == null) {
            yereKartDus(
                rotate = true,
                firstMove = true
            )
//            rotate = true
//            newPlaceOnTable = PlaceOnTable(first = cardIsKozr)
        }
        //Hücumcu sənsən. Sıra səndədir. Yerdəki kartın rəqəmini düş. Kozr var
        else if (isAttacker && isYourTurn && sameNumberOnTable == true) {
            yereKartDus(rotate = true)
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
                yereKartDus(rotate = false)
            }
        }
        //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
        else if (!isAttacker && isYourTurn && attackCardIsHigherThanTable && attackCardIsSameSuitWithTable) {
            if (!isOneCardLeft) {
                Toast.makeText(context, "Cox kart var", Toast.LENGTH_SHORT)
                    .show()
                setManyCardLeft()
            } else {
                yereKartDus(rotate = true, attack = true)
            }
        }
        //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
        else if (!isAttacker && isYourTurn && yourCardIsKozr && attackCardIsHigherThanTable) {
            if (durakData?.rules?.perevod == true && cardNumberIsSameWithTable && currentSelected?.isEmpty() == true) {
                onPerevodAlert()
                perevodAlert = true
            } else if (!isOneCardLeft) {
                Toast.makeText(
                    context,
                    "Vuracağın kartı seç.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                setManyCardLeft()
            }
            else {
                yereKartDus(rotate = true, attack = true)
            }
        }
        //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəki ilə eynidir. Perevod var . Kozr var
        else if (!isAttacker && isYourTurn && cardNumberIsSameWithTable && durakData?.rules?.perevod == true && currentSelected?.isEmpty() == true) {
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
                yereKartDus(rotate = true, changeAttacker = true)

//                                        userViewModel.yereKartDus(
//                                            changeAttacker = true,
//                                            selectedCard = cardIsKozr ?: CardPair(),
//                                            rotate = true,
//                                            placeOnTable = newPlaceOnTable ?: PlaceOnTable(),
//                                            perevodKartlari = cardsNotAttacked.plus(cardIsKozr) as List<CardPair>
//                                        )
            }
        }
//        if (!perevodAlert){
//            userRepo.yereKartDus(
//                durakData = durakDataState.value ?: DurakData(),
//                playerData = currentPlayer ?: PlayerData(),
//                selectedCard = selectedCard ?: CardPair(),
//                rotate = rotate,
//                changeAttacker,
//                placeOnTable = if (newPlaceOnAttack) newPlaceOnTableAttack ?: PlaceOnTable() else newPlaceOnTable ?: PlaceOnTable(),
//                perevodKartlari = cardsNotAttacked.value.plus(cardIsKozr) as List<CardPair>
//            )
//        }
    }

    fun attackFirst(
        durakData: DurakData,
        placeOnTable: PlaceOnTable
    ) {
        userRepo.attackFirst(durakData, placeOnTable)
    }

    fun eleYig() {
        val userId = userDataState.value?.userId
        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == userId }

        val allSelectedCards = this.durakDataState.value?.selectedCards

        val selectedCards = allSelectedCards?.map {
            if (it.number!! > 15) {
                CardPair(number = it.number - 15, suit = it.suit)
            } else {
                CardPair(number = it.number, suit = it.suit)
            }
        }

        userRepo.eleYig(
            durakData = durakDataState.value ?: DurakData(),
            playerData = currentPlayer ?: PlayerData(),
            selectedCards = selectedCards ?: listOf()
        )
        viewModelScope.launch {
            delay(1000)
            yerdenKartGotur()
        }
    }

    fun getDurakData(gameId: String) {
        userRepo.getDurakData(gameId)
    }

    fun stolaOtur(durakData: DurakData, playerData: PlayerData, tableNumber: Int) {
        userRepo.stolaOtur(durakData, playerData, tableNumber)
    }

    fun stoldanQalx(durakData: DurakData, userId: String) {
        userRepo.stoldanQalx(durakData, userId)
    }

    fun updateDurakData(update: (DurakData) -> DurakData) {
        userRepo.updateDurakData(update)
    }

    fun updateDurakCards(durakData: DurakData, cards: MutableList<CardPair>) {
        userRepo.updateDurakCards(durakData, cards)
    }

    fun kozrGotur(durakData: DurakData) {
        userRepo.kozrGotur(durakData)
    }

    fun oyuncularaKartPayla() {
        val durakData = durakDataState.value
        val cards = mutableStateOf(listOf<CardPair>())
        val remainingCards = mutableStateOf(listOf<CardPair>())
        val shuffled = durakData?.cards?.shuffled(Random)
        val numPlayers = durakData?.playerData?.size ?: 0
        val cardsPerPlayer = when (numPlayers) {
            1 -> 6
            2 -> 6 // Each player gets 6 cards for a total of 12
            3 -> 6 // Each player gets 6 cards for a total of 18
            else -> 0 // Handle other cases as needed
        }
        cards.value = shuffled?.subList(0, cardsPerPlayer * numPlayers) ?: listOf()
        remainingCards.value = (shuffled ?: listOf()) - cards.value.toSet()

        val lastCard = remainingCards.value.lastOrNull()

        val modifiedRemainingCards = if (lastCard != null) {
            remainingCards.value.toMutableList().apply {
                remove(lastCard)
            }
        } else {
            remainingCards.value
        }
        println("durakdata -> " + durakData)
        if (durakData?.tableData?.secondTable != null && durakData.started != true) {
            userRepo.oyuncularaKartPayla(
                durakData = durakData,
                playerData = durakData.playerData ?: listOf(),
                originalList = cards.value,
                kozr = lastCard ?: CardPair(),
                remainingCards = modifiedRemainingCards,
                onComplete = { updatedRemainingCards ->
                    updateDurakData { durakData ->
                        durakData.copy(cards = updatedRemainingCards.toMutableList())
                    }
                },
                onSuccess = {

                })
        }
    }


    fun yerdenKartGotur() {
        val mutableRemainingCards = remainingCardsRepo.value?.toMutableList()

        val userId = userDataState.value?.userId

        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == userId }

        val currentPlayerCardSize = currentPlayer?.cards?.size

        val durakData = durakDataState.value
        val allPlayers = durakDataState.value?.playerData

        val cardsToTake =
            if ((currentPlayerCardSize ?: 0) > 5) emptyList() else remainingCardsRepo.value?.takeLast(6 - (currentPlayerCardSize ?: 0))

        mutableRemainingCards?.removeAll(cardsToTake ?: emptyList())
        remainingCardsRepo.value = mutableRemainingCards
        val nextPlayer = getNextPlayer(allPlayers, currentPlayer ?: PlayerData())
        val nextPlayerCardSize = nextPlayer?.cards?.size

        val cardsToTakeNext =
            if ((nextPlayerCardSize ?: 0) > 5) emptyList() else remainingCardsRepo.value?.takeLast(6 - (nextPlayerCardSize ?: 0))


        mutableRemainingCards?.removeAll(cardsToTakeNext ?: emptyList())
        remainingCardsRepo.value = mutableRemainingCards


        userRepo.yerdenKartGotur(
            durakData = durakData ?: DurakData(),
            player = currentPlayer ?: PlayerData(),
            nextPlayer = nextPlayer ?: PlayerData(),
            playerDataList = allPlayers ?: mutableListOf(),
            card = cardsToTake ?: emptyList(),
            nextPlayerCard = cardsToTakeNext ?: emptyList(),
            remainingCards = mutableRemainingCards ?: mutableListOf()
        )

//                        userViewModel.kozrGotur(durakData)
    }


    fun bitayaGetsin() {
        val selectedCards =
            durakDataState.value?.playerData?.flatMap { it.selectedCard ?: listOf() }

        userRepo.bitayaGetsin(
            durakData = durakDataState.value ?: DurakData(),
            playerData = durakDataState.value?.playerData ?: mutableListOf(),
            cards = selectedCards ?: listOf()
        )
        viewModelScope.launch {
            delay(1000)
            yerdenKartGotur()
        }
    }

    fun deleteAllGames() {
        userRepo.deleteAllGames()
    }

    fun startListeningForDurakUpdates(
        durakData: DurakData
    ) {
        userRepo.startListeningForDurakUpdates(durakData)
    }

    fun updateOyuncuSirasi() {
        val durakData = durakDataState.value
        val allPlayers = durakData?.playerData

        val kozrSuit = durakData?.kozrSuit
        println("kozrSuit" + kozrSuit)
        println("allplayers -> " + allPlayers)

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
                    startingPlayer.userData?.username
                println("durakData -> " + durakData)
                println("startingPlayerUsername -> " + startingPlayerUsername)
                println("startingPlayerTableNumber -> " + startingPlayer.tableNumber)
                println("allPlayerCards -> " + allPlayers)

                allPlayers.map {
                    it.cards?.forEach { card ->
                        println("cardSuit -> " + card.suit)
                        if (kozrSuit == card.suit) {
                            if (durakData.cardsOnHands != true) {
                                userRepo.updateOyuncuSirasi(
                                    durakData = durakData,
                                    startingPlayer = startingPlayerUsername ?: "",
                                    starterTableNumber = startingPlayer.tableNumber ?: 0
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    fun loseGame(durakData: DurakData, loser: UserData, winner: UserData, onSuccess: () -> Unit) {
        userRepo.loseGame(durakData, loser, winner, onSuccess)
    }

    fun deleteGame(durakData: DurakData) {
        userRepo.deleteGame(durakData)
    }


    fun refreshCards(
        durakData: DurakData,
        userData: UserData,
        playerData: PlayerData,
        card: List<CardPair>
    ) {
        val userId = userDataState.value?.userId

        val currentPlayer =
            this.durakDataState.value?.playerData?.find { it.userData?.userId == userId }

        println("currentPlayer: " + currentPlayer)
        println("userID: " + userId)
        println("durakData: " + this.durakDataState.value)
        println("playerId: " + this.durakDataState.value?.playerData?.map { it.playerId })
        userRepo.refreshCards(durakData, userData, playerData, card)
    }

    fun changeSkin(userData: UserData) {
        userRepo.changeSkin(userData)
    }

//    fun buyNewSkin(userData: UserData, skin: Skin) {
//        userRepo.buyNewSkin(userData, skin)
//    }


    var downloadedPercentage = MutableLiveData<Float>()

    fun startThreadGradient(onComplete: () -> Unit) {
        setTimer(onComplete)
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                var initial = 0f

                while (true) {
                    initial += 10f
                    if (initial < 100) {
                        withContext(Dispatchers.Main) {
                            downloadedPercentage.value = initial
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            downloadedPercentage.value = 100f
                        }
                        break
                    }
                    delay(1000)
                }
            }
            resetCountdown()

        }
    }

    fun resetCountdown() {
        downloadedPercentage.value = 0f
    }

    private var timerJob: Job? = null

    private fun setTimer(onComplete: () -> Unit) {
        timerJob?.cancel() // Cancel the previous job if it exists

        timerJob = viewModelScope.launch {
            try {
                delay(5.seconds)
                withContext(NonCancellable) {
                    userRepo.setTimer(durakDataState.value ?: DurakData(), 100, onComplete = {
                        onComplete()
                        println("onComplete worked")
                    })
                }
            } catch (e: CancellationException) {
                println("Coroutine was cancelled before onComplete()")
            }
        }
    }

    //Search Users
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
                it.tableOwner?.username?.contains(
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


    fun getNextPlayer(
        allPlayers: MutableList<PlayerData>?,
        currentPlayer: PlayerData
    ): PlayerData? {
        return allPlayers?.let { players ->
            val currentPlayerIndex = players.indexOf(currentPlayer)
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            players.getOrNull(nextPlayerIndex)
        }
    }

    fun generateCardPairList(placeOnTable: PlaceOnTable): List<CardPair> {
        val cardPairList = mutableListOf<CardPair>()

        if (placeOnTable.firstAttack == null) {
            placeOnTable.first?.let { cardPairList.add(it) }
        }

        if (placeOnTable.secondAttack == null) {
            placeOnTable.second?.let { cardPairList.add(it) }
        }

        if (placeOnTable.thirdAttack == null) {
            placeOnTable.third?.let { cardPairList.add(it) }
        }

        if (placeOnTable.fourthAttack == null) {
            placeOnTable.fourth?.let { cardPairList.add(it) }
        }

        if (placeOnTable.fifthAttack == null) {
            placeOnTable.fifth?.let { cardPairList.add(it) }
        }

        if (placeOnTable.sixthAttack == null) {
            placeOnTable.sixth?.let { cardPairList.add(it) }
        }

        return cardPairList
    }

}
