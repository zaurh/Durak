package com.example.birlik.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.birlik.R
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.CardPair
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.presentation.viewmodel.UserViewModel

@Composable
fun TableDesign(
    userViewModel: UserViewModel,
    table: PlayerData?,
    tableNumber: Int,
    starter: Int,
    image: String
) {
    val currentUser = userViewModel.currentUserId
    val playerData = userViewModel.playerData.collectAsState()
    val durak = userViewModel.durakData.collectAsState()
    val userData = userViewModel.userData.collectAsState()
    val starterNumber = durak.value?.starterTableNumber
    val cardSize = table?.cards?.size
    

    LaunchedEffect(key1 = true){
//        durak.value?.let {
//            userViewModel.getPlayerData(durak.value ?: DurakData(), table?.playerId ?: "")
//        }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            Icon(
                modifier = Modifier
                    .clickable {
                        userViewModel.sitTable(
                            durakData = durak.value ?: DurakData(),
                            playerData = PlayerData(userData = userData.value, cards = null),
                            tableNumber = tableNumber
                        )
                    }
                    .size(50.dp)
                    .border(
                        width = 2.dp,
                        color = if (starterNumber == starter) Color.Green else Color.Transparent
                    ),
                painter = painterResource(id = R.drawable.chair),
                contentDescription = "",
                tint = if (table != null) Color.Black else Color.Gray
            )
            if (table != null) {
                Image(
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.TopCenter)
                        .clip(CircleShape),
                    painter = rememberImagePainter(data = image),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
            } else {
                Text(modifier = Modifier.align(Alignment.Center), text = "Boş", color = Color.White)
            }

        }
        Text(text = table?.userData?.username ?: "")
        if (table?.userData != userData.value){
            LazyRow{
                items(cardSize ?: 0){
                    Box(modifier = Modifier
                        .height(30.dp)
                        .width(25.dp)
                    ){
                        Image(modifier = Modifier.fillMaxSize(),painter = painterResource(id = R.drawable.cardback), contentDescription = "")
                    }
                }
            }
        }

    }
}