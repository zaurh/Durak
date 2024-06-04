@file:OptIn(ExperimentalTextApi::class)

package com.zaurh.durak.presentation.screen.components.durak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.zaurh.durak.R
import com.zaurh.durak.common.NavParam
import com.zaurh.durak.common.navigateTo
import com.zaurh.durak.common.willBeSoonToast
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.DurakData
import com.zaurh.durak.data.remote.durak.PlayerData
import com.zaurh.durak.presentation.viewmodel.CircularProgressViewModel
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import com.zaurh.durak.presentation.viewmodel.UserViewModel


@Composable
fun TableDesign(
    userViewModel: UserViewModel,
    durakViewModel: DurakViewModel,
    table: PlayerData?,
    tableNumber: Int,
    starter: Int,
    navController: NavController,
    hide: Boolean = false,
    standUp: Boolean = false
) {
    val durak = durakViewModel.durakDataState.collectAsState()
    val userData = userViewModel.userDataState.collectAsState()
    val tableData = durak.value?.tableData
    val starterNumber = durak.value?.starterTableNumber
    val cardSize = table?.cards?.size
    val context = LocalContext.current
    var profileDropdown by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val durakTables = durakViewModel.durakTables
        val filteredDurakTables = durakTables.value.filter { !it.finished  }
        val userInGame =
            filteredDurakTables.any { it.playerData?.any { it.userData?.email == userData.value?.email } == true }

        if (!hide) {
            Box() {
                val tableSkin = table?.userData?.skinSettings?.tablePicked?.image

                if (table != null) {
                    Row() {
                        Column() {
                            val viewModel: CircularProgressViewModel = hiltViewModel()
                            val decreaseSec = viewModel.decreaseSecond.observeAsState()

                            Box(contentAlignment = Alignment.Center) {
                                if (starterNumber == starter) {
                                    CountdownBar(
                                        percentage = decreaseSec.value!!.toFloat()
                                    )
                                }
                                Box(
                                    modifier = Modifier.clickable {
                                        profileDropdown = true
                                    },
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .size(50.dp),
                                        painter = painterResource(
                                            id = tableSkin ?: R.drawable.tableskin_black_simple
                                        ),
                                        contentDescription = ""
                                    )

                                    Image(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape),
                                        painter = rememberImagePainter(data = table.userData?.image ?: R.drawable.empty_profile),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = ""
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = profileDropdown,
                                onDismissRequest = { profileDropdown = false }) {
                                DropdownMenuItem(text = {
                                    Row {
                                        Icon(
                                            modifier = Modifier.size(18.dp),
                                            imageVector = Icons.Default.Person,
                                            contentDescription = ""
                                        )
                                        Spacer(modifier = Modifier.size(4.dp))
                                        Text(text = stringResource(id = R.string.showProfile), fontSize = 12.sp)
                                    }
                                },onClick = {
                                    navigateTo(
                                        navController = navController,
                                        "profile_screen",
                                        NavParam("userData", table.userData ?: UserData())
                                    )
                                })
                                DropdownMenuItem(text = {
                                    Row {
                                        Icon(
                                            modifier = Modifier.size(18.dp),
                                            imageVector = Icons.Default.CardGiftcard,
                                            contentDescription = ""
                                        )
                                        Spacer(modifier = Modifier.size(4.dp))
                                        Text(text = stringResource(id = R.string.sendGift), fontSize = 12.sp)
                                    }

                                },onClick = {
                                    willBeSoonToast(context)
                                })
                                DropdownMenuItem(text = {
                                    Row {
                                        Icon(
                                            modifier = Modifier.size(18.dp),
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = ""
                                        )
                                        Spacer(modifier = Modifier.size(4.dp))
                                        Text(text = stringResource(id = R.string.like), fontSize = 12.sp)
                                    }

                                },onClick = {
                                    willBeSoonToast(context)
                                })
                            }
                        }

                        if (standUp) {
                            IconButton(onClick = {
                                durakViewModel.standUp()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "",
                                    tint = colorResource(
                                        id = R.color.red
                                    )
                                )
                            }
                        }
                    }


                } else {
                    Image(
                        modifier = Modifier
                            .clickable {
                                if (tableData?.firstTable != userData.value && tableData?.secondTable != userData.value) {
                                    if ((userData.value?.cash ?: 0) >= (durak.value?.entryPriceCash
                                            ?: 0)
                                    ) {
                                        if (!userInGame) {
                                            durakViewModel.sitDown(
                                                durakData = durak.value ?: DurakData(),
                                                playerData = PlayerData(
                                                    userData = userData.value,
                                                    cards = null
                                                ),
                                                tableNumber = tableNumber
                                            )
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    context.getString(R.string.youAreOnOtherTable),
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }

                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                context.getString(R.string.notEnoughMoney),
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                            }
                            .size(50.dp)
                            .alpha(0.5f),
                        painter = painterResource(id = R.drawable.tableskin_black_simple),
                        contentDescription = ""
                    )
                }

            }
            if (table?.userData?.userId != userData.value?.userId && table?.cards != null) {
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(
                            id = table.userData?.skinSettings?.cardBackPicked?.image
                                ?: R.drawable.job
                        ),
                        contentDescription = ""
                    )
                    Text(
                        text = "$cardSize",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }


    }
}