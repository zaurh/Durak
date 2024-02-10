package com.example.birlik

import android.content.Context
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.birlik.data.repository.UserRepo
import com.example.birlik.presentation.viewmodel.UserViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val userRepo: UserRepo,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
): Worker(context, workerParameters) {

    override fun doWork(): Result {

        return Result.success()
    }
}

