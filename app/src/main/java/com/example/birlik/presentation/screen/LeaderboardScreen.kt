@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.presentation.screen.components.durak.LeaderUserItem
import com.example.birlik.presentation.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeaderboardScreen(
    navController: NavController, userViewModel: UserViewModel
) {
    val allUsers = userViewModel.allUsers.value

    val allUsersCash = allUsers.sortedByDescending { it.cash }
    val allUsersCoin = allUsers.sortedByDescending { it.coin }
    val allUsersRating = allUsers.sortedByDescending { it.rating }

    var selectedCash by remember { mutableStateOf(false) }
    var selectedCoin by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(true) }

    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
        }, title = { Text(text = "Liderlər") })
    }, content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRating) colorResource(id = R.color.green) else Color.Gray
                ), onClick = {
                    selectedRating = true
                    selectedCash = false
                    selectedCoin = false
                }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "",
                        tint = colorResource(
                            id = R.color.yellow
                        )
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "Reytinq")
                }
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCash) colorResource(id = R.color.green) else Color.Gray
                ), onClick = {
                    selectedCash = true
                    selectedRating = false
                    selectedCoin = false
                }) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.birlik_cash),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "Cash")
                }
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCoin) colorResource(id = R.color.green) else Color.Gray
                ), onClick = {
                    selectedCoin = true
                    selectedRating = false
                    selectedCash = false
                }) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.birlik_coin),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "Coin")
                }
            }
            LazyColumn {
                items(
                    if (selectedCash) allUsersCash
                    else if (selectedCoin) allUsersCoin
                    else if (selectedRating) allUsersRating
                    else allUsers
                ) {
                    LeaderUserItem(userData = it,navController = navController, rating = selectedRating, cash = selectedCash, coin = selectedCoin)
                }
            }
        }

    }, bottomBar = {

    })
}