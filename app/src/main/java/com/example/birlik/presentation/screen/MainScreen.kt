@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.birlik.presentation.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.screen.components.CountryItem
import com.example.birlik.presentation.viewmodel.AuthViewModel
import com.example.birlik.presentation.viewmodel.RoomViewModel
import com.example.birlik.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    roomViewModel: RoomViewModel
) {
    val currentUserId by authViewModel.currentUserId.collectAsState()
    val userData = userViewModel.userData.collectAsState()
    val countryData = roomViewModel.countryData.observeAsState(listOf())
    val selectedCountries = roomViewModel.selectedCountries
    var dropdownState by remember { mutableStateOf(false) }
    var searchState by remember { mutableStateOf(false) }

    val updatedUserData = rememberUpdatedState(userData)

    val context = LocalContext.current


    LaunchedEffect(true) {
        currentUserId?.let { userId ->
            userViewModel.getUserData(userId)
        }
    }


    if (selectedCountries.isNotEmpty()) {
        BackHandler(onBack = {
            selectedCountries.clear()
        })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(100.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                painter = rememberImagePainter(data = "${userData.value?.image}"),
                                contentDescription = ""
                            )

                            Spacer(modifier = Modifier.size(16.dp))
                            Column() {
                                Text(text = "@${userData.value?.username}")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${userData.value?.country?.name}")
                                    Spacer(modifier = Modifier.size(8.dp))
                                    userData.value?.country?.image?.let {
                                        val countryImageDrawable = painterResource(id = it)
                                        Image(
                                            modifier = Modifier.size(20.dp),
                                            painter = countryImageDrawable,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            }
                        }
                    }
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Text(text = item.title)
                            },
                            selected = false,
                            onClick = {
//                                if (item.title == "Ölkələr") {
//                                    navController.navigate("all_countries_screen")
//                                }
//                                scope.launch {
//                                    drawerState.close()
//                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            badge = {
                                item.badgeCount?.let {
                                    Text(text = item.badgeCount.toString())
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            },
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            if (selectedCountries.isNotEmpty()) {
                                IconButton(onClick = {
                                    selectedCountries.clear()
                                }) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                            }else{
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }) {
                                    Icon(imageVector = Icons.Default.Menu, contentDescription = "")
                                }
                            }

                        },
                        title = {
                            Text(text = if (selectedCountries.isNotEmpty()) "${selectedCountries.size}" else "oyun")
                        },
                        actions = {
                            if (selectedCountries.isNotEmpty()) {
                                IconButton(onClick = {
                                    dropdownState = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = ""
                                    )
                                }
                                if (dropdownState) {
                                    DropdownMenu(
                                        expanded = dropdownState,
                                        onDismissRequest = { dropdownState = false })
                                    {
                                        DropdownMenuItem(
                                            text = { Text(text = "Sil") },
                                            onClick = {
                                                for (country in selectedCountries) {
                                                    roomViewModel.deleteCountry(
                                                        countryEntity = country
                                                    )
                                                }
                                                selectedCountries.clear()
                                            })
                                        if (countryData.value.size > 1){
                                            DropdownMenuItem(
                                                text = { Text(text = "Hamısını seç") },
                                                onClick = {
                                                    selectedCountries.clear()
                                                    selectedCountries.addAll(countryData.value)
                                                    dropdownState = false
                                                })
                                        }

                                    }
                                }
                            } else {
                                IconButton(onClick = {
                                    searchState = !searchState
                                }) {
                                    Icon(
                                        imageVector = if (searchState) Icons.Default.SearchOff else Icons.Default.Search,
                                        contentDescription = ""
                                    )
                                }
                            }

                        },
                    )
                },
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        LazyColumn {
                            item {
                                Button(onClick = {
                                    navController.navigate("durak_tables")
                                }) {
                                    Text(text = "tables")
                                }
                                Button(onClick = {
                                    userViewModel.oyunuBaslat(DurakData(), userData.value ?: UserData())
                                }) {
                                    Text(text = "start")
                                }
                                Button(onClick = {
                                    userViewModel.deleteAllGames()
                                }) {
                                    Text(text = "Delete")
                                }
                            }
                            items(countryData.value) {
                                CountryItem(
                                    countryEntity = it,
                                    roomViewModel = roomViewModel,
                                    selectable = true,
                                    navController = navController
                                )
                            }
                        }
                    }
                },
                bottomBar = {

                }
            )
        }
    }
}


data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
)

val items = listOf(
    NavigationItem(
        title = "Ayarlar",
        icon = Icons.Filled.Settings,
    ),
    NavigationItem(
        title = "  ",
        icon = Icons.Filled.Circle,
        badgeCount = 3
    ),
    NavigationItem(
        title = "Yeniliklər",
        icon = Icons.Filled.Info,
    ),
)

