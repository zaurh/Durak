@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.birlik.presentation.screen.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.birlik.R

@Composable
fun MySearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
//    restaurantViewModel: RestaurantViewModel
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var text by rememberSaveable { mutableStateOf("") }


    Box(modifier = modifier) {
        BackHandler(enabled = text.isNotEmpty(),onBack = {
            text = ""
//            restaurantViewModel.clearSearch()
            focusManager.clearFocus()
        })
        TextField(
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "")},
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Axtar")},
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier
                .clip(RoundedCornerShape(20))
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .shadow(5.dp)
                .background(colorResource(id = R.color.light_grey))
        )
    }
}