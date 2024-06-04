package com.zaurh.durak.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zaurh.durak.data.remote.UserData
import com.zaurh.durak.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    val userDataState = userRepo.userData

    val allUsers = userRepo.usersData

    fun updateUserData(
        username: String,
        imageUrl: String
    ) {
        val userData = userDataState.value ?: UserData()
        userRepo.updateUser(
            userData.copy(
                name = username,
                image = imageUrl
            )
        )
    }

    fun rewardUser() {
        userRepo.rewardUser(
            userData = userDataState.value ?: UserData(),
        )
    }

    fun getUserData(userId: String) {
        userRepo.getUserData(userId = userId)
    }


    fun changeSkin(userData: UserData) {
        userRepo.changeSkin(userData)
    }

    fun getPromo(
        code: String,
        context: Context
    ) {
        userRepo.getPromo(
            code = code,
            onSuccess = { cash, coin ->
                Toast.makeText(context, "+$cash cash & +$coin coin added.", Toast.LENGTH_LONG).show()
            },
            onFailure = {
                Toast.makeText(context, "Promo not found.", Toast.LENGTH_SHORT)
                    .show()
            },
            onAlreadyApplied = {
                Toast.makeText(context, "Already applied.", Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }


}
