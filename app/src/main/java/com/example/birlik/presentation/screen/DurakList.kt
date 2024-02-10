@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.birlik.R
import com.example.birlik.common.NavParam
import com.example.birlik.common.formatNumberWithK
import com.example.birlik.common.navigateTo
import com.example.birlik.common.roundToNearestPowerOfTen
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.remote.durak.Rules
import com.example.birlik.presentation.screen.components.durak.DurakTextField
import com.example.birlik.presentation.screen.components.durak.MySearchBar
import com.example.birlik.presentation.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DurakList(
    navController: NavController, userViewModel: UserViewModel
) {

    val userData = userViewModel.userDataState.collectAsState()
    val durakTables = userViewModel.durakTables.value

    val alertState = remember { mutableStateOf(false) }

    Box {

        Scaffold(floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(20.dp),
                contentColor = Color.White,
                containerColor = colorResource(id = R.color.dark_green),
                onClick = {
                    alertState.value = true
//                    userViewModel.clearSearch()
                }) {
                Icon(Icons.Filled.Add, "")
            }
        }, topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }, title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.birlik_cash),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = formatNumberWithK(value = userData.value?.cash ?: 0), fontSize = 20.sp)
                    Spacer(modifier = Modifier.size(16.dp))
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.birlik_coin),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = formatNumberWithK(value = userData.value?.coin ?: 0), fontSize = 20.sp)
                    Spacer(modifier = Modifier.size(16.dp))
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "",
                        tint = colorResource(
                            id = R.color.yellow
                        )
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = formatNumberWithK(value = userData.value?.rating ?: 0), fontSize = 20.sp)
                }

            }, actions = {
                IconButton(onClick = {
                    navController.navigate("durak_settings")
                }) {
                    Icon(imageVector = Icons.Default.Storefront, contentDescription = "")
                }
            })
        }, content = {

            Column(
                modifier = Modifier.padding(it), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (alertState.value) {
                    AlertDurak(
                        onDismiss = { alertState.value = false },
                        onSuccess = { alertState.value = false },
                        userViewModel = userViewModel,
                        userData = userData.value ?: UserData()
                    )
                }
                LazyColumn {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MySearchBar(
                                modifier = Modifier.weight(9f),
                                userViewModel = userViewModel,
                                onSearch = userViewModel::searchList
                            )
                            IconButton(modifier = Modifier
                                .weight(2f), onClick = {
                                    navController.navigate("leaderboard")
                            }) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "",
                                    tint = colorResource(
                                        id = R.color.yellow
                                    )
                                )
                            }
                        }
                    }
                    items(durakTables) {
                        DurakTableItem(durakData = it, navController = navController)
                    }
                }
            }

        })
    }


}

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
                    Text(text = "${durakData.title}", fontSize = 18.sp, color = Color.White)
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
                                    painter = rememberImagePainter(data = firstTable.image ?: ""),
                                    contentDescription = ""
                                )
                                Image(
                                    modifier = Modifier.size(50.dp), painter = rememberImagePainter(
                                        data = firstTable.skinSettings?.tablePicked?.image ?: ""
                                    ), contentDescription = ""
                                )

                            }
                        } else {
                            Image(
                                modifier = Modifier.size(50.dp),
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
                                    painter = rememberImagePainter(data = secondTable.image ?: ""),
                                    contentDescription = ""
                                )
                                Image(
                                    modifier = Modifier.size(50.dp), painter = rememberImagePainter(
                                        data = secondTable.skinSettings?.tablePicked?.image ?: ""
                                    ), contentDescription = ""
                                )

                            }
                        } else {
                            Image(
                                modifier = Modifier.size(50.dp),
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
//                            text = if (durakData.entryPriceCash == 0.toLong()) "Pulsuz" else "${durakData.entryPriceCash}",
                            text =
                            if (durakData.entryPriceCash == 0.toLong() && durakData.entryPriceCoin == 0.toLong()) "Pulsuz"
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
                            tint = Color.Green
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

//                Text(text = "Hiylə: ${durakData.rules?.cheat}")
//                Text(text = "Ötürmə: ${durakData.rules?.perevod}")
//                Text(text = "Stol sayı: ${durakData.rules?.tableSize}")
//                Text(text = "Stol qurucusu: ${durakData.tableOwner?.username}")
//                Text(text = "Giriş qiyməti: ${durakData.entryPrice}")
            }
        }
    }
}


@Composable
private fun AlertDurak(
    onDismiss: () -> Unit, onSuccess: () -> Unit, userViewModel: UserViewModel, userData: UserData
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var perevod by remember { mutableStateOf(true) }
//    var tableSize by remember { mutableStateOf(2) }
    var cheat by remember { mutableStateOf(false) }

    var table1 by remember { mutableStateOf(true) }
    var table2 by remember { mutableStateOf(true) }
    var table3 by remember { mutableStateOf(false) }
    var table4 by remember { mutableStateOf(false) }

    val tableSize = listOf(table1, table2, table3, table4).count { it }

    var tablePrice by remember { mutableStateOf("") }


    val durakTables = userViewModel.durakTables.value


    Dialog(
        onDismissRequest = { onDismiss() }, content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clip(shape = RoundedCornerShape(10))
            ) {
                val focus = LocalFocusManager.current
                var pickedSkin by remember { mutableStateOf(userData.skinSettings?.backgroundPicked?.image) }
                var moneyMethod by remember { mutableStateOf(false) }

                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(
                        id = pickedSkin ?: R.drawable.background_green
                    ),
                    contentDescription = ""
                )

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Stolun adı", color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    DurakTextField(
                        value = title,
                        onValueChange = { title = it },
                        onDone = {
                            focus.clearFocus()
                        },
                        placeHolder = "Ad seç"
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Qaydalar", color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row() {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = ""
                            )
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        perevod = !perevod
                                    },
                                painter = painterResource(id = R.drawable.rule_perevod),
                                contentDescription = "",
                                tint = if (perevod) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = ""
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {

                                    },
                                painter = painterResource(id = R.drawable.rule_cheat),
                                contentDescription = "",
                                tint = if (cheat) Color.Green else Color.Gray

                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Stol sayı", color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row() {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = ""
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {

                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table1) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = ""
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {

                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table2) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = ""
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
//                                        table3 = !table3
                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table3) Color.Green else Color.Gray
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(60.dp),
                                imageVector = Icons.Default.Circle,
                                contentDescription = ""
                            )

                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
//                                        table4 = !table4
                                    },
                                painter = painterResource(id = R.drawable.tableskin_black_simple),
                                contentDescription = "",
                                tint = if (table4) Color.Green else Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Giriş qiyməti", color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    DurakTextField(
                        value = tablePrice,
                        onValueChange = {
                            if (it.length <= 18) {
                                tablePrice = it.filter { it.isDigit() }
                            }
                        },
                        onDone = {
                            focus.clearFocus()
                        },
                        placeHolder = if (tablePrice == "") "Pulsuz" else "",
                        leadingIcon = painterResource(id = if (moneyMethod) R.drawable.birlik_coin else R.drawable.birlik_cash),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        changeMoney = {
                            moneyMethod = !moneyMethod
                            tablePrice = ""
                        }
                    )
                    val cash = roundToNearestPowerOfTen(userData.cash ?: 0).toFloat()
                    val coin = roundToNearestPowerOfTen(userData.coin ?: 0).toFloat()

                    Slider(
                        steps = 4, // Set the number of desired steps
                        valueRange = 0f..if (moneyMethod) coin else cash,
                        value = if (tablePrice == "") 0.toFloat() else tablePrice.toFloat(),
                        onValueChange = {
                            tablePrice = it.toInt().toString()
                        }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Arxa plan", color = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        val backgroundSkins = userData.skinSettings?.ownBackgroundSkins

                        for (skin in backgroundSkins ?: mutableListOf()) {
                            Image(modifier = Modifier
                                .clickable {
                                    pickedSkin = skin.image
                                    userData.let {
                                        userViewModel.changeSkin(
                                            userData = it.copy(
                                                skinSettings = it.skinSettings?.copy(
                                                    backgroundPicked = skin
                                                )
                                            ),
                                        )
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (pickedSkin == skin.image) Color.White else Color.Transparent
                                )
                                .padding(8.dp),
                                painter = painterResource(
                                    id = skin.image ?: R.drawable.background_green
                                ),
                                contentDescription = "")
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(onClick = {
                        val sufficientFunds = if (moneyMethod) {
                            userData.coin >= if (tablePrice.isEmpty()) 0 else tablePrice.toLong()
                        } else {
                            userData.cash >= if (tablePrice.isEmpty()) 0 else tablePrice.toLong()
                        }

                        if (sufficientFunds) {
                            userViewModel.oyunuBaslat(
                                title = title.ifEmpty { "Adsız stol" },
                                rules = Rules(cheat, tableSize, perevod),
                                entryPriceCash = if (!moneyMethod) if (tablePrice == "") 0 else tablePrice.toLong() else 0,
                                entryPriceCoin = if (moneyMethod) if (tablePrice == "") 0 else tablePrice.toLong() else 0,
                                context = context
                            )
                            onSuccess()
                            }
                         else {
                            Toast.makeText(context, "Pul çatmır", Toast.LENGTH_SHORT).show()
                        }

                    }) {
                        Text(text = "Başlat")
                    }

                }
            }


        })
}

