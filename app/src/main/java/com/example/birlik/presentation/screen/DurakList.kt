package com.example.birlik.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.birlik.common.NavParam
import com.example.birlik.common.navigateTo
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.viewmodel.UserViewModel

@Composable
fun DurakList(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val durakTables = userViewModel.durakTables.value

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn{
            items(durakTables){
                DurakTableItem(durakData = it, navController = navController)
            }
        }
    }
}

@Composable
fun DurakTableItem(durakData: DurakData, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navigateTo(navController, "durak_game", NavParam("durak_data", durakData))
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = durakData.gameId.toString())
    }
}