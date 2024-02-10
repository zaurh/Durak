package com.example.birlik

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.birlik.data.AppLifecycleCallbacks
import com.example.birlik.data.repository.UserRepo
import com.example.birlik.presentation.viewmodel.UserViewModel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HiltApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var appLifecycleCallbacks: AppLifecycleCallbacks

    override fun onCreate() {
        super.onCreate()

        // Register the injected instance of AppLifecycleCallbacks
        registerActivityLifecycleCallbacks(appLifecycleCallbacks)
    }

    override fun onTerminate() {
        super.onTerminate()
        // App is terminated, perform final cleanup here
        println("App is terminated")
    }

    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}

class CustomWorkerFactory @Inject constructor(private val userRepo: UserRepo): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = CustomWorker(
        userRepo, appContext, workerParameters
    )
}