package com.example.birlik.presentation.screen.components.durak

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCards(
    durakData: DurakData,
    userData: UserData,
    userViewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val durak = userViewModel.durakDataState.collectAsState()
    val currentPlayer = durakData.playerData?.find { it.userData?.username == userData.username }
    val allPlayers = durak.value?.playerData
    val allSelectedCards =
        durakData.playerData?.flatMap { it.selectedCard ?: emptyList() } ?: emptyList()
    val remainingCardsViewModel = userViewModel.remainingCardsRepo.observeAsState(listOf())

//    var winnerAlert by remember { mutableStateOf(false) }
//
//    LaunchedEffect(key1 = true){
//        if (allPlayers?.any { it.cards?.size == 0 } == true && remainingCardsViewModel.value.isEmpty() && durak.value?.started == true) {
//            userViewModel.winGame(durakData, userData) {
//                winnerAlert = true
//            }
//        }
//    }
//
//
//    if (winnerAlert) {
//        val winner = allPlayers?.filter { it.cards?.size == 0 }
//        AlertDialog(
//            title = { Text(text = "Winner is ${winner?.lastOrNull()?.userData?.username}") },
//            confirmButton = {
//                Button(onClick = {
//                    winnerAlert = false
//                    navController.popBackStack()
//                }) {
//                    Text(text = "Close")
//                }
//            },
//            onDismissRequest = {
//                winnerAlert = false
//                navController.popBackStack()
//            })
//    }

    if (allSelectedCards.size == 12) {
        val selectedCards = durak.value?.playerData?.flatMap { it.selectedCard ?: listOf() }
//        userViewModel.bitayaGetsin(
//            durak.value ?: DurakData(),
//            durak.value?.playerData ?: mutableListOf(),
//            selectedCards ?: listOf()
//        )
        Toast.makeText(context, "Bitaya getdi", Toast.LENGTH_SHORT).show()
    }

    if (currentPlayer != null) {
        Box(Modifier.fillMaxWidth()) {

            val cards = currentPlayer.cards

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                    Spacer(modifier = Modifier.size(40.dp))
                    var selectedCard by remember { mutableStateOf<CardPair?>(null) }

                    cards
                        ?.forEachIndexed { index, card ->
                            val rotationAngle = (index - (currentPlayer.cards.size - 1) / 2) * 1f
                            val horizontalOffset =
                                index * -15f // Adjust this value to control the spacing
                            PlayerCardStyle(
                                modifier = Modifier
                                    .rotate(rotationAngle)
                                    .offset(x = horizontalOffset.dp),
                                selectedCard = selectedCard,
                                onCardSelected = { selectedCard = it },
                                card = card,
                                onClick = {
                                    userViewModel.setSelectedCard(card)
                                },
                                onInactiveClick = {
                                    userViewModel.clearSelectedCard()
                                }
                            )
                        }
                    Spacer(modifier = Modifier.size(40.dp))

            }
        }
    }

}
