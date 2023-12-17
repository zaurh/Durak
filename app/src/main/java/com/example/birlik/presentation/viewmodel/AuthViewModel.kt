package com.example.birlik.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.birlik.data.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    val isAuthLoading = authRepo.isAuthLoading
    val currentUserId: StateFlow<String?> = authRepo.currentUserId
    val isSignedIn = authRepo.isSignedIn

    fun signUp(
        username: String,
        country: String,
        password: String,
        context: Context,
    ) {
        authRepo.signUp(username,country, password, context)
    }

    fun signIn(email: String, password: String, context: Context){
        authRepo.signIn(email, password, context)
    }

    fun signOut(){
        authRepo.signOut()
    }

    fun forgotPassword(email:String, context: Context){
        authRepo.forgotPassword(email, context)
    }
}