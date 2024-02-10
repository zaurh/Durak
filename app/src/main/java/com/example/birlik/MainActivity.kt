package com.example.birlik

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.presentation.screen.AllCountiesScreen
import com.example.birlik.presentation.screen.DurakGame
import com.example.birlik.presentation.screen.DurakList
import com.example.birlik.presentation.screen.DurakSettings
import com.example.birlik.presentation.screen.LeaderboardScreen
import com.example.birlik.presentation.screen.MainScreen
import com.example.birlik.presentation.screen.ProfileScreen
import com.example.birlik.presentation.screen.auth.ForgotPasswordScreen
import com.example.birlik.presentation.screen.auth.SignInScreen
import com.example.birlik.presentation.screen.auth.SignUpScreen
import com.example.birlik.presentation.viewmodel.AuthViewModel
import com.example.birlik.presentation.viewmodel.CircularProgressViewModel
import com.example.birlik.presentation.viewmodel.UserViewModel
import com.example.birlik.ui.theme.BirlikTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var mInterstitialAd: InterstitialAd? = null
    private var adId: String = "ca-app-pub-3940256099942544/1033173712"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        MobileAds.initialize(this)
        super.onCreate(savedInstanceState)

        val workRequest = OneTimeWorkRequestBuilder<CustomWorker>()
            .setInitialDelay(Duration.ofSeconds(10))
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            )
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)



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

    private fun loadInterstitialAd(adStatus: (Boolean) -> Unit) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, adId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                mInterstitialAd = null
                Log.i("AD_TAG", "onAdFailedToLoad: ${error.message}")
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                mInterstitialAd = interstitialAd
                Log.i("AD_TAG", "onAdLoaded: ")
            }

        })
    }

    private fun showInterstitialAd() {
        mInterstitialAd?.let {ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.i("AD_TAG", "onAdDismissedFullScreenContent: ")
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.i("AD_TAG", "onAdImpression: ")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.i("AD_TAG", "onAdClicked: ")
                }
            }
            ad.show(this)
        }?: kotlin.run {
            Toast.makeText(this, "Ad is null", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Navig() {
    val navController = rememberNavController()
    val authViewModel = viewModel<AuthViewModel>()
    val userViewModel = viewModel<UserViewModel>()
    val circularProgressViewModel = viewModel<CircularProgressViewModel>()

    NavHost(navController = navController, startDestination = "sign_in") {
        composable("main") {
            MainScreen(
                navController = navController,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
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
            )
        }
//        composable("country_screen") {
//            val countryEntity =
//                navController.previousBackStackEntry?.arguments?.getParcelable<CountryEntity>("countryEntity")
//
//            countryEntity?.let {
//                CountryScreen(
//                    navController = navController,
//                    countryEntity = it
//                )
//            }
//        }
        composable("durak_game") {
            val durakData =
                navController.previousBackStackEntry?.arguments?.getParcelable<DurakData>("durak_data")

            durakData?.let {
                DurakGame(
                    navController = navController,
                    userViewModel = userViewModel,
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