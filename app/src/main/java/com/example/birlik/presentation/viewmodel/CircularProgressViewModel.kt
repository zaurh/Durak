package com.example.birlik.presentation.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birlik.data.remote.UserData
import com.example.birlik.data.remote.durak.DurakData
import com.example.birlik.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CircularProgressViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {
    var decreaseSecond = userRepo.decreaseSecond
    val timeFinished = mutableStateOf(false)

    init {
        decreaseCountDown(){
            userRepo.decreaseSecond.value = 10
//            timeFinished.value = true
        }
    }

    private fun decreaseCountDown(
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            while (true){
                delay(1000)
                userRepo.decreaseSecond.value = userRepo.decreaseSecond.value!! -1
//                decreaseSecond.value = decreaseSecond.value!! - 1
                if (userRepo.decreaseSecond.value == 0){
                    onComplete()
//                    break
                }
            }
        }
    }
}