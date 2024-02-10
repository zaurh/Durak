package com.example.birlik.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.birlik.R
import com.example.birlik.common.NavParam
import com.example.birlik.common.navigateTo
import com.example.birlik.data.remote.UserData

@Composable
fun LeaderUserItem(
    userData: UserData,
    navController: NavController,
    rating: Boolean,
    cash: Boolean,
    coin: Boolean
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    navigateTo(navController, "profile_screen", NavParam("userData", userData))
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Column() {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    painter = rememberImagePainter(data = userData.image),
                    contentDescription = ""
                )
                Text(text = "${userData.username}")
            }
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End) {
                if (rating){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${userData.rating}")
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "",
                            tint = colorResource(
                                id = R.color.yellow
                            )
                        )
                    }
                }else if (cash){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${userData.cash}")
                        Image(
                            modifier = Modifier
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.birlik_cash),
                            contentDescription = ""
                        )
                    }
                }else if (coin){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${userData.coin}")
                        Image(
                            modifier = Modifier
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.birlik_coin),
                            contentDescription = ""
                        )
                    }
                }
            }

        }
    }

}