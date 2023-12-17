@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.data.local.CountryEntity
import com.example.birlik.presentation.screen.components.MySearchBar
import com.example.birlik.presentation.screen.components.SubjectItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CountryScreen(
    navController: NavController,
    countryEntity: CountryEntity
) {
   
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }, title = { Text(text = countryEntity.name ?: "Birlik") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column {
                    MySearchBar(Modifier.padding(start = 10.dp, end = 10.dp))
                }
                Spacer(modifier = Modifier.size(16.dp))
                LazyColumn{
                    item { 
                        SubjectItem(title = "Söhbət", showFlag = true, countryImage = countryEntity.image ?: 0, subjectImage = R.drawable.chat, lastMessage = "@filankes: Salamlar", time = "09:42 AM")
                        SubjectItem(title = "Nəqliyyat bazarı", countryImage = countryEntity.image ?: 0, subjectImage = R.drawable.car, lastMessage = "@isa: bilmirem, gorum ney...", time = "10:22 PM")
                        SubjectItem(title = "İş yerləri", countryImage = countryEntity.image ?: 0, subjectImage = R.drawable.job, lastMessage = "@elnur: sabaha refer ede...", time = "11:02 AM")
                        SubjectItem(title = "Yeməkxana", countryImage = countryEntity.image ?: 0, subjectImage = R.drawable.restaurant, lastMessage = "@jalal: en qeseng restor...", time = "07:41 PM")
                    }
                }
            }
        }
    )
}