package com.example.birlik.presentation.screen.components.durak

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.birlik.R
import com.example.birlik.common.NavParam
import com.example.birlik.common.navigateTo
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.durak.PlayerData
import com.example.birlik.presentation.viewmodel.CircularProgressViewModel
import com.example.birlik.presentation.viewmodel.UserViewModel

@Composable
fun TableDesign(
    userViewModel: UserViewModel,
    table: PlayerData?,
    tableNumber: Int,
    starter: Int,
    navController: NavController,
    hide: Boolean = false,
    standUp: Boolean = false
) {

    val durak = userViewModel.durakDataState.collectAsState()
    val userData = userViewModel.userDataState.collectAsState()
    val tableData = durak.value?.tableData
    val starterNumber = durak.value?.starterTableNumber
    val cardSize = table?.cards?.size
    val context = LocalContext.current
    var profileDropdown by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val durakTables = userViewModel.durakTables

        val userInGame =
            durakTables.value.any { it.playerData?.any { it.userData?.username == userData.value?.username } == true }

        if (!hide) {
            Box {
                val tableSkin = table?.userData?.skinSettings?.tablePicked?.image

                if (table != null) {
                    Row() {
                        Column() {
                            val viewModel: CircularProgressViewModel = hiltViewModel()
                            val decreaseSec = viewModel.decreaseSecond.observeAsState()



                            Box(contentAlignment = Alignment.Center){
                                if (starterNumber == starter){
                                    DecreaseCircularProgressBar(
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
                                        painter = rememberImagePainter(data = table.userData?.image),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = ""
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = profileDropdown,
                                onDismissRequest = { profileDropdown = false }) {
                                DropdownMenuItem(onClick = {
                                    navigateTo(
                                        navController = navController,
                                        "profile_screen",
                                        NavParam("userData", table.userData ?: UserData())
                                    )
                                }) {
                                    Icon(
                                        modifier = Modifier.size(18.dp),
                                        imageVector = Icons.Default.Person,
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = "Profilinə bax", fontSize = 12.sp)
                                }
                                DropdownMenuItem(onClick = { /*TODO*/ }) {
                                    Icon(
                                        modifier = Modifier.size(18.dp),
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = "Hədiyyə yolla", fontSize = 12.sp)
                                }
                                val circularViewModel: CircularProgressViewModel = hiltViewModel()
                                DropdownMenuItem(onClick = {
                                   circularViewModel.decreaseSecond.value = 10
                                }) {
                                    Icon(
                                        modifier = Modifier.size(18.dp),
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = "Bəyən", fontSize = 12.sp)
                                }
                            }
                        }

                        if (standUp) {
                            IconButton(onClick = {
                                userViewModel.stoldanQalx(
                                    durakData = durak.value ?: DurakData(),
                                    userId = userData.value?.userId ?: ""
                                )
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
                    Icon(
                        modifier = Modifier
                            .clickable {
                                println(tableData?.firstTable)
                                println(tableData?.secondTable)
                                println(!userInGame)

                                if (tableData?.firstTable != userData.value && tableData?.secondTable != userData.value) {
                                    if ((userData.value?.cash ?: 0) >= (durak.value?.entryPriceCash
                                            ?: 0)
                                    ) {
                                        if (!userInGame) {
                                            userViewModel.stolaOtur(
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
                                                    "Başqa stolda oyun davam edir.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }

                                    } else {
                                        Toast
                                            .makeText(context, "Pul çatmır", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                            .size(50.dp),
                        imageVector = Icons.Default.Circle,
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Boş",
                        color = Color.White
                    )
                }

            }
            if (table?.userData?.username != userData.value?.username) {
                LazyRow {
                    items(cardSize ?: 0) {
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(25.dp)
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(
                                    id = table?.userData?.skinSettings?.cardBackPicked?.image
                                        ?: R.drawable.job
                                ),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }


    }
}