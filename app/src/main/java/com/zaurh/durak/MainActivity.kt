package com.zaurh.durak

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.remote.durak.DurakData
import com.zaurh.durak.data_store.StoreSettings
import com.zaurh.durak.presentation.screen.DurakGame
import com.zaurh.durak.presentation.screen.DurakList
import com.zaurh.durak.presentation.screen.DurakSettings
import com.zaurh.durak.presentation.screen.LeaderboardScreen
import com.zaurh.durak.presentation.screen.ProfileScreen
import com.zaurh.durak.presentation.screen.SplashScreen
import com.zaurh.durak.presentation.screen.auth.ForgotPasswordScreen
import com.zaurh.durak.presentation.screen.auth.SignInScreen
import com.zaurh.durak.presentation.screen.auth.SignUpScreen
import com.zaurh.durak.presentation.viewmodel.AuthViewModel
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import com.zaurh.durak.presentation.viewmodel.StorageViewModel
import com.zaurh.durak.presentation.viewmodel.UserViewModel
import com.zaurh.durak.ui.theme.DurakTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        MobileAds.initialize(this)
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val dataStore = StoreSettings(context)
            var darkTheme by remember { mutableStateOf(false) }
            val savedDarkMode = dataStore.getDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            DurakTheme(
                darkTheme = savedDarkMode.value ?: false
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Navig(
                        darkTheme = savedDarkMode.value ?: false,
                    ) {
                        scope.launch {
                            darkTheme = !(savedDarkMode.value ?: false)
                            dataStore.saveDarkMode(darkTheme)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Navig(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    val navController = rememberNavController()
    val authViewModel = viewModel<AuthViewModel>()
    val durakViewModel = viewModel<DurakViewModel>()
    val userViewModel = viewModel<UserViewModel>()
    val storageViewModel = viewModel<StorageViewModel>()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(
                navController = navController
            )
        }

        composable("sign_in") {
            SignInScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("sign_up") {
            SignUpScreen(navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen()
        }

        composable("durak_game") {
            val durakData =
                navController.previousBackStackEntry?.arguments?.getParcelable<DurakData>("durak_data")

            durakData?.let {
                DurakGame(
                    navController = navController,
                    userViewModel = userViewModel,
                    durakViewModel = durakViewModel,
                    durakData = it
                )
            }
        }
        composable("durak_tables") {
            DurakList(
                navController = navController,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                durakViewModel = durakViewModel,
                storageViewModel = storageViewModel,
                darkTheme = darkTheme,
                onThemeUpdated = onThemeUpdated
            )
        }
        composable("durak_settings") {
            DurakSettings(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable("leaderboard") {
            LeaderboardScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable("profile_screen") {
            val userData =
                navController.previousBackStackEntry?.arguments?.getParcelable<UserData>("userData")
            userData?.let {
                ProfileScreen(
                    navController = navController,
                    userData = it,
                    userViewModel = userViewModel
                )
            }
        }
    }
}