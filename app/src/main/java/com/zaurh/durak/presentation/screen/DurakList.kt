@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.zaurh.durak.presentation.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.zaurh.durak.R
import com.zaurh.durak.common.CustomSwitcher
import com.zaurh.durak.common.NavParam
import com.zaurh.durak.common.formatNumberWithK
import com.zaurh.durak.common.navigateTo
import com.zaurh.durak.common.sendMail
import com.zaurh.durak.common.willBeSoonToast
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.presentation.screen.components.CreateTableAlert
import com.zaurh.durak.presentation.screen.components.EditProfileAlert
import com.zaurh.durak.presentation.screen.components.PromoAlert
import com.zaurh.durak.presentation.screen.components.WhatsNewAlert
import com.zaurh.durak.presentation.screen.components.durak.DurakTableItem
import com.zaurh.durak.presentation.screen.components.durak.MySearchBar
import com.zaurh.durak.presentation.viewmodel.AuthViewModel
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import com.zaurh.durak.presentation.viewmodel.StorageViewModel
import com.zaurh.durak.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DurakList(
    navController: NavController,
    userViewModel: UserViewModel,
    durakViewModel: DurakViewModel,
    authViewModel: AuthViewModel,
    storageViewModel: StorageViewModel,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit
) {

    data class NavigationItem(
        val title: String,
        val icon: ImageVector
    )

    val items = listOf(
        NavigationItem(
            title = stringResource(id = R.string.account),
            icon = Icons.Filled.Person,
        ),
        NavigationItem(
            title = stringResource(id = R.string.whatsnew),
            icon = Icons.Filled.Newspaper,
        ),
        NavigationItem(
            title = stringResource(id = R.string.promo),
            icon = Icons.Filled.Money,
        ),
        NavigationItem(
            title = stringResource(id = R.string.rules),
            icon = Icons.Filled.Gamepad,
        ),
        NavigationItem(
            title = stringResource(id = R.string.report),
            icon = Icons.Filled.Report
        ),
        NavigationItem(
            title = stringResource(id = R.string.dark_mode),
            icon = Icons.Filled.DarkMode
        ),
        NavigationItem(
            title = stringResource(id = R.string.signOut),
            icon = Icons.Filled.Logout
        ),
    )

    val userData = userViewModel.userDataState.collectAsState()
    val durakTables = durakViewModel.durakTables.value

    val alertState = remember { mutableStateOf(false) }
    val editProfileAlertState = remember { mutableStateOf(false) }
    val whatsNewAlert = remember { mutableStateOf(false) }
    val promoAlert = remember { mutableStateOf(false) }


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val currentUserId by authViewModel.currentUserId.collectAsState()
    val focus = LocalFocusManager.current



    LaunchedEffect(true) {
        currentUserId?.let { userId ->
            userViewModel.getUserData(userId)
        }
    }
    BackHandler(
        enabled = drawerState.isOpen,
    ) {
        scope.launch {
            drawerState.close()
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            editProfileAlertState.value = true
                        }
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
                            painter = rememberImagePainter(data = userData.value?.image.takeUnless { it.isNullOrEmpty() }
                                ?: R.drawable.empty_profile),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(text = "${userData.value?.name}")
                    }
                }

                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.title,
                                color = if (item.icon == Icons.Filled.Logout) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.primary
                            )
                        },
                        selected = false,
                        onClick = {
                            if (item.icon == Icons.Filled.Person) {
                                navigateTo(
                                    navController,
                                    "profile_screen",
                                    NavParam("userData", userData.value ?: UserData())
                                )
                            } else if (item.icon == Icons.Filled.Report) {
                                context.sendMail(
                                    to = "zaurway@gmail.com",
                                    subject = "Durak App Report"
                                )
                            } else if (item.icon == Icons.Filled.Logout) {
                                navController.navigate("sign_in") {
                                    popUpTo(0)
                                }
                                authViewModel.signOut()
                            } else if (item.icon == Icons.Filled.Newspaper) {
                                whatsNewAlert.value = true
                            } else if (item.icon == Icons.Filled.DarkMode) {
                                onThemeUpdated()
                            } else if (item.icon == Icons.Filled.Money) {
                                promoAlert.value = true
                            } else {
                                willBeSoonToast(context)
                            }
                        },
                        badge = {
                            if (item.icon == Icons.Filled.DarkMode) {
                                CustomSwitcher(
                                    switch = darkTheme,
                                    firstIcon = "☀️",
                                    secondIcon = "\uD83C\uDF19"
                                )
                            }

                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (item.icon == Icons.Filled.Logout) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            floatingActionButton = {
                if (durakTables.isNotEmpty()){
                    FloatingActionButton(
                        modifier = Modifier.padding(20.dp),
                        contentColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.background,
                        onClick = {
                            alertState.value = true
                        }) {
                        Icon(Icons.Filled.Add, "")
                    }
                }
            }, topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "")
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
                            Text(
                                text = formatNumberWithK(value = userData.value?.cash ?: 0),
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.birlik_coin),
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = formatNumberWithK(value = userData.value?.coin ?: 0),
                                fontSize = 20.sp
                            )
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
                            Text(
                                text = formatNumberWithK(value = userData.value?.rating ?: 0),
                                fontSize = 20.sp
                            )
                        }

                    }, actions = {
                        IconButton(onClick = {
                            navController.navigate("durak_settings")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    })
            }, content = {

                Column(
                    modifier = Modifier.padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (alertState.value) {
                        CreateTableAlert(
                            onDismiss = { alertState.value = false },
                            onSuccess = { alertState.value = false },
                            durakViewModel = durakViewModel,
                            userViewModel = userViewModel,
                            userData = userData.value ?: UserData()
                        )
                    }
                    if (editProfileAlertState.value) {
                        EditProfileAlert(
                            storageViewModel = storageViewModel,
                            userViewModel = userViewModel,
                            userData = userData.value ?: UserData(),
                            onDone = {
                                focus.clearFocus()
                            }
                        ) {
                            editProfileAlertState.value = false
                        }
                    }
                    if (whatsNewAlert.value) {
                        WhatsNewAlert(
                            version = "1.0.0",
                            text = "• New table and card skins added \n Now you can customize your table and cards. \n\n" +
                                    "• New background skins added \n You can choose a background color of your own table.\n\n" +
                                    "This is the first release of the application. You may face some bugs or errors.\n" +
                                    "If you have any problems or advice. Please use Report feature."
                        )
                        {
                            whatsNewAlert.value = false
                        }
                    }
                    if (promoAlert.value) {
                        PromoAlert(onDismiss = {
                            promoAlert.value = false
                        }, userViewModel = userViewModel)
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
                                    durakViewModel = durakViewModel,
                                    onSearch = durakViewModel::searchList
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
                        val durakListFiltered = durakTables.filter { !it.finished }
                        items(durakListFiltered) { durakData ->
                            DurakTableItem(durakData = durakData, navController = navController)
                        }
                    }
                }

            })
    }


}





