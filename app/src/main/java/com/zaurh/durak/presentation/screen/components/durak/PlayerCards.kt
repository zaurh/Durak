package com.zaurh.durak.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.zaurh.durak.R
import com.zaurh.durak.common.NavParam
import com.zaurh.durak.common.navigateTo
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.CardPair
import com.zaurh.durak.data.remote.durak.DurakData
import com.zaurh.durak.data.remote.durak.PlaceOnTable
import com.zaurh.durak.presentation.screen.components.DragTarget
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import com.zaurh.durak.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@Composable
fun PlayerCards(
    modifier: Modifier = Modifier,
    durakData: DurakData,
    userData: UserData,
    userViewModel: UserViewModel,
    durakViewModel: DurakViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val durak = durakViewModel.durakDataState.collectAsState()
    val currentPlayer = durakData.playerData?.find { it.userData?.email == userData.email }
    val isAttacker = durak.value?.attacker == currentPlayer?.userData?.email
    val allSelectedCards = durak.value?.selectedCards ?: listOf()
    val remainingCardsViewModel = durakViewModel.remainingCardsRepo.observeAsState(listOf())
    var remainingCards by remember { mutableStateOf(listOf<CardPair>()) }

    val buttonsDelay by durakViewModel.buttonsState
    var buttonsVisibility by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()



    if (currentPlayer != null) {
        Box(modifier = modifier.fillMaxSize()) {

            var cards by remember { mutableStateOf(currentPlayer.cards ?: listOf()) }

            LaunchedEffect(currentPlayer.cards, remainingCardsViewModel.value) {
                cards = currentPlayer.cards ?: listOf()
                durak.value?.let {
                    durakViewModel.startListeningForDurakUpdates(it)
                    durakViewModel.startListeningForStartingPlayer(it)
                }
                remainingCardsViewModel.value?.let {
                    remainingCards = it
                }
                if (allSelectedCards.size == 12) {
                    durakViewModel.passToBita()
                }
            }

            durak.value?.kozr?.let {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (remainingCards.isNotEmpty()) {
                        CardStyle(
                            modifier = Modifier
                                .width(60.dp)
                                .height(90.dp)
                                .rotate(80f)
                                .offset(y = (10).dp, x = (-25).dp),
                            card = remainingCards.last()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(90.dp)
                                .rotate(-15f)
                                .offset(y = (-40).dp, x = (15).dp)
                                .background(colorResource(id = R.color.whiteLight))
                                .alpha(0.5f),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(40.dp),
                                painter = painterResource(
                                    id = durak.value?.kozrSuit ?: R.drawable.job
                                ),
                                contentDescription = ""
                            )
                        }
                    }

                    if (remainingCards.size > 1) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(75.dp)
                                    .offset(x = (20).dp, y = (-0).dp), painter = painterResource(
                                    id = durakData.tableOwner?.skinSettings?.cardBackPicked?.image
                                        ?: R.drawable.cardback_black
                                ), contentDescription = ""
                            )
                            Text(text = "${remainingCards.size - 1}", color = Color.White)
                        }
                    }

                }
            }


            Column(modifier = Modifier.align(Alignment.Center)) {
                val selectedCard = durak.value?.placeOnTable

                fun yereKartDus(
                    cardPair: CardPair,
                    cardOnTable: CardPair = CardPair(),
                    perevod: Boolean = false,
                    firstCard: CardPair? = null,
                    firstAttackCard: CardPair? = null,
                    secondCard: CardPair? = null,
                    secondAttackCard: CardPair? = null,
                    thirdCard: CardPair? = null,
                    thirdAttackCard: CardPair? = null,
                    fourthCard: CardPair? = null,
                    fourthAttackCard: CardPair? = null,
                    fifthCard: CardPair? = null,
                    fifthAttackCard: CardPair? = null,
                    sixthCard: CardPair? = null,
                    sixthAttackCard: CardPair? = null,
                ) {
                    val placeOnTable = durak.value?.placeOnTable

                    val modifiedPlaceOnTable = placeOnTable?.copy(
                        first = firstCard ?: placeOnTable.first,
                        firstAttack = firstAttackCard ?: placeOnTable.firstAttack,
                        second = secondCard ?: placeOnTable.second,
                        secondAttack = secondAttackCard ?: placeOnTable.secondAttack,
                        third = thirdCard ?: placeOnTable.third,
                        thirdAttack = thirdAttackCard ?: placeOnTable.thirdAttack,
                        fourth = fourthCard ?: placeOnTable.fourth,
                        fourthAttack = fourthAttackCard ?: placeOnTable.fourthAttack,
                        fifth = fifthCard ?: placeOnTable.fifth,
                        fifthAttack = fifthAttackCard ?: placeOnTable.fifthAttack,
                        sixth = sixthCard ?: placeOnTable.sixth,
                        sixthAttack = sixthAttackCard ?: placeOnTable.sixthAttack,
                    )
                    durakViewModel.putCardOnTableConditions(
                        selectedCard = cardPair,
                        cardOnTable = cardOnTable,
                        context = context,
                        perevod = perevod,
                        placeOnTable = modifiedPlaceOnTable ?: PlaceOnTable()
                    ) {
                        navController.popBackStack()
                        navigateTo(navController, "durak_game", NavParam("durak_data", durakData))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box {
                        if (durakViewModel.isDragging.value && isAttacker && selectedCard?.first == null) {
                            EmptyCard(onDrop = { yereKartDus(it, firstCard = it) })
                        }
                        selectedCard?.first?.let { card ->
                            CardStyle(card = card,
                                onDropState = selectedCard.firstAttack == null && !isAttacker,
                                onDrop = {
                                    yereKartDus(
                                        it, firstAttackCard = it, cardOnTable = card
                                    )
                                })
                        }
                        selectedCard?.firstAttack?.let { card ->
                            CardStyle(
                                modifier = Modifier
                                    .rotate(6F)
                                    .offset(x = (8).dp), card = card
                            )
                        }
                    }
                    Box {
                        if (durakViewModel.isDragging.value &&
                            selectedCard?.first?.number == durakViewModel.dropCard.value?.number &&
                            selectedCard?.firstAttack == null && !isAttacker
                        ) {
                            EmptyCard(onDrop = { yereKartDus(it, secondCard = it, perevod = true) })
                        } else if (durakViewModel.isDragging.value && isAttacker && selectedCard?.first != null && selectedCard.second == null) {
                            EmptyCard(onDrop = { yereKartDus(it, secondCard = it) })
                        }
                        selectedCard?.second?.let { card ->
                            CardStyle(card = card,
                                onDropState = selectedCard.secondAttack == null && !isAttacker,
                                onDrop = {
                                    yereKartDus(it, secondAttackCard = it, cardOnTable = card)
                                })
                        }
                        selectedCard?.secondAttack?.let { card ->
                            CardStyle(
                                modifier = Modifier
                                    .rotate(6F)
                                    .offset(x = (8).dp), card = card
                            )
                        }
                    }
                    Box {
                        if (durakViewModel.isDragging.value &&
                            selectedCard?.second?.number == durakViewModel.dropCard.value?.number &&
                            selectedCard?.firstAttack == null &&
                            !isAttacker
                        ) {
                            EmptyCard(onDrop = { yereKartDus(it, thirdCard = it, perevod = true) })
                        }
                        if (durakViewModel.isDragging.value && isAttacker && selectedCard?.second != null && selectedCard.third == null) {
                            EmptyCard(onDrop = {
                                yereKartDus(it, thirdCard = it)
                            })
                        }
                        selectedCard?.third?.let { card ->
                            CardStyle(card = card,
                                onDropState = selectedCard.thirdAttack == null && !isAttacker,
                                onDrop = {
                                    yereKartDus(it, thirdAttackCard = it, cardOnTable = card)
                                })
                        }
                        selectedCard?.thirdAttack?.let { card ->
                            CardStyle(
                                modifier = Modifier
                                    .rotate(6F)
                                    .offset(x = (8).dp), card = card
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box {
                        if (durakViewModel.isDragging.value &&
                            selectedCard?.third?.number == durakViewModel.dropCard.value?.number &&
                            selectedCard?.secondAttack == null &&
                            !isAttacker
                        ) {
                            EmptyCard(onDrop = { yereKartDus(it, fourthCard = it, perevod = true) })
                        }
                        if (durakViewModel.isDragging.value && isAttacker && selectedCard?.third != null && selectedCard.fourth == null) {
                            EmptyCard(onDrop = {
                                yereKartDus(it, fourthCard = it)
                            })
                        }
                        selectedCard?.fourth?.let { card ->
                            CardStyle(card = card,
                                onDropState = selectedCard.fourthAttack == null && !isAttacker,
                                onDrop = {
                                    yereKartDus(it, fourthAttackCard = it, cardOnTable = card)
                                })
                        }
                        selectedCard?.fourthAttack?.let { card ->
                            CardStyle(
                                modifier = Modifier
                                    .rotate(6F)
                                    .offset(x = (8).dp), card = card
                            )
                        }
                    }
                    Box {
                        if (durakViewModel.isDragging.value && isAttacker && selectedCard?.fourth != null && selectedCard.fifth == null) {
                            EmptyCard(onDrop = {
                                yereKartDus(it, fifthCard = it)
                            })
                        }
                        selectedCard?.fifth?.let { card ->
                            CardStyle(card = card,
                                onDropState = selectedCard.fifthAttack == null && !isAttacker,
                                onDrop = {
                                    yereKartDus(it, fifthAttackCard = it, cardOnTable = card)
                                })
                        }
                        selectedCard?.fifthAttack?.let { card ->
                            CardStyle(
                                modifier = Modifier
                                    .rotate(6F)
                                    .offset(x = (8).dp), card = card
                            )
                        }
                    }
                    Box {
                        if (durakViewModel.isDragging.value && isAttacker && selectedCard?.fifth != null && selectedCard.sixth == null) {
                            EmptyCard(onDrop = {
                                yereKartDus(it, sixthCard = it)
                            })
                        }
                        selectedCard?.sixth?.let { card ->
                            CardStyle(card = card,
                                onDropState = selectedCard.sixthAttack == null && !isAttacker,
                                onDrop = {
                                    yereKartDus(it, sixthAttackCard = it, cardOnTable = card)
                                })
                        }
                        selectedCard?.sixthAttack?.let { card ->
                            CardStyle(
                                modifier = Modifier
                                    .rotate(6F)
                                    .offset(x = (8).dp), card = card
                            )
                        }
                    }
                }
            }

            Column(
                modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val cardWidth = 130.dp
                val numCards = cards.size
                val totalCardsWidth = numCards * cardWidth
                val totalSpacingWidth = screenWidth - totalCardsWidth
                val horizontalSpacing =
                    if (numCards > 1) totalSpacingWidth / (numCards - 1) else 0.dp
                val isYourTurn = durak.value?.startingPlayer == currentPlayer.userData?.email


                Box(modifier = Modifier.fillMaxSize()) {
                    if (durak.value?.cardsOnHands == true) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 130.dp)
                        ) {
                            TableDesign(
                                durakViewModel = durakViewModel,
                                userViewModel = userViewModel,
                                table = currentPlayer,
                                tableNumber = currentPlayer.tableNumber ?: 0,
                                starter = currentPlayer.tableNumber ?: 0,
                                hide = currentPlayer.tableNumber == null,
                                navController = navController,
                                standUp = false
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (cards.size > 2) Arrangement.spacedBy(
                            horizontalSpacing
                        ) else Arrangement.Center
                    ) {
                        items(cards) { card ->
                            DragTarget(
                                modifier = Modifier,
                                durakViewModel = durakViewModel,
                                dataToDrop = card,
                                onDragStart = {
                                    buttonsVisibility = false
                                },
                                onDragEnd = {
                                    buttonsVisibility = true
                                },
                                content = {
                                    PlayerCardStyle(
                                        modifier = Modifier.offset(y = 80.dp),
                                        card = card,
                                    )
                                },
                            )
                        }
                    }

                    if (isAttacker && isYourTurn && allSelectedCards.isNotEmpty()) {
                        LaunchedEffect(true) {
                            delay(2000)
                            buttonsVisibility = true
                        }
                        if (buttonsVisibility){
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = 130.dp)
                            ) {
                                Button(colors = ButtonDefaults.buttonColors(
                                    MaterialTheme.colorScheme.background
                                ),enabled = buttonsDelay, onClick = {
                                    buttonsVisibility = false
                                    scope.launch {
                                        delay(3.seconds)
                                        buttonsVisibility = true
                                    }
                                    durakViewModel.passToBita()
                                }) {
                                    Text(text = stringResource(id = R.string.pass), color = Color.White)
                                }
                            }
                        }

                    } else if (!isAttacker && isYourTurn && allSelectedCards.isNotEmpty()) {

                        if (buttonsVisibility) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = 130.dp)
                            ) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        MaterialTheme.colorScheme.background
                                    ),
                                    enabled = buttonsDelay, onClick = {
                                    buttonsVisibility = false
                                    scope.launch {
                                        delay(3.seconds)
                                        buttonsVisibility = true
                                    }
                                    durakViewModel.takeCardsToHand()
                                }) {
                                    Text(text = stringResource(id = R.string.take), color = Color.White)
                                }
                            }
                        }

                    }


                }


            }

        }

    }

}