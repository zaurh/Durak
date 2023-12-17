package com.example.birlik.presentation.screen.components.durak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.birlik.R
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.presentation.viewmodel.UserViewModel

@Composable
fun UserItem(
    userData: UserData, durakData: DurakData, onGetCard: () -> Unit, userViewModel: UserViewModel
) {
    val remainingCardsViewModel = userViewModel.remainingCards.observeAsState(listOf())
    val context = LocalContext.current
    val currentPlayer = durakData.playerData?.find { it.userData?.username == userData.username }
    val currentPlayerCardSize = currentPlayer?.cards?.size
    var result by remember { mutableStateOf("") }
    val durak = userViewModel.durakData.collectAsState()

    val allPlayers = durak.value?.playerData

    val allPlayersExceptCurrent =
        allPlayers?.filter { it.userData?.username != currentPlayer?.userData?.username }
            ?: emptyList()

    val allPlayersCardSize = allPlayersExceptCurrent.map { it.cards?.size }

    val allSelectedCards =
        durakData.playerData?.flatMap { it.selectedCard ?: emptyList() } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val firstTable = durak.value?.tableData?.firstTable
        val firstImage = durak.value?.tableData?.firstTable?.image
        val firstUser =
            durak.value?.playerData?.find { it.userData?.username == firstTable?.username }

        val secondTable = durak.value?.tableData?.secondTable
        val secondImage = durak.value?.tableData?.secondTable?.image
        val secondUser =
            durak.value?.playerData?.find { it.userData?.username == secondTable?.username }

        TableDesign(
            userViewModel = userViewModel,
            table = PlayerData(
                userData = firstTable,
                playerId = firstTable?.userId,
                cards = firstUser?.cards
            ),
            tableNumber = 1,
            starter = 1,
            image = firstImage ?: ""
        )

        TableDesign(
            userViewModel = userViewModel,
            table = PlayerData(
                userData = secondTable,
                playerId = secondTable?.userId,
                cards = secondUser?.cards
            ),
            tableNumber = 2,
            starter = 2,
            image = secondImage ?: ""
        )


        Box(Modifier.fillMaxSize()) {
            durakData.kozr?.let {
                CardStyle(
                    modifier = Modifier
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
                                if (durak.value?.attacker != userData.username || (allPlayersCardSize.all {
                                        (it ?: 0) >= 6
                                    })) {
                                    userViewModel.kozrGotur(durakData)
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

                    }
                }
            }
            if (durak.value?.started == true) {
                if (remainingCardsViewModel.value.isNotEmpty()) {
                    Card(
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
                                        if (durak.value?.attacker != userData.username || (allPlayersCardSize.all {
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
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Inside,
                                painter = painterResource(id = R.drawable.cardback),
                                contentDescription = ""
                            )
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = durakData.cards.size.toString(),
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

        }


        LazyRow(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
            itemsIndexed(durakData.selectedCards) { index, card ->
                CardStyle(
                    card = card,
                    modifier = Modifier
                        .rotate(if (index % 2 == 0) 0F else 15F)
                        .offset(x = if (index % 2 == 0) (0).dp else (-30).dp)
                )
            }
        }

//            for (player in durakData.playerData ?: mutableListOf()) {
//                player.selectedCard?.let { cards ->
//                    LazyRow {
//                        itemsIndexed(cards) { index, card ->
//                            CardStyle(
//                                card = card,
//                                modifier = Modifier
//                                    .rotate(if (index % 2 == 0) 0F else 15F)
//                                    .padding(end = if (index % 2 == 0) 8.dp else 0.dp)
//                            )
//                        }
//                    }
//                }
//            }


//            for (player in durakData.playerData!!) {
//                if (player != currentPlayer) {
//                    Row() {
//                        player.selectedCard?.forEach { card ->
//                            CardStyle(card = card)
//                        }
//                    }
//                } else {
//                    Row() {
//                        player.selectedCard?.forEach { card ->
//                            CardStyle(
//                                card = card, modifier = Modifier
//                                    .offset(x = (15).dp)
//                                    .rotate(15F)
//                            )
//                        }
//                    }
//                }
//            }
    }

}

