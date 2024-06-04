package com.zaurh.durak.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.zaurh.durak.R
import com.zaurh.durak.common.NavParam
import com.zaurh.durak.common.navigateTo
import com.zaurh.durak.data.remote.UserData


@Composable
fun LeaderUserItem(
    index: Int,
    currentUser: Boolean,
    userData: UserData,
    navController: NavController,
    rating: Boolean,
    cash: Boolean,
    coin: Boolean
) {
    Card(modifier = Modifier.padding(8.dp), colors = CardDefaults.cardColors(
        containerColor = if (currentUser) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.tertiary
    )) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    navigateTo(navController, "profile_screen", NavParam("userData", userData))
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (index < 3) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp),
                            tint = if (index == 0) colorResource(id = R.color.yellow) else if (index == 1) Color.Gray else if (index == 2) colorResource(
                                id = R.color.bronze
                            ) else Color.Gray
                        )
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = if (index == 0) "1" else if (index == 1) "2" else if (index == 2) "3" else "",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
                Spacer(modifier = Modifier.size(8.dp))
                Column() {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        painter = rememberImagePainter(data = userData.image.takeUnless { it.isNullOrEmpty() }
                            ?: R.drawable.empty_profile),
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                    Text(text = "${userData.name}", color = MaterialTheme.colorScheme.primary)
                }
            }

            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End) {
                if (rating) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${userData.rating}", color = MaterialTheme.colorScheme.primary)
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "",
                            tint = colorResource(
                                id = R.color.yellow
                            )
                        )
                    }
                } else if (cash) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${userData.cash}", color = MaterialTheme.colorScheme.primary)
                        Image(
                            modifier = Modifier
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.birlik_cash),
                            contentDescription = ""
                        )
                    }
                } else if (coin) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${userData.coin}", color = MaterialTheme.colorScheme.primary)
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