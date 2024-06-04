@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.durak.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.durak.R
import com.zaurh.durak.common.entryPriceCalculate
import com.zaurh.durak.common.willBeSoonToast
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.DurakData
import com.zaurh.durak.data.remote.durak.PlayerData
import com.zaurh.durak.presentation.screen.components.FlagAlert
import com.zaurh.durak.presentation.screen.components.ResultAlert
import com.zaurh.durak.presentation.screen.components.durak.PlayerCards
import com.zaurh.durak.presentation.screen.components.durak.TableDesign
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import com.zaurh.durak.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DurakGame(
    navController: NavController,
    userViewModel: UserViewModel,
    durakViewModel: DurakViewModel,
    durakData: DurakData
) {
    val userData = userViewModel.userDataState.collectAsState()
    val durakDataState = durakViewModel.durakDataState.collectAsState()
    var flagAlert by remember { mutableStateOf(false) }
    var resultAlert by remember { mutableStateOf(false) }
    val allPlayers = durakDataState.value?.playerData
    val currentPlayer =
        durakDataState.value?.playerData?.find { it.userData?.userId == userData.value?.userId }
    val cardAscending by remember {
        mutableStateOf(
            userData.value?.durakSettings?.cardAscending ?: false
        )
    }
    val context = LocalContext.current

    LaunchedEffect(true) {
        durakViewModel.getDurakData(durakData.gameId ?: "")
    }


    Box(Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(
                id = durakData.tableOwner?.skinSettings?.backgroundPicked?.image
                    ?: R.drawable.background_green
            ),
            contentDescription = ""
        )

        Scaffold(containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        colorResource(id = R.color.transparent)
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "", tint = Color.White)
                        }
                    }, title = {},
                    actions = {
                        if (durakDataState.value?.started == true){
                            var menuOpen by remember { mutableStateOf(false) }

                            AnimatedVisibility(visible = menuOpen, enter = slideInHorizontally(
                                initialOffsetX = {it/4}
                            ), exit = slideOutHorizontally(
                                targetOffsetX = { it/4 },
                                animationSpec = tween(durationMillis = 50)
                            )) {
                                Row {
                                    IconButton(onClick = {
                                        willBeSoonToast(context)
//                                        cardAscending = !cardAscending
//                                    durakViewModel.refreshCards(cardAscending)
                                    }) {
                                        Box{
                                            Icon(
                                                painterResource(id = R.drawable.ascending),
                                                contentDescription = "",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp),
                                                )
                                            if (cardAscending){
                                                Icon(
                                                    imageVector = Icons.Default.Done,
                                                    contentDescription = "",
                                                    tint = Color.Green
                                                )
                                            }
                                        }

                                    }
                                    IconButton(onClick = {
                                        flagAlert = true
                                    }) {
                                        Icon(
                                            painterResource(id = R.drawable.flag),
                                            contentDescription = "",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                            }

                            IconButton(onClick = {
                                menuOpen = !menuOpen
                            }) {
                                Icon(
                                    imageVector = if (menuOpen) Icons.Default.Close else Icons.Default.Menu,
                                    contentDescription = "", tint = Color.White
                                )
                            }
                        }

                    }
                )
            },
            content = {
                val firstUser =
                    durakDataState.value?.playerData?.find { it.userData?.email == durakDataState.value?.tableData?.firstTable?.email }
                val secondUser =
                    durakDataState.value?.playerData?.find { it.userData?.email == durakDataState.value?.tableData?.secondTable?.email }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    PlayerCards(
                        modifier = Modifier,
                        durakData = durakDataState.value ?: DurakData(),
                        userData = userData.value ?: UserData(),
                        userViewModel = userViewModel,
                        durakViewModel = durakViewModel,
                        navController = navController
                    )
                    Column(Modifier.fillMaxSize()) {
                        TableDesign(
                            durakViewModel = durakViewModel,
                            userViewModel = userViewModel,
                            table = firstUser?.copy(
                                userData = durakDataState.value?.tableData?.firstTable,
                                playerId = durakDataState.value?.tableData?.firstTable?.userId,
                                cards = firstUser.cards
                            ),
                            tableNumber = 1,
                            starter = 1,
                            navController = navController,
                            hide = (durakDataState.value?.tableData?.firstTable?.userId == userData.value?.userId && durakDataState.value?.started == true),
                            standUp = durakDataState.value?.tableData?.firstTable?.userId == userData.value?.userId
                        )
                        TableDesign(
                            durakViewModel = durakViewModel,
                            userViewModel = userViewModel,
                            table = secondUser?.copy(
                                userData = durakDataState.value?.tableData?.secondTable,
                                playerId = durakDataState.value?.tableData?.secondTable?.userId,
                                cards = secondUser.cards
                            ),
                            tableNumber = 2,
                            starter = 2,
                            navController = navController,
                            hide = (durakDataState.value?.tableData?.secondTable?.userId == userData.value?.userId && durakDataState.value?.started == true),
                            standUp = durakDataState.value?.tableData?.secondTable?.userId == userData.value?.userId
                        )
                    }
                }


            }
        )
    }

    val coin = durakDataState.value?.entryPriceCoin ?: 0
    val entryCoin = durakDataState.value?.entryPriceCoin ?: 0
    val entryCash = durakDataState.value?.entryPriceCash ?: 0
    val winCoin = entryPriceCalculate(entryCoin)
    val winCash = entryPriceCalculate(entryCash)

    var moneyIconState by remember { mutableStateOf(R.drawable.birlik_cash) }
    var ratingState by remember { mutableStateOf("") }
    var lostAmountState by remember { mutableStateOf("") }
    var winAmountState by remember { mutableStateOf("") }



    if (flagAlert) {
        val flagEnabled = remember { mutableStateOf(true) }
        FlagAlert(enabled = flagEnabled.value,onDismiss = { flagAlert = false }, yes = {
            flagEnabled.value = false
            val nextPlayer =
                getNextPlayer(allPlayers, currentPlayer ?: PlayerData())
            durakViewModel.finishGame(
                winner = nextPlayer?.userData ?: UserData(),
                loser = userData.value ?: UserData(),
            )
        }, no = {
            flagAlert = false
        })
    }
    if (resultAlert) {
        LaunchedEffect(true){
            delay(5000)
            resultAlert = false
            navController.popBackStack()
        }
        val win = durakDataState.value?.loser?.userId != userData.value?.userId

        ResultAlert(
            win = win,
            rating = ratingState,
            amount = if (win) winAmountState else lostAmountState,
            moneyIcon = moneyIconState,
            onDismiss = {
                resultAlert = false
                navController.popBackStack()
            }) {
            resultAlert = false
            navController.popBackStack()
        }
    }

    LaunchedEffect(durakDataState.value?.finished) {
        if (durakDataState.value?.finished == true) {
            println(durakDataState.value?.title)
            println(durakData.title)
            moneyIconState = if (coin > 0) R.drawable.birlik_coin else R.drawable.birlik_cash
            ratingState = durakDataState.value?.rating.toString()
            lostAmountState =
                if (moneyIconState == R.drawable.birlik_coin) entryCoin.toString() else entryCash.toString()
            winAmountState =
                if (moneyIconState == R.drawable.birlik_coin) winCoin.toString() else winCash.toString()
            if (durakData.gameId == durakDataState.value?.gameId){
                resultAlert = true
            }
        }
    }

    val finishCardOnHand = currentPlayer?.cards?.isEmpty() ?: false
    val remainingCards = durakDataState.value?.cards?.isEmpty() ?: false
    val started = durakDataState.value?.started

    LaunchedEffect(durakDataState.value?.cards) {
        if (finishCardOnHand && remainingCards && started != null) {
            delay(2000)
            val startingPlayer = durakDataState.value?.playerData?.find { it.userData?.email == durakDataState.value?.startingPlayer }
            val nextPlayer =
                getNextPlayer(allPlayers, startingPlayer ?: PlayerData())

            if (durakData.gameId == durakDataState.value?.gameId){
                durakViewModel.finishGame(
                    winner = nextPlayer?.userData ?: UserData(),
                    loser = startingPlayer?.userData ?: UserData(),
                )
            }
        }
    }
}



fun getNextPlayer(allPlayers: MutableList<PlayerData>?, currentPlayer: PlayerData): PlayerData? {
    return allPlayers?.let { players ->
        val currentPlayerIndex = players.indexOf(currentPlayer)
        val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
        players.getOrNull(nextPlayerIndex)
    }
}
fun showRemainingCardsToast(context: Context, remainingCards: Int) {
    val toastMessage = context.getString(R.string.opponentDoesntHaveCard)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}
