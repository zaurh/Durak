package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.presentation.screen.components.durak.PlayerCards
import com.example.birlik.presentation.screen.components.durak.TableDesign
import com.example.birlik.presentation.screen.components.durak.UserItem
import com.example.birlik.presentation.viewmodel.CircularProgressViewModel
import com.example.birlik.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun DurakGame(
    navController: NavController,
    userViewModel: UserViewModel,
    circularViewModel: CircularProgressViewModel = hiltViewModel(),
    durakData: DurakData
) {

    var durakSettingsAlert by remember { mutableStateOf(false) }
    var perevodAlert by remember { mutableStateOf(false) }
    var flagAlert by remember { mutableStateOf(false) }
    var loserAlert by remember { mutableStateOf(false) }


    val durak = userViewModel.durakDataState.collectAsState()
    val userData = userViewModel.userDataState.collectAsState()
    val selectedCardViewModel = userViewModel.selectedCardState.collectAsState()


    val remainingCardsViewModel = userViewModel.remainingCardsRepo.observeAsState(listOf())
    var remainingCards by remember { mutableStateOf(listOf<CardPair>()) }


    val currentPlayer =
        durak.value?.playerData?.find { it.userData?.username == userData.value?.username }
    val allPlayers = durak.value?.playerData



    val startingPlayer = durak.value?.startingPlayer


    val kozrSuit = durak.value?.kozrSuit


    var cardsNotAttacked by remember {
        mutableStateOf<List<CardPair?>>(listOf())
    }
    val scope = rememberCoroutineScope()


    LaunchedEffect(remainingCardsViewModel.value, durak.value) {
        userViewModel.getDurakData(durakData.gameId ?: "")
        durak.value?.let {
            userViewModel.startListeningForDurakUpdates(it)

        }
        remainingCardsViewModel.value?.let {
            remainingCards = it
        }
        userViewModel.clearSelectedCard()
    }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            println("onDispose :")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = colorResource(id = R.color.dark_grey)) {
        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(
                    id = durak.value?.tableOwner?.skinSettings?.backgroundPicked?.image
                        ?: R.drawable.background_green
                ),
                contentDescription = ""
            )
            Scaffold(containerColor = colorResource(id = R.color.transparent), topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        colorResource(id = R.color.transparent)
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                        }
                    },
                    title = { Text(text = "aaa", modifier = Modifier.clickable {

                    }) },
                    actions = {
                        if (durak.value?.started == true) {
                            IconButton(onClick = {
                                flagAlert = true
                            }) {
                                Icon(imageVector = Icons.Default.Flag, contentDescription = "")
                            }
                        }

                        if (durak.value?.rules?.perevod == true) {
                            Icon(imageVector = Icons.Default.Recycling, contentDescription = "")
                        }
                        IconButton(onClick = {
                            durakSettingsAlert = true
                        }) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                        }
                    })
            }, content = { it ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LaunchedEffect(durak.value?.finished) {
                            if (allPlayers?.any { it.cards?.size == 0 } == true && remainingCardsViewModel.value.isEmpty() && durak.value?.started == true) {
                                val nextPlayer =
                                    getNextPlayer(allPlayers, currentPlayer ?: PlayerData())

                                userViewModel.loseGame(
                                    durakData,
                                    nextPlayer?.userData ?: UserData(),
                                    userData.value ?: UserData()
                                ) {
                                    loserAlert = true
                                }
                            } else if (durak.value?.finished == true) {
                                loserAlert = true
                            }
                        }

                        if (loserAlert) {
                            AlertDialog(
                                title = { Text(text = "${durak.value?.loser?.username} lose.") },
                                confirmButton = {
                                    Button(onClick = {
//                                winnerAlert = false
//                                navController.popBackStack()
                                    }) {
                                        Text(text = "Close")
                                    }
                                },
                                onDismissRequest = {
//                            winnerAlert = false
//                            navController.popBackStack()
                                })
                            LaunchedEffect(key1 = true) {
                                scope.launch {
                                    delay(3000)
                                    userViewModel.deleteGame(durakData)
                                    navController.popBackStack()
                                }
                            }

                        }

                        if (flagAlert) {
                            FlagAlert(onDismiss = { flagAlert = false }, beli = {
                                val nextPlayer =
                                    getNextPlayer(allPlayers, currentPlayer ?: PlayerData())

                                userViewModel.loseGame(
                                    durakData,
                                    currentPlayer?.userData ?: UserData(),
                                    nextPlayer?.userData ?: UserData()
                                ) {

                                }
                            }, xeyr = {
                                flagAlert = false
                            })
                        }
                        if (durakSettingsAlert) {
                            DurakSettingsAlert(
                                closeAlert = {
                                    durakSettingsAlert = false
                                }, userViewModel = userViewModel
                            )
                        }

                    }
                    LazyColumn {
                        items(1) {


                            UserItem(
                                userData = userData.value ?: UserData(),
                                durakData = durak.value ?: DurakData(),
                                onGetCard = {
//                                        val currentPlayerCard = currentPlayer?.cards?.size
//                                        val singleCard =
//                                            remainingCards.takeLast(6 - (currentPlayerCard ?: 0))


//                                        val mutableRemainingCards = remainingCards.toMutableList()
//                                        mutableRemainingCards.removeAll(singleCard)

//                                        remainingCards = mutableRemainingCards

//                                        userViewModel.updateDurakCards(
//                                            durakData = durak.value ?: DurakData(),
//                                            cards = mutableRemainingCards
//                                        )

//                                        userViewModel.yerdenKartGotur(durakData = durak.value
//                                            ?: DurakData(),
//                                            player = currentPlayer ?: PlayerData(),
//                                            playerData = allPlayers ?: listOf(),
//                                            card = singleCard.ifEmpty { listOfNotNull(durak.value?.kozr) })

//                                isFunctionEnabled = false
//
//                                // Enable the function after 1 second using a coroutine
//                                scope.launch {
//                                    delay(1000L)
//                                    isFunctionEnabled = true
//                                }

                                },
                                userViewModel = userViewModel,
                                navContoller = navController
                            )
                        }
                    }
                }
            }, bottomBar = {
                val context = LocalContext.current
                val currentUsername = userData.value?.username
                val attacker = durak.value?.attacker
                val isAttacker = attacker == currentUsername
                val isYourTurn = startingPlayer == currentUsername
                val sixCardsOnHands = allPlayers?.all {
                    (it.cards?.size ?: 0) >= 6
                }

                val allSelected = allPlayers?.flatMap { it.selectedCard ?: listOf() }

                val currentSelected = currentPlayer?.selectedCard

                val selectedExceptCurrent =
                    allSelected?.filterNot { currentSelected?.contains(it) == true } ?: emptyList()

                val isOneCardLeft = selectedExceptCurrent.size - (currentSelected?.size ?: 0) <= 1

                val onTableSelected = durak.value?.selectedCards

                val lastCardOnTable = onTableSelected?.lastOrNull()
                val lastCardOnTableExceptCurrent = selectedExceptCurrent.lastOrNull()

                val allSelectedCards =
                    durak.value?.playerData?.flatMap { it.selectedCard ?: emptyList() }
                        ?: emptyList()
                val tableIsEmpty = allSelectedCards.isEmpty()
                val kozr = durak.value?.kozr
                val selectedCard = selectedCardViewModel.value

                val sameNumberOnTable = allSelected?.any {
                    it.number == selectedCard?.number || it.number?.minus(
                        15
                    ) == selectedCard?.number
                }

                val placeOnTable = durak.value?.placeOnTable

//                cardsNotAttacked = generateCardPairList(placeOnTable ?: PlaceOnTable())
                println(cardsNotAttacked)

                val cardNumberIsSameWithTable =
                    selectedCard?.number == lastCardOnTableExceptCurrent?.number || selectedCard?.number == onTableSelected?.lastOrNull()?.number?.minus(
                        15
                    )


                val perevod = durak.value?.rules?.perevod


                val attackCardIsHigherThanTable =
                    if (selectedCard?.suit == kozrSuit) (selectedCard?.number?.plus(
                        15
                    ) ?: 0) > (lastCardOnTable?.number ?: 0) else (selectedCard?.number
                        ?: 0) > (lastCardOnTable?.number ?: 0)

                val attackCardIsSameSuitWithTable = selectedCard?.suit == lastCardOnTable?.suit

//                val cardIsKozr = if (yourCardIsKozr) selectedCard?.copy(
//                    number = selectedCard.number?.plus(15)
//                ) else selectedCard

//                val newPlaceOnTableAttack = placeOnTable?.copy(
//                    firstAttack = placeOnTable.firstAttack ?: cardIsKozr,
//                    secondAttack = if (placeOnTable.firstAttack != null && placeOnTable.secondAttack == null) cardIsKozr else placeOnTable.secondAttack,
//                    thirdAttack = if (placeOnTable.secondAttack != null && placeOnTable.thirdAttack == null) cardIsKozr else placeOnTable.thirdAttack,
//                    fourthAttack = if (placeOnTable.thirdAttack != null && placeOnTable.fourthAttack == null) cardIsKozr else placeOnTable.fourthAttack,
//                    fifthAttack = if (placeOnTable.fourthAttack != null && placeOnTable.fifthAttack == null) cardIsKozr else placeOnTable.fifthAttack,
//                    sixthAttack = if (placeOnTable.fifthAttack != null && placeOnTable.sixthAttack == null) cardIsKozr else placeOnTable.sixthAttack
//                )
//
//                val newPlaceOnTable = placeOnTable?.copy(
//                    second = if (placeOnTable.first != null && placeOnTable.second == null) cardIsKozr else placeOnTable.second,
//                    third = if (placeOnTable.second != null && placeOnTable.third == null) cardIsKozr else placeOnTable.third,
//                    fourth = if (placeOnTable.third != null && placeOnTable.fourth == null) cardIsKozr else placeOnTable.fourth,
//                    fifth = if (placeOnTable.fourth != null && placeOnTable.fifth == null) cardIsKozr else placeOnTable.fifth,
//                    sixth = if (placeOnTable.fifth != null && placeOnTable.sixth == null) cardIsKozr else placeOnTable.sixth
//                )

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    if (perevodAlert) {
                        PerevodAlert(onDismiss = {
                            perevodAlert = false
                        }, perevod = {
//                            val nextPlayer =
//                                getNextPlayer(allPlayers, currentPlayer ?: PlayerData())
//                            if ((nextPlayer?.cards?.size ?: 0) <= cardsNotAttacked.size) {
//                                showRemainingCardsToast(context, nextPlayer?.cards?.size ?: 0)
//                            } else {
////                                userViewModel.yereKartDus(
////                                    changeAttacker = true,
////                                    selectedCard = cardIsKozr ?: CardPair(),
////                                    rotate = true,
////                                    placeOnTable = newPlaceOnTable ?: PlaceOnTable(),
////                                    perevodKartlari = cardsNotAttacked.plus(cardIsKozr) as List<CardPair>
////                                )
//                            }
                            userViewModel.yereKartDus(
                                rotate = true,
                                changeAttacker = true
                            )
                            perevodAlert = false

                        }, vur = {
                            if (!isOneCardLeft) {
                                Toast.makeText(context, "Vuracağın kartı seç.", Toast.LENGTH_SHORT)
                                    .show()
                                userViewModel.setManyCardLeft()
                            } else {
                                userViewModel.yereKartDus(
                                    rotate = true,
                                    attack = true
                                )
//                                userViewModel.yereKartDus(
//                                    selectedCard = cardIsKozr ?: CardPair(),
//                                    rotate = true,
//                                    placeOnTable = newPlaceOnTableAttack ?: PlaceOnTable()
//                                )
                            }
                            perevodAlert = false
                        })
                    }

                    if (selectedCard != null) {
                        Button(onClick = {
                            userViewModel.yereKartDusConditions(
                                context = context,
                                onPerevodAlert = {perevodAlert = true}
                            )
                        }) {
                            Text(text = "dus")
                        }
                        var yereDusState by remember { mutableStateOf(true) }
                        if (yereDusState) {
                            Button(onClick = {
                                //Hücumcu sənsən. Sıra səndədir. Yerdə kart yoxdur. Kozr var
                                if (isAttacker && isYourTurn && sixCardsOnHands == true && tableIsEmpty && kozr != null) {
//                                    userViewModel.yereKartDus(
//                                        cardIsKozr ?: CardPair(),
//                                        true,
//                                        placeOnTable = PlaceOnTable(first = cardIsKozr)
//                                    )
                                } else if (isAttacker && isYourTurn && tableIsEmpty && kozr == null) {
//                                    userViewModel.yereKartDus(
//                                        cardIsKozr ?: CardPair(),
//                                        true,
//                                        placeOnTable = PlaceOnTable(first = cardIsKozr)
//                                    )
                                }
                                //Hücumcu sənsən. Sıra səndədir. Yerdəki kartın rəqəmini düş. Kozr var
                                else if (isAttacker && isYourTurn && sameNumberOnTable == true) {
//                                    userViewModel.yereKartDus(
//                                        cardIsKozr ?: CardPair(),
//                                        true,
//                                        placeOnTable = newPlaceOnTable ?: PlaceOnTable()
//                                    )
                                }
                                //Hücumcu sənsən. Sıra səndə deyil. Eyni rəqəmli kartı düş. Kozr var
                                else if (isAttacker && !isYourTurn && sameNumberOnTable == true) {
                                    val nextPlayer =
                                        getNextPlayer(allPlayers, currentPlayer ?: PlayerData())
                                    if ((nextPlayer?.cards?.size ?: 0) <= cardsNotAttacked.size) {
                                        showRemainingCardsToast(
                                            context,
                                            nextPlayer?.cards?.size ?: 0
                                        )
                                    } else {
//                                        userViewModel.yereKartDus(
//                                            cardIsKozr ?: CardPair(),
//                                            false,
//                                            placeOnTable = newPlaceOnTable ?: PlaceOnTable()
//                                        )
                                    }
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                else if (!isAttacker && isYourTurn && attackCardIsHigherThanTable && attackCardIsSameSuitWithTable) {
                                    if (!isOneCardLeft) {
                                        Toast.makeText(context, "Cox kart var", Toast.LENGTH_SHORT)
                                            .show()
                                        userViewModel.setManyCardLeft()
                                    } else {
//                                        userViewModel.yereKartDus(
//                                            selectedCard = cardIsKozr ?: CardPair(),
//                                            rotate = true,
//                                            placeOnTable = newPlaceOnTableAttack ?: PlaceOnTable()
//                                        )
                                    }
                                }

//                                Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
//                                else if (!isAttacker && isYourTurn && yourCardIsKozr && attackCardIsHigherThanTable) {
//                                    if (perevod == true && cardNumberIsSameWithTable && currentSelected?.isEmpty() == true) {
//                                        perevodAlert = true
//                                    } else if (!isOneCardLeft) {
//                                        Toast.makeText(
//                                            context,
//                                            "Vuracağın kartı seç.",
//                                            Toast.LENGTH_SHORT
//                                        )
//                                            .show()
//                                        userViewModel.setManyCardLeft()
//                                    }
//                                    else {
////                                        userViewModel.yereKartDus(
////                                            selectedCard = cardIsKozr ?: CardPair(),
////                                            rotate = true,
////                                            placeOnTable = newPlaceOnTableAttack ?: PlaceOnTable()
////                                        )
//                                    }
//                                }

                                //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəki ilə eynidir. Perevod var . Kozr var
                                else if (!isAttacker && isYourTurn && cardNumberIsSameWithTable && perevod == true && currentSelected?.isEmpty() == true) {
                                    val nextPlayer =
                                        getNextPlayer(allPlayers, currentPlayer ?: PlayerData())
                                    if ((nextPlayer?.cards?.size ?: 0) <= cardsNotAttacked.size) {
                                        showRemainingCardsToast(
                                            context,
                                            nextPlayer?.cards?.size ?: 0
                                        )
                                    } else {
//                                        userViewModel.yereKartDus(
//                                            changeAttacker = true,
//                                            selectedCard = cardIsKozr ?: CardPair(),
//                                            rotate = true,
//                                            placeOnTable = newPlaceOnTable ?: PlaceOnTable(),
//                                            perevodKartlari = cardsNotAttacked.plus(cardIsKozr) as List<CardPair>
//                                        )
                                    }
                                }
                                yereDusState = false
                                scope.launch {
                                    circularViewModel.decreaseSecond.value = 10
                                    delay(1000L)
                                    yereDusState = true
                                }
                            }) {
                                Text(text = "Yerə düş")
                            }
                        }
                    }
                    if (isAttacker && isYourTurn && allSelectedCards.isNotEmpty() && selectedCard == null) {

                        Button(onClick = {
                            userViewModel.bitayaGetsin()
//                            scope.launch {
//                                delay(1000)
//                                userViewModel.yerdenKartGotur(
//                                    durakData = durak.value
//                                        ?: DurakData(),
//                                    player = currentPlayer ?: PlayerData(),
//                                    playerData = allPlayers ?: listOf(),
//                                    card = singleCard.ifEmpty { listOfNotNull(durak.value?.kozr) },
//                                    context = context
//                                )
//                            }
                        }) {
                            Text(text = "Bita")
                        }
                    } else if (!isAttacker && isYourTurn && allSelectedCards.isNotEmpty() && selectedCard == null) {

                        Button(onClick = {
                            userViewModel.eleYig()
                        }) {
                            Text(text = "Ələ yığ")
                        }
                    }
                    Spacer(modifier = Modifier.size(30.dp))

                    TableDesign(
                        userViewModel = userViewModel,
                        table = currentPlayer,
                        tableNumber = currentPlayer?.tableNumber ?: 0,
                        starter = currentPlayer?.tableNumber ?: 0,
                        hide = currentPlayer?.tableNumber == null,
                        navController = navController,
                        standUp = false
                    )
                    PlayerCards(
                        durakData = durak.value ?: DurakData(),
                        userData = userData.value ?: UserData(),
                        userViewModel = userViewModel,
                        navController = navController
                    )

                    Spacer(modifier = Modifier.size(10.dp))
                }

            })
        }
    }
}


@Composable
fun DurakSettingsAlert(
    closeAlert: () -> Unit, userViewModel: UserViewModel
) {
    val userData = userViewModel.userDataState.collectAsState()
    val durak = userViewModel.durakDataState.collectAsState()

    val currentPlayer =
        durak.value?.playerData?.find { it.userData?.username == userData.value?.username }
    val cards = currentPlayer?.cards
    val kozrSuit = durak.value?.kozrSuit

    val ascendingCard =
        cards?.sortedBy { if (it.suit == kozrSuit) it.number?.plus(15) else it.number }
    val shuffledCard = cards?.shuffled()

    var cardAscending by remember { mutableStateOf(userData.value?.durakSettings?.cardAscending) }


    AlertDialog(onDismissRequest = {
        closeAlert()
    }, icon = {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Kartları sırala:")
                Switch(checked = cardAscending ?: false, onCheckedChange = { cardAscending = it })
            }
        }
    }, confirmButton = {
        Button(onClick = {
            userData.value?.let {
                userViewModel.refreshCards(
                    durakData = durak.value ?: DurakData(),
                    playerData = currentPlayer ?: PlayerData(),
                    card = if (cardAscending == true) ascendingCard ?: listOf() else shuffledCard
                        ?: listOf(),
                    userData = it.copy(
                        durakSettings = it.durakSettings?.copy(
                            cardAscending = cardAscending
                        )
                    ),
                )
            }
            closeAlert()
        }) {
            Text(text = "Təsdiqlə")
        }
    })
}

@Composable
fun PerevodAlert(
    onDismiss: () -> Unit, perevod: () -> Unit, vur: () -> Unit
) {

    AlertDialog(onDismissRequest = { onDismiss() }, title = { Text(text = "Seçim et") }, text = {
        Text(
            text = "Kartı vurmaq istəyirsən yoxsa ötürmək?"
        )
    }, confirmButton = {
        Button(onClick = {
            perevod()
        }) {
            Text(text = "Ötür")
        }
    }, dismissButton = {
        Button(onClick = {
            vur()
        }) {
            Text(text = "Vur")
        }
    })
}

@Composable
fun FlagAlert(
    onDismiss: () -> Unit, beli: () -> Unit, xeyr: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Təslim olmaq istəyirsən?") },
        confirmButton = {
            Button(onClick = {
                beli()
            }) {
                Text(text = "Bəli")
            }
        },
        dismissButton = {
            Button(onClick = {
                xeyr()
            }) {
                Text(text = "Xeyr")
            }
        })
}

//fun generateCardPairList(placeOnTable: PlaceOnTable): List<CardPair> {
//    val cardPairList = mutableListOf<CardPair>()
//
//    if (placeOnTable.firstAttack == null) {
//        placeOnTable.first?.let { cardPairList.add(it) }
//    }
//
//    if (placeOnTable.secondAttack == null) {
//        placeOnTable.second?.let { cardPairList.add(it) }
//    }
//
//    if (placeOnTable.thirdAttack == null) {
//        placeOnTable.third?.let { cardPairList.add(it) }
//    }
//
//    if (placeOnTable.fourthAttack == null) {
//        placeOnTable.fourth?.let { cardPairList.add(it) }
//    }
//
//    if (placeOnTable.fifthAttack == null) {
//        placeOnTable.fifth?.let { cardPairList.add(it) }
//    }
//
//    if (placeOnTable.sixthAttack == null) {
//        placeOnTable.sixth?.let { cardPairList.add(it) }
//    }
//
//    return cardPairList
//}


fun getNextPlayer(allPlayers: MutableList<PlayerData>?, currentPlayer: PlayerData): PlayerData? {
    return allPlayers?.let { players ->
        val currentPlayerIndex = players.indexOf(currentPlayer)
        val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
        players.getOrNull(nextPlayerIndex)
    }
}

fun showRemainingCardsToast(context: Context, remainingCards: Int) {
    val toastMessage = "Rəqibin $remainingCards kartı qalıb."
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}
