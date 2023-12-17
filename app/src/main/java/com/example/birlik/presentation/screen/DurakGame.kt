@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.screen.components.durak.PlayerCards
import com.example.birlik.presentation.screen.components.durak.UserItem
import com.example.birlik.presentation.viewmodel.AuthViewModel
import com.example.birlik.presentation.viewmodel.UserViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun DurakGame(
    navController: NavController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    durakData: DurakData
) {

    val durak = userViewModel.durakData.collectAsState()
    val userData = userViewModel.userData.collectAsState()

    val currentUserId = authViewModel.currentUserId
    val remainingCardsViewModel = userViewModel.remainingCards.observeAsState(listOf())
    var remainingCards by remember { mutableStateOf(listOf<CardPair>()) }

    val currentPlayer =
        durak.value?.playerData?.find { it.userData?.username == userData.value?.username }
    val allPlayers = durak.value?.playerData
    val kozrSuit = durak.value?.kozr?.suit

    var cards by remember { mutableStateOf(listOf<CardPair>()) }

    val shuffled = durak.value?.cards?.shuffled(Random)




    LaunchedEffect(remainingCardsViewModel.value, durak.value) {
        userViewModel.getDurakData(durakData.gameId ?: "")
        durak.value?.let {
            userViewModel.startListeningForDurakUpdates(it)
        }
        remainingCardsViewModel.value?.let {
            remainingCards = it
        }


    }

    DisposableEffect(Unit) {
        onDispose {
//            currentUserId.value?.let {
//                userData.value?.let { userData ->
//                    userViewModel.updateUserData(userData.copy(status = "offline"))
//                }
//            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(colorResource(id = R.color.dark_green)),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            },
            title = { Text(text = "Durak") })
    }, content = { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.dark_green))
                .padding(it)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val context = LocalContext.current

                if (durak.value?.tableOwner == currentPlayer?.userData) {
                    if (durak.value?.started != true) {
                        Button(onClick = {
                            if (shuffled != null) {
                                val numPlayers = durak.value?.playerData?.size ?: 0
                                val cardsPerPlayer = when (numPlayers) {
                                    1 -> 6
                                    2 -> 6 // Each player gets 6 cards for a total of 12
                                    3 -> 6 // Each player gets 6 cards for a total of 18
                                    else -> 0 // Handle other cases as needed
                                }

                                cards = shuffled.subList(0, cardsPerPlayer * numPlayers)
                                remainingCards = shuffled - cards.toSet()
                            }

                            val lastCard = remainingCards.lastOrNull()
                            val modifiedRemainingCards = if (lastCard != null) {
                                remainingCards.toMutableList().apply {
                                    remove(lastCard)
                                }
                            } else {
                                remainingCards
                            }

                            userViewModel.oyuncularaKartPayla(
                                durakData = durak.value ?: DurakData(),
                                players = durak.value?.playerData ?: mutableListOf(),
                                originalList = cards,
                                kozr = lastCard ?: CardPair(),
                                remainingCards = modifiedRemainingCards,
                                onComplete = { updatedRemainingCards ->
                                    userViewModel.updateDurakData { durakData ->
                                        durakData.copy(cards = updatedRemainingCards.toMutableList())
                                    }
                                },
                                onSuccess = {
                                    println(allPlayers?.map { it.cards })
                                    println(currentPlayer?.cards)
                                }
                            )
                        }) {
                            Text(text = "Kartları payla")
                        }
                    } else if (durak.value?.choseAttacker != true) {
                        Button(onClick = {
                            println(allPlayers?.map { it.cards })
                            println(currentPlayer?.cards)
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

//                                    currentPlayer?.cards?.forEach { card ->
//                                        if (durak.value?.kozr?.suit == card.suit) {
//                                            // Handle the case when the card has the same suit as kozr
//                                            userViewModel.updateOyuncuSirasi(
//                                                durakData = durakData,
//                                                starter = startingPlayerUsername ?: "",
//                                                starterTableNumber = startingPlayer.tableNumber
//                                                    ?: 0
//                                            )
//                                        }
//                                    }

                                    allPlayers.map {
                                        it.cards?.forEach { card ->
                                            if (durak.value?.kozr?.suit == card.suit) {
                                                // Handle the case when the card has the same suit as kozr
                                                userViewModel.updateOyuncuSirasi(
                                                    durakData = durakData,
                                                    starter = startingPlayerUsername ?: "",
                                                    starterTableNumber = startingPlayer.tableNumber
                                                        ?: 0
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                            Text(text = "Başla")
                        }

                    }
                }


            }
            LazyColumn {
//                item {
//                    val firstTable = durak.value?.tableData?.firstTable
//                    val image = durak.value?.tableData?.firstTable?.image
//                    val currentPla = durak.value?.playerData?.find { it.userData?.username == userData.value?.username }
//
//                    TableDesign(
//                        userViewModel = userViewModel,
//                        table = PlayerData(userData = firstTable, playerId = userData.value?.userId, cards = currentPla?.cards),
//                        tableNumber = 1,
//                        starter = 1,
//                        image = image ?: ""
//                    )
//
//
//                }
                items(1) {
                    UserItem(
                        userData = userData.value ?: UserData(),
                        durakData = durak.value ?: DurakData(),
                        onGetCard = {
                            val singleCard = remainingCards.lastOrNull()

                            if (singleCard != null) {
                                val mutableRemainingCards = remainingCards.toMutableList()
                                mutableRemainingCards.remove(singleCard)
                                remainingCards = mutableRemainingCards

                                userViewModel.updateDurakCards(
                                    durakData = durak.value ?: DurakData(),
                                    cards = mutableRemainingCards
                                )
                            }

                            for (playerData in durak.value?.playerData!!) {
                                if (playerData.userData?.userId == currentUserId.value) {
                                    userViewModel.yerdenKartGotur(
                                        durakData = durak.value ?: DurakData(),
                                        player = playerData,
                                        playerDataList = durak.value?.playerData ?: listOf(),
                                        card = singleCard ?: durak.value?.kozr ?: CardPair(),
                                    )
                                    break
                                }
                            }
                        }, userViewModel = userViewModel
                    )
                }
            }
        }
    }, bottomBar = {

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            PlayerCards(
                durakData = durak.value ?: DurakData(),
                userData = userData.value ?: UserData(),
                userViewModel = userViewModel
            )

            Spacer(modifier = Modifier.size(10.dp))
        }

    })
}







