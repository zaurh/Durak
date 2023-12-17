@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.data.remote.CountryData
import com.example.birlik.data.toCountryEntity
import com.example.birlik.presentation.screen.components.CountryItem
import com.example.birlik.presentation.screen.components.MySearchBar
import com.example.birlik.presentation.viewmodel.RoomViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllCountiesScreen(
    navController: NavController,
    roomViewModel: RoomViewModel
) {
    val selectedCountries = roomViewModel.selectedCountries

    var countryList by remember {
        mutableStateOf(
            listOf(
                CountryData("Azərbaycan", R.drawable.az, "112 nəfər"),
                CountryData("Polşa", R.drawable.pl, "210 nəfər"),
                CountryData("Türkiyə", R.drawable.tr, "380 nəfər"),
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }, title = {
                Text(text = if (selectedCountries.isNotEmpty()) "${selectedCountries.size}" else "Ölkələr")
            })
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MySearchBar(modifier = Modifier.padding(start = 10.dp, end = 10.dp))
                    Spacer(modifier = Modifier.size(20.dp))
                    LazyColumn {
                        items(countryList) {
                            CountryItem(
                                countryEntity = it.toCountryEntity(),
                                roomViewModel = roomViewModel,
                                selectable = false,
                                navController = navController
                            )
                        }
                    }
                }
            }

        }
    )
}