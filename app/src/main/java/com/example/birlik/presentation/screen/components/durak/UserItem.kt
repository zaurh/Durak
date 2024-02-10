package com.example.birlik.presentation.screen.components.durak

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.durak.PlaceOnTable
import com.example.birlik.presentation.viewmodel.UserViewModel

@Composable
fun UserItem(
    userData: UserData, durakData: DurakData, onGetCard: () -> Unit, userViewModel: UserViewModel, navContoller: NavController
) {
    val remainingCardsViewModel = userViewModel.remainingCardsRepo.observeAsState(listOf())
    val context = LocalContext.current
    val currentPlayer = durakData.playerData?.find { it.userData?.username == userData.username }
    val currentPlayerCardSize = currentPlayer?.cards?.size
    val placeOnTable = durakData.placeOnTable
    val selectedCardViewModel = userViewModel.selectedCardState.collectAsState()

    val allPlayers = durakData.playerData

    val allPlayersExceptCurrent =
        allPlayers?.filter { it.userData?.username != currentPlayer?.userData?.username }
            ?: emptyList()

    val allPlayersCardSize = allPlayersExceptCurrent.map { it.cards?.size }

    val allSelectedCards = durakData.playerData?.flatMap { it.selectedCard ?: emptyList() } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val firstTable = durakData.tableData?.firstTable
        val firstImage = durakData.tableData?.firstTable?.image
        val firstUser =
            durakData.playerData?.find { it.userData?.username == firstTable?.username }

        val secondTable = durakData?.tableData?.secondTable
        val secondImage = durakData.tableData?.secondTable?.image
        val secondUser =
            durakData.playerData?.find { it.userData?.username == secondTable?.username }

        TableDesign(
            userViewModel = userViewModel,
            table = firstUser?.copy(
                userData = firstTable,
                playerId = firstTable?.userId,
                cards = firstUser.cards
            ),
            tableNumber = 1,
            starter = 1,
            navController = navContoller,
            hide = (firstTable?.username == userData.username && durakData.started == true),
            standUp = firstTable?.username == userData.username
        )

        TableDesign(
            userViewModel = userViewModel,
            table = secondUser?.copy(
                userData = secondTable,
                playerId = secondTable?.userId,
                cards = secondUser.cards
            ),
            tableNumber = 2,
            starter = 2,
            navController = navContoller,
            hide = (secondTable?.username == userData.username && durakData.started == true),
            standUp = secondTable?.username == userData.username

        )


        Box(Modifier.fillMaxSize()) {
            durakData.kozr?.let {
                CardStyle(
                    modifier = Modifier
                        .width(90.dp)
                        .padding(start = 30.dp)
                        .align(Alignment.CenterStart)
                        .rotate(75F), card = durakData.kozr
                ) {
                    //will be fixed future
                    if (remainingCardsViewModel.value.isEmpty()) {
                        if (currentPlayerCardSize!! < 6) {
                            if (allSelectedCards.isNotEmpty()) {
                                Toast
                                    .makeText(
                                        context,
                                        "Hələ yox.",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else {
                                if ((durakData.attacker != userData.username || allPlayersCardSize.all {
                                        (it ?: 0) >= 6
                                    })) {
                                    onGetCard()
                                    userViewModel.kozrGotur(durakData)
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Əvvəl hücum edən götürməlidir.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "Əlində $currentPlayerCardSize kart var",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }

                    }
                }
            }
            if (durakData.started == true) {
                if (remainingCardsViewModel.value.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .width(50.dp)
                            .align(Alignment.CenterStart)
                            .clickable {
                                if (currentPlayerCardSize!! < 6) {
                                    if (allSelectedCards.isNotEmpty()) {
                                        Toast
                                            .makeText(
                                                context,
                                                "Hələ yox.",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } else {
                                        if (durakData.attacker != userData.username || (allPlayersCardSize.all {
                                                (it ?: 0) >= 6
                                            })) {
                                            onGetCard()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Əvvəl hücum edən götürməlidir.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Əlində $currentPlayerCardSize kart var",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            },
                    ) {
                        Box() {
                            val cardbackPicked = durakData.tableOwner?.skinSettings?.cardBackPicked?.image

                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = cardbackPicked ?: R.drawable.job),
                                contentDescription = ""
                            )
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = durakData.cards.size.toString(),
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

        }

        val card = durakData.placeOnTable

        val first = card?.first
        val firstAttack = card?.firstAttack
        val second = card?.second
        val secondAttack = card?.secondAttack
        val third = card?.third
        val thirdAttack = card?.thirdAttack
        val fourth = card?.fourth
        val fourthAttack = card?.fourthAttack
        val fifth = card?.fifth
        val fifthAttack = card?.fifthAttack
        val sixth = card?.sixth
        val sixthAttack = card?.sixthAttack



        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val attacker = durakData.attacker
            val currentUsername = userData.username
            val startingPlayer = durakData.startingPlayer
            val cardPair = selectedCardViewModel.value
            val kozrSuit = durakData.kozrSuit


            val firstTableCard = durakData.placeOnTable?.first
            val secondTableCard = durakData.placeOnTable?.second
            val thirdTableCard = durakData.placeOnTable?.third
            val fourthTableCard = durakData.placeOnTable?.fourth
            val fifthTableCard = durakData.placeOnTable?.fifth
            val sixthTableCard = durakData.placeOnTable?.sixth

            val isAttacker = attacker == currentUsername
            val isYourTurn = startingPlayer == currentUsername
            val yourCardIsKozr = cardPair?.suit == kozrSuit


            fun isHigherThan(card: CardPair?, suit: Int?, tableCard: CardPair?): Boolean {
                return if (card?.suit == suit) (card?.number?.plus(15) ?: 0) > (tableCard?.number
                    ?: 0)
                else (card?.number ?: 0) > (tableCard?.number ?: 0)
            }

            fun isSameSuit(card: CardPair?, tableCard: CardPair?): Boolean {
                return card?.suit == tableCard?.suit
            }

            val firstAttackCardIsHigherThanFirst = isHigherThan(cardPair, kozrSuit, firstTableCard)
            val firstAttackCardIsSameSuitWithFirst = isSameSuit(cardPair, firstTableCard)

            val secondAttackCardIsHigherThanSecond =
                isHigherThan(cardPair, kozrSuit, secondTableCard)
            val secondAttackCardIsSameSuitWithSecond = isSameSuit(cardPair, secondTableCard)

            val thirdAttackCardIsHigherThanThird = isHigherThan(cardPair, kozrSuit, thirdTableCard)
            val thirdAttackCardIsSameSuitWithThird = isSameSuit(cardPair, thirdTableCard)

            val fourthAttackCardIsHigherThanFourth =
                isHigherThan(cardPair, kozrSuit, fourthTableCard)
            val fourthAttackCardIsSameSuitWithFourth = isSameSuit(cardPair, fourthTableCard)

            val fifthAttackCardIsHigherThanFifth = isHigherThan(cardPair, kozrSuit, fifthTableCard)
            val fifthAttackCardIsSameSuitWithFifth = isSameSuit(cardPair, fifthTableCard)

            val sixthAttackCardIsHigherThanSixth = isHigherThan(cardPair, kozrSuit, sixthTableCard)
            val sixthAttackCardIsSameSuitWithSixth = isSameSuit(cardPair, sixthTableCard)


            val allSelected =
                allPlayers?.flatMap { it.selectedCard ?: listOf() }

            val currentSelected = currentPlayer?.selectedCard

            val selectedExceptCurrent =
                allSelected?.filterNot { currentSelected?.contains(it) == true }
                    ?: emptyList()

            val isOneCardLeft =
                selectedExceptCurrent.size - (currentSelected?.size ?: 0) <= 1

            val onTableSelected = durakData.selectedCards


            fun yereKartDus(
                rotate: Boolean,
                card: CardPair,
                firstAttackCard: CardPair? = null,
                secondAttackCard: CardPair? = null,
                thirdAttackCard: CardPair? = null,
                fourthAttackCard: CardPair? = null,
                fifthAttackCard: CardPair? = null,
                sixthAttackCard: CardPair? = null,
                changeAttacker: Boolean = false
            ) {
                val modifiedPlaceOnTable = placeOnTable?.copy(
                    firstAttack = firstAttackCard ?: placeOnTable.firstAttack,
                    secondAttack = secondAttackCard ?: placeOnTable.secondAttack,
                    thirdAttack = thirdAttackCard ?: placeOnTable.thirdAttack,
                    fourthAttack = fourthAttackCard ?: placeOnTable.fourthAttack,
                    fifthAttack = fifthAttackCard ?: placeOnTable.fifthAttack,
                    sixthAttack = sixthAttackCard ?: placeOnTable.sixthAttack,
                )

//                userViewModel.yereKartDus(
//                    selectedCard = card,
//                    rotate = rotate,
//                    placeOnTable = modifiedPlaceOnTable ?: PlaceOnTable(),
//                    changeAttacker = changeAttacker
//                )
            }

            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
                Box(){
                    first?.let {
                        CardStyle(
                            userViewModel = userViewModel,
                            card = it
                        ) {
                            val cardIsKozr = if (yourCardIsKozr) cardPair?.copy(
                                number = cardPair.number?.plus(15)
                            ) else cardPair

                            if (firstAttack == null) {
                                //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə tək kart qalıb. Kozr var
                                if (!isAttacker && isYourTurn && firstAttackCardIsHigherThanFirst && firstAttackCardIsSameSuitWithFirst && isOneCardLeft) {
                                    yereKartDus(
                                        rotate = true,
                                        card = cardIsKozr ?: CardPair(),
                                        firstAttackCard = cardIsKozr
                                    )
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                else if (!isAttacker && isYourTurn && firstAttackCardIsHigherThanFirst && firstAttackCardIsSameSuitWithFirst) {
                                    yereKartDus(
                                        rotate = false,
                                        card = cardIsKozr ?: CardPair(),
                                        firstAttackCard = cardIsKozr
                                    )
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                else if (!isAttacker && isYourTurn && yourCardIsKozr && isOneCardLeft && firstAttackCardIsHigherThanFirst) {
                                    yereKartDus(
                                        rotate = true,
                                        card = cardIsKozr ?: CardPair(),
                                        firstAttackCard = cardIsKozr
                                    )
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
                                else if (!isAttacker && isYourTurn && yourCardIsKozr && firstAttackCardIsHigherThanFirst) {
                                    yereKartDus(
                                        rotate = false,
                                        card = cardIsKozr ?: CardPair(),
                                        firstAttackCard = cardIsKozr
                                    )
                                }

                            }

                        }
                    }
                    firstAttack?.let {
                        CardStyle(
                            card = it, modifier = Modifier
                                .rotate(15F)
                                .offset(x = (20).dp)
                        )
                    }
                }
                Box(){
                    second?.let {
                        CardStyle(
                            userViewModel = userViewModel,
                            card = it
                        ) {
                            val cardIsKozr = if (yourCardIsKozr) cardPair?.copy(
                                number = cardPair.number?.plus(15)
                            ) else cardPair

                            if (secondAttack == null) {
                                //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə kart çoxdur. Kozr var
                                if (!isAttacker && isYourTurn && secondAttackCardIsHigherThanSecond && secondAttackCardIsSameSuitWithSecond && isOneCardLeft) {
                                    yereKartDus(
                                        rotate = true,
                                        card = cardIsKozr ?: CardPair(),
                                        secondAttackCard = cardIsKozr
                                    )
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                else if (!isAttacker && isYourTurn && secondAttackCardIsHigherThanSecond && secondAttackCardIsSameSuitWithSecond) {
                                    yereKartDus(
                                        rotate = false,
                                        card = cardIsKozr ?: CardPair(),
                                        secondAttackCard = cardIsKozr
                                    )
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                else if (!isAttacker && isYourTurn && yourCardIsKozr && isOneCardLeft && secondAttackCardIsHigherThanSecond) {
                                    yereKartDus(
                                        rotate = true,
                                        card = cardIsKozr ?: CardPair(),
                                        secondAttackCard = cardIsKozr
                                    )
                                }
                                //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
                                else if (!isAttacker && isYourTurn && yourCardIsKozr && secondAttackCardIsHigherThanSecond) {
                                    yereKartDus(
                                        rotate = false,
                                        card = cardIsKozr ?: CardPair(),
                                        secondAttackCard = cardIsKozr
                                    )
                                }
                            }

                        }
                    }
                    secondAttack?.let {
                        CardStyle(
                            card = it, modifier = Modifier
                                .rotate(15F)
                                .offset(x = (20).dp)
                        )
                    }
                }
                Box(){
                    third?.let {
                        CardStyle(userViewModel = userViewModel,
                            card = it, onClick = {

                                val cardIsKozr = if (yourCardIsKozr) cardPair?.copy(
                                    number = cardPair.number?.plus(15)
                                ) else cardPair

                                if (thirdAttack == null) {
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə kart çoxdur. Kozr var
                                    if (!isAttacker && isYourTurn && thirdAttackCardIsHigherThanThird && thirdAttackCardIsSameSuitWithThird && isOneCardLeft) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            thirdAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                    else if (!isAttacker && isYourTurn && thirdAttackCardIsHigherThanThird && thirdAttackCardIsSameSuitWithThird) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            thirdAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && isOneCardLeft && thirdAttackCardIsHigherThanThird) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            thirdAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && thirdAttackCardIsHigherThanThird) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            thirdAttackCard = cardIsKozr
                                        )
                                    }
                                }

                            })
                    }
                    thirdAttack?.let {
                        CardStyle(
                            card = it, modifier = Modifier
                                .rotate(15F)
                                .offset(x = (20).dp)
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.size(14.dp))
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
                Box(){
                    fourth?.let {
                        CardStyle(userViewModel = userViewModel,
                            card = it, onClick = {

                                val cardIsKozr = if (yourCardIsKozr) cardPair?.copy(
                                    number = cardPair.number?.plus(15)
                                ) else cardPair

                                if (fourthAttack == null) {
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə kart çoxdur. Kozr var
                                    if (!isAttacker && isYourTurn && fourthAttackCardIsHigherThanFourth && fourthAttackCardIsSameSuitWithFourth && isOneCardLeft) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            fourthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                    else if (!isAttacker && isYourTurn && fourthAttackCardIsHigherThanFourth && fourthAttackCardIsSameSuitWithFourth) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            fourthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && isOneCardLeft && fourthAttackCardIsHigherThanFourth) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            fourthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && fourthAttackCardIsHigherThanFourth) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            fourthAttackCard = cardIsKozr
                                        )
                                    }
                                }

                            })
                    }
                    fourthAttack?.let {
                        CardStyle(
                            card = it, modifier = Modifier
                                .rotate(15F)
                                .offset((20).dp)
                        )
                    }
                }
                Box() {
                    fifth?.let {
                        CardStyle(userViewModel = userViewModel,
                            card = it, onClick = {

                                val cardIsKozr = if (yourCardIsKozr) cardPair?.copy(
                                    number = cardPair.number?.plus(15)
                                ) else cardPair

                                if (fifthAttack == null) {
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə kart çoxdur. Kozr var
                                    if (!isAttacker && isYourTurn && fifthAttackCardIsHigherThanFifth && fifthAttackCardIsSameSuitWithFifth && isOneCardLeft) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            fifthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                    else if (!isAttacker && isYourTurn && fifthAttackCardIsHigherThanFifth && fifthAttackCardIsSameSuitWithFifth) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            fifthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && isOneCardLeft && fifthAttackCardIsHigherThanFifth) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            fifthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && fifthAttackCardIsHigherThanFifth) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            fifthAttackCard = cardIsKozr
                                        )
                                    }
                                }

                            })
                    }
                    fifthAttack?.let {
                        CardStyle(
                            card = it, modifier = Modifier
                                .rotate(15F)
                                .offset((20).dp)
                        )
                    }
                }
                Box() {
                    sixth?.let {
                        CardStyle(userViewModel = userViewModel,
                            card = it, onClick = {

                                val cardIsKozr = if (yourCardIsKozr) cardPair?.copy(
                                    number = cardPair.number?.plus(15)
                                ) else cardPair

                                if (sixthAttack == null) {
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir.Yerdə kart çoxdur. Kozr var
                                    if (!isAttacker && isYourTurn && sixthAttackCardIsHigherThanSixth && sixthAttackCardIsSameSuitWithSixth && isOneCardLeft) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            sixthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Rəqəm yerdəkindən böyükdür. Suitlər eynidir. Kozr var
                                    else if (!isAttacker && isYourTurn && sixthAttackCardIsHigherThanSixth && sixthAttackCardIsSameSuitWithSixth) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            sixthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Yerdə kart çoxdur. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && isOneCardLeft && sixthAttackCardIsHigherThanSixth) {
                                        yereKartDus(
                                            rotate = true,
                                            card = cardIsKozr ?: CardPair(),
                                            sixthAttackCard = cardIsKozr
                                        )
                                    }
                                    //Hücumcu sən deyilsən. Sıra səndədir. Kartın kozrdu. Rəqəm yerdəkindən böyükdür. Kozr var
                                    else if (!isAttacker && isYourTurn && yourCardIsKozr && sixthAttackCardIsHigherThanSixth) {
                                        yereKartDus(
                                            rotate = false,
                                            card = cardIsKozr ?: CardPair(),
                                            sixthAttackCard = cardIsKozr
                                        )
                                    }
                                }

                            })
                    }
                    sixthAttack?.let {
                        CardStyle(
                            card = it, modifier = Modifier
                                .rotate(15F)
                                .offset((-30).dp)
                        )
                    }
                }

            }
        }


    }

}
