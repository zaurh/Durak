package com.example.birlik

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.birlik.data.local.CountryEntity
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.screen.AllCountiesScreen
import com.example.birlik.presentation.screen.CountryScreen
import com.example.birlik.presentation.screen.DurakGame
import com.example.birlik.presentation.screen.DurakList
import com.example.birlik.presentation.screen.MainScreen
import com.example.birlik.presentation.screen.auth.ForgotPasswordScreen
import com.example.birlik.presentation.screen.auth.SignInScreen
import com.example.birlik.presentation.screen.auth.SignUpScreen
import com.example.birlik.presentation.viewmodel.AuthViewModel
import com.example.birlik.presentation.viewmodel.RoomViewModel
import com.example.birlik.presentation.viewmodel.UserViewModel
import com.example.birlik.ui.theme.BirlikTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("YourActivity", "created")

        setContent {
            BirlikTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navig()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }
}

@Composable
fun Navig() {
    val navController = rememberNavController()
    val authViewModel = viewModel<AuthViewModel>()
    val userViewModel = viewModel<UserViewModel>()
    val roomViewModel = viewModel<RoomViewModel>()

    NavHost(navController = navController, startDestination = "sign_in") {
        composable("main") {
            MainScreen(
                navController = navController,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                roomViewModel = roomViewModel
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
        composable("all_countries_screen") {
            AllCountiesScreen(
                navController = navController,
                roomViewModel = roomViewModel
            )
        }
        composable("country_screen") {
            val countryEntity =
                navController.previousBackStackEntry?.arguments?.getParcelable<CountryEntity>("countryEntity")

            countryEntity?.let {
                CountryScreen(
                    navController = navController,
                    countryEntity = it
                )
            }
        }
        composable("durak_game") {
            val durakData = navController.previousBackStackEntry?.arguments?.getParcelable<DurakData>("durak_data")

            durakData?.let {
                DurakGame(
                    navController = navController,
                    userViewModel = userViewModel,
                    authViewModel = authViewModel,
                    durakData = it
                )
            }
        }
        composable("durak_tables") {
            DurakList(
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}