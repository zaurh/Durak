@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.durak.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.durak.R
import com.zaurh.durak.presentation.screen.components.durak.LeaderUserItem
import com.zaurh.durak.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeaderboardScreen(
    navController: NavController, userViewModel: UserViewModel
) {
    val allUsers = userViewModel.allUsers.value
    val userData = userViewModel.userDataState.collectAsState()

    val allUsersCash = allUsers.sortedByDescending { it.cash }
    val allUsersCoin = allUsers.sortedByDescending { it.coin }
    val allUsersRating = allUsers.sortedByDescending { it.rating }

    var selectedCash by remember { mutableStateOf(false) }
    var selectedCoin by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
        }, title = { Text(text = stringResource(id = R.string.leaderboard)) })
    }, content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRating) colorResource(id = R.color.green) else MaterialTheme.colorScheme.tertiary
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
                    Text(text = stringResource(id = R.string.rating), color = if (selectedRating) Color.White else MaterialTheme.colorScheme.primary)
                }
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCash) colorResource(id = R.color.green) else MaterialTheme.colorScheme.tertiary
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
                    Text(text = stringResource(id = R.string.cash), color = if (selectedCash) Color.White else MaterialTheme.colorScheme.primary)
                }
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCoin) colorResource(id = R.color.green) else MaterialTheme.colorScheme.tertiary
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
                    Text(text = stringResource(id = R.string.coin), color = if (selectedCoin) Color.White else MaterialTheme.colorScheme.primary)
                }
            }
            LazyColumn {
                itemsIndexed(
                    if (selectedCash) allUsersCash
                    else if (selectedCoin) allUsersCoin
                    else if (selectedRating) allUsersRating
                    else allUsers
                ) { index, data ->
                    Row {
                        LeaderUserItem(
                            index = index,
                            userData = data,
                            currentUser = data == userData.value,
                            navController = navController,
                            rating = selectedRating,
                            cash = selectedCash,
                            coin = selectedCoin
                        )
                    }

                }
            }
        }

    }
    )
}