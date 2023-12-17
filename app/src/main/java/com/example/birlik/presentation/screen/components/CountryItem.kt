@file:OptIn(ExperimentalFoundationApi::class)

package com.example.birlik.presentation.screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.birlik.common.NavParam
import com.example.birlik.common.navigateTo
import com.example.birlik.data.local.CountryEntity
import com.example.birlik.data.remote.CountryData
import com.example.birlik.data.toCountryEntity
import com.example.birlik.presentation.viewmodel.RoomViewModel

@Composable
fun CountryItem(
    navController: NavController,
    countryEntity: CountryEntity,
    roomViewModel: RoomViewModel,
    selectable: Boolean
) {
    val selectedCountries = roomViewModel.selectedCountries

    Row(
        Modifier
            .background(
                if (selectedCountries.contains(countryEntity)) {
                    Color.LightGray
                } else {
                    Color.Transparent
                }
            )
            .combinedClickable(
                onClick = {
                    if (selectedCountries.isNotEmpty()) {
                        if (selectedCountries.contains(countryEntity)) {
                            selectedCountries.remove(countryEntity)
                        } else {
                            selectedCountries.add(countryEntity)
                        }
                    } else {
                        navigateTo(
                            navController,
                            "country_screen",
                            NavParam("countryEntity", countryEntity)
                        )
                    }
                },
                onLongClick = {
                    if (selectable){
                        if (selectedCountries.contains(countryEntity)) {
                            selectedCountries.remove(countryEntity)
                        } else {
                            selectedCountries.add(countryEntity)
                        }
                    }
                },
            )
            .fillMaxWidth()
            .padding(20.dp)
            .height(50.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = rememberImagePainter(data = countryEntity.image), contentDescription = "")
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "${countryEntity.name}")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${countryEntity.users}")
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = ">")
        }

    }

}