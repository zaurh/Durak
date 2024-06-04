package com.zaurh.durak.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.durak.data.repository.DurakRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CircularProgressViewModel @Inject constructor(
    private val durakRepo: DurakRepo
) : ViewModel() {
    var decreaseSecond = durakRepo.decreaseSecond
    val timeFinished = mutableStateOf(false)

    init {
        decreaseCountDown(){
//            timeFinished.value = true
        }
    }

    private fun decreaseCountDown(
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            while (true){
                delay(2000)
                durakRepo.decreaseSecond.value = durakRepo.decreaseSecond.value!! -1
                if (durakRepo.decreaseSecond.value == 0){
                    onComplete()
                }
            }
        }
    }
}