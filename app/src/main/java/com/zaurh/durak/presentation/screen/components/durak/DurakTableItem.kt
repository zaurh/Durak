package com.zaurh.durak.presentation.screen.components.durak

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.zaurh.durak.R
import com.zaurh.durak.common.NavParam
import com.zaurh.durak.common.navigateTo
import com.zaurh.durak.data.remote.durak.DurakData

@Composable
fun DurakTableItem(durakData: DurakData, navController: NavController) {
    val ownerSettings = durakData.tableOwner?.skinSettings
    val firstTable = durakData.tableData?.firstTable
    val secondTable = durakData.tableData?.secondTable

    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            navigateTo(navController, "durak_game", NavParam("durak_data", durakData))
        }
        .padding(5.dp)
        .height(150.dp)) {
        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(
                    id = ownerSettings?.backgroundPicked?.image ?: R.drawable.background_green
                ),
                contentDescription = ""
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "${durakData.title}", fontSize = 18.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row() {
                        if (firstTable != null) {
                            Box(
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape),
                                    painter = rememberImagePainter(data = firstTable.image.takeUnless { it.isNullOrEmpty() } ?: R.drawable.empty_profile),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop
                                )
                                Image(
                                    modifier = Modifier.size(50.dp), painter = rememberImagePainter(
                                        data = firstTable.skinSettings?.tablePicked?.image ?: ""
                                    ), contentDescription = ""
                                )

                            }
                        } else {
                            Image(
                                modifier = Modifier
                                    .size(50.dp)
                                    .alpha(0.5f),
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = ""
                            )
                        }
                        if (secondTable != null) {
                            Box(
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape),
                                    painter = rememberImagePainter(data = secondTable.image.takeUnless { it.isNullOrEmpty() } ?: R.drawable.empty_profile),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop
                                )
                                Image(
                                    modifier = Modifier.size(50.dp), painter = rememberImagePainter(
                                        data = secondTable.skinSettings?.tablePicked?.image ?: ""
                                    ), contentDescription = ""
                                )

                            }
                        } else {
                            Image(
                                modifier = Modifier
                                    .size(50.dp)
                                    .alpha(0.5f),
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = ""
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (durakData.entryPriceCash != 0.toLong() || durakData.entryPriceCoin != 0.toLong()) {
                            Image(
                                modifier = Modifier.size(26.dp),
                                painter = painterResource(
                                    id =
                                    if (durakData.entryPriceCoin == 0.toLong()) R.drawable.birlik_cash else R.drawable.birlik_coin
                                ),
                                contentDescription = ""
                            )
                        }

                        Text(
                            text =
                            if (durakData.entryPriceCash == 0.toLong() && durakData.entryPriceCoin == 0.toLong()) stringResource(
                                id = R.string.free
                            )
                            else if (durakData.entryPriceCash == 0.toLong()) "${durakData.entryPriceCoin}"
                            else if (durakData.entryPriceCoin == 0.toLong()) "${durakData.entryPriceCash}"
                            else "",
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    if (durakData.rules?.perevod == true) {
                        Icon(
                            painter = painterResource(id = R.drawable.rule_perevod),
                            contentDescription = "",
                            tint = colorResource(id = R.color.green)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    if (durakData.rules?.cheat == true) {
                        Icon(
                            painter = painterResource(id = R.drawable.rule_cheat),
                            contentDescription = "",
                            tint = Color.Green
                        )
                    }

                }

            }
        }
    }

}