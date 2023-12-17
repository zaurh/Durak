package com.example.birlik.presentation.screen.components.durak

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.viewmodel.UserViewModel

@Composable
fun PlayerCards(
    durakData: DurakData,
    userData: UserData,
    userViewModel: UserViewModel,
) {
    val context = LocalContext.current
    val durak = userViewModel.durakData.collectAsState()
    val currentPlayer = durakData.playerData?.find { it.userData?.username == userData.username }
    val allPlayers = durak.value?.playerData
    val allSelectedCards =
        durakData.playerData?.flatMap { it.selectedCard ?: emptyList() } ?: emptyList()
    val remainingCardsViewModel = userViewModel.remainingCards.observeAsState(listOf())

    var winnerAlert by remember { mutableStateOf(false) }

    if (allPlayers?.any { it.cards?.size == 0 } == true && remainingCardsViewModel.value.isEmpty() && durak.value?.started == true) {
        userViewModel.winGame(durakData, userData) {
            winnerAlert = true
        }
    }

    if (winnerAlert) {
        AlertDialog(
            title = { Text(text = "Winner is ${durak.value?.winner?.username}") },
            confirmButton = {
                Text(text = "Close")
            },
            onDismissRequest = { winnerAlert = false })
    }

    if (allSelectedCards.size == 12) {
        val selectedCards = durak.value?.playerData?.flatMap { it.selectedCard ?: listOf() }
        userViewModel.bitayaGetsin(
            durak.value ?: DurakData(),
            durak.value?.playerData ?: mutableListOf(),
            selectedCards ?: listOf()
        )
        Toast.makeText(context, "Bitaya getdi", Toast.LENGTH_SHORT).show()
    }

    if (userData.username == durak.value?.attacker && userData.username == durak.value?.startingPlayer && allSelectedCards.isNotEmpty()) {
        Button(onClick = {
            val selectedCards = durak.value?.playerData?.flatMap { it.selectedCard ?: listOf() }
            userViewModel.bitayaGetsin(
                durak.value ?: DurakData(),
                durak.value?.playerData ?: mutableListOf(),
                selectedCards ?: listOf()
            )
        }) {
            Text(text = "Bita")
        }
    } else if (userData.username != durak.value?.attacker && userData.username == durak.value?.startingPlayer && allSelectedCards.isNotEmpty()) {
        val selectedCardSizeSum = durakData.playerData?.sumOf { it.selectedCard?.size ?: 0 } ?: 0

        Button(onClick = {

            if (selectedCardSizeSum % 2 != 0) {
                userViewModel.eleYig(durakData, playerData = currentPlayer!!,
                    selectedCards = allSelectedCards.map {
                        if (it.number!! > 15) {
                            CardPair(number = it.number - 15, suit = it.suit)
                        } else {
                            CardPair(number = it.number, suit = it.suit)
                        }
                    }
                )
            }
        }) {
            Text(text = "Ələ yığ")
        }
    }


    if (currentPlayer != null) {
        Box(Modifier.fillMaxWidth()) {
            val yourLastDroppedCardNumber = currentPlayer.lastDroppedCardNum?.number
            val yourLastDroppedCard = currentPlayer.lastDroppedCardNum
            val lastDroppedCardNumber = durak.value?.cards?.lastOrNull()?.number
            val attacker = durak.value?.attacker
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.size(40.dp))
                currentPlayer.cards?.forEachIndexed { index, card ->
                    val rotationAngle = (index - (currentPlayer.cards.size - 1) / 2) * 1f
                    val horizontalOffset = index * -15f // Adjust this value to control the spacing
                    PlayerCardStyle(
                        card = card,
                        onClick = {
                            if (durak.value?.cheat != true) {
                                if (remainingCardsViewModel.value.isNotEmpty() && (allPlayers?.all { it.selectedCard!!.isEmpty() } == true) && allPlayers.all {
                                        (it.cards?.size ?: 0) < 6
                                    }) {
                                    Toast.makeText(context, "Kart götü", Toast.LENGTH_SHORT).show()
                                } else {
                                    val startingPlayer = durak.value?.startingPlayer
                                    val allSelected =
                                        allPlayers?.flatMap { it.selectedCard ?: listOf() }

                                    val currentSelected = currentPlayer.selectedCard
                                    val selectedExceptCurrent =
                                        allSelected?.filterNot { currentSelected?.contains(it) == true }
                                            ?: emptyList()
                                    val currentUsername = userData.username
                                    val round = durak.value?.round
                                    val kozr = durak.value?.kozr


                                    if (round == true || kozr == null) {
                                        val isYourTurn = startingPlayer == currentUsername
                                        val isOneCardOnTable =
                                            selectedExceptCurrent.size - currentSelected!!.size <= 1

                                        if (isYourTurn && isOneCardOnTable) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                card,
                                                true
                                            )
                                            Toast.makeText(context, "bir", Toast.LENGTH_SHORT)
                                                .show()

                                        } else if (isYourTurn) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                card,
                                                false
                                            )
                                            Toast.makeText(context, "iki", Toast.LENGTH_SHORT)
                                                .show()

                                        } else if (yourLastDroppedCardNumber == card.number && attacker == currentUsername) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                card,
                                                false
                                            )
                                            Toast.makeText(context, "uc", Toast.LENGTH_SHORT).show()

                                        } else {
                                            showNotYourTurnToast(context)
                                        }
                                    } else {
                                        val isYourTurn = startingPlayer == currentUsername
                                        val isOneCardSelected =
                                            selectedExceptCurrent.size - currentSelected!!.size <= 1

                                        if (currentPlayer.cards.size >= 6 && isYourTurn && isOneCardSelected) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                card,
                                                true
                                            )
                                            Toast.makeText(context, "dord", Toast.LENGTH_SHORT)
                                                .show()

                                        } else if (currentPlayer.cards.size >= 6 && isYourTurn) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                card,
                                                false
                                            )
                                            Toast.makeText(context, "bes", Toast.LENGTH_SHORT)
                                                .show()

                                        } else if (currentPlayer.cards.size >= 6 && yourLastDroppedCardNumber == card.number && attacker == currentUsername) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                card,
                                                false
                                            )
                                            Toast.makeText(context, "alti", Toast.LENGTH_SHORT)
                                                .show()

                                        } else {
//                                        showNotYourTurnToast(context)
                                            Toast.makeText(
                                                context,
                                                "${allPlayers?.all { it.cards?.size != 6 }}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        Toast.makeText(context, "yeddi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            //Cheat not activated
                            else {
                                val startingPlayer = durak.value?.startingPlayer
                                val allSelected =
                                    allPlayers?.flatMap { it.selectedCard ?: listOf() }
                                val currentSelected = currentPlayer.selectedCard
                                val selectedExceptCurrent =
                                    allSelected?.filterNot { currentSelected?.contains(it) == true }
                                        ?: emptyList()
                                val currentUsername = userData.username
                                val round = durak.value?.round
                                val kozr = durak.value?.kozr
                                val kozrSuit = durak.value?.kozrSuit
                                val cardIsHigherThanLastDroppedCard =
                                    (lastDroppedCardNumber ?: 0) < (card.number ?: 0)
                                val onTableSelected = durak.value?.selectedCards
                                val isYourTurn = startingPlayer == currentUsername
                                val isOneCardSelected =
                                    selectedExceptCurrent.size - currentSelected!!.size <= 1
                                val isAttacker = attacker == currentUsername
                                val tableIsEmpty = allSelectedCards.isEmpty()
                                val sameNumberOnTable =
                                    allSelected?.any {
                                        it.number == card.number || it.number?.minus(
                                            15
                                        ) == card.number
                                    }
                                val sixCardsOnHands = allPlayers?.all {
                                    (it.cards?.size ?: 0) >= 6
                                }
                                val yourLastDropped =
                                    yourLastDroppedCardNumber == card.number
                                val cardIsHigherThanTable =
                                    (card.number ?: 0) > (onTableSelected?.lastOrNull()?.number
                                        ?: 0)
                                val cardIsSameSuitWithTable =
                                    card.suit == onTableSelected?.lastOrNull()?.suit

                                val yourCardIsKozr = card.suit == kozrSuit
                                val kozrOnTable = onTableSelected?.lastOrNull()?.suit == kozrSuit
                                val isOneCardOnTable =
                                    selectedExceptCurrent.size - currentSelected!!.size <= 1

                                if (remainingCardsViewModel.value.isNotEmpty() && (allPlayers?.all { it.selectedCard!!.isEmpty() } == true) && allPlayers.all {
                                        (it.cards?.size ?: 0) < 6
                                    }) {
                                    Toast.makeText(context, "Kart götür.", Toast.LENGTH_SHORT)
                                        .show()
                                } else {

                                    //Hücumcu sənsən. Sıra səndədir. Yerdə kart yoxdur. Kozr var
                                    if (isAttacker && isYourTurn && sixCardsOnHands == true && tableIsEmpty && kozr != null) {

                                        val cardIsKozr = if (yourCardIsKozr) card.copy(
                                            number = card.number?.plus(15)
                                        ) else card

                                        userViewModel.yereKartDus(
                                            durakData,
                                            currentPlayer,
                                            cardIsKozr,
                                            true
                                        )
                                        Toast.makeText(context, "birinci", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    //Hücumcu sənsən. Sıra səndədir. Yerdə kart yoxdur. Kozr yoxdur
                                    else if (isAttacker && isYourTurn && tableIsEmpty && kozr == null) {

                                        val cardIsKozr = if (yourCardIsKozr) card.copy(
                                            number = card.number?.plus(15)
                                        ) else card

                                        userViewModel.yereKartDus(
                                            durakData,
                                            currentPlayer,
                                            cardIsKozr,
                                            true
                                        )
                                        Toast.makeText(context, "kozr yoxdur", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    //Hücumcu sənsən. Sıra səndə deyil. Eyni rəqəmli kartı düş. Kozr var
                                    else if (isAttacker && !isYourTurn && yourLastDropped) {
                                        val cardIsKozr =
                                            if (yourCardIsKozr || yourLastDroppedCard?.suit == kozrSuit) card.copy(
                                                number = card.number?.plus(15)
                                            )
                                            else card

                                        userViewModel.yereKartDus(
                                            durakData,
                                            currentPlayer,
                                            if (yourLastDroppedCard?.suit == kozrSuit) cardIsKozr else cardIsKozr,
                                            false
                                        )
                                        Toast.makeText(context, "ikinci", Toast.LENGTH_SHORT)
                                            .show()

                                    }
                                    //Hücumcu sənsən. Sıra səndədir. Yerdəki kartın rəqəmini düş. Kozr var
                                    else if (isAttacker && isYourTurn && sameNumberOnTable == true) {
                                        val cardIsKozr = if (yourCardIsKozr) card.copy(
                                            number = card.number?.plus(15)
                                        ) else card

                                        userViewModel.yereKartDus(
                                            durakData,
                                            currentPlayer,
                                            cardIsKozr,
                                            true
                                        )
                                        Toast.makeText(context, "ucuncu", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Vurulmayan bir kart qalıb. Kozr var
                                    else if (!isAttacker && isYourTurn && cardIsHigherThanTable && cardIsSameSuitWithTable && isOneCardSelected) {
                                        userViewModel.yereKartDus(
                                            durakData,
                                            currentPlayer,
                                            card,
                                            true
                                        )
                                        Toast.makeText(context, "dorduncu", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə kart çoxdur. Kozr var
                                    else if (!isAttacker && isYourTurn && cardIsHigherThanTable && cardIsSameSuitWithTable) {
                                        userViewModel.yereKartDus(
                                            durakData,
                                            currentPlayer,
                                            card,
                                            false
                                        )
                                        Toast.makeText(context, "besinci", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr) {

                                        val cardIsKozr = card.copy(number = card.number?.plus(15))
                                        if (cardIsKozr.number!! > (onTableSelected?.lastOrNull()?.number
                                                ?: 0)
                                        ) {
                                            userViewModel.yereKartDus(
                                                durakData,
                                                currentPlayer,
                                                cardIsKozr,
                                                true
                                            )
                                        }

                                        Toast.makeText(context, "altinci", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        Toast.makeText(context, "Xeta", Toast.LENGTH_SHORT).show()
//                                        showNotYourTurnToast(context)
//                                        Toast.makeText(
//                                            context,
//                                            "cardIsHigher = $cardIsHigherThanTable \n" +
//                                                    " cardWithSameSuit = $cardIsSameSuitWithTable \n" +
//                                                    " isYourTurn = $isYourTurn \n " +
//                                                    "isAttacker = $isAttacker \n " +
//                                                    "lastCardSuit = ${onTableSelected?.lastOrNull()} \n" +
//                                                    "cardSuit = ${card} \n" +
//                                                    "sameNumberOnTable = $sameNumberOnTable",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//
//                                        println("cardIsHigher = $cardIsHigherThanTable")
//                                        println("cardWithSameSuit = $cardIsSameSuitWithTable")
//                                        println("isYourTurn = $isYourTurn")
//                                        println("isAttacker = $isAttacker")
//                                        println("lastCardSuit = ${onTableSelected?.lastOrNull()?.suit}")
//                                        println("cardSuit = ${card.suit}")
//                                        println("sameNumberOnTable = $sameNumberOnTable")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .rotate(rotationAngle)
                            .offset(x = horizontalOffset.dp)
                    )
                }
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }

}

private fun showNotYourTurnToast(context: Context) {

    Toast.makeText(
        context,
        "Sənin sıran deyil.",
        Toast.LENGTH_SHORT
    ).show()
}
