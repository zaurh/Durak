package com.example.birlik.data

import android.app.Activity
import android.app.Application
import android.os.Bundle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleCallbacks @Inject constructor() : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Activity created
        println("App is created")

    }

    override fun onActivityStarted(activity: Activity) {
        // Activity started
        println("App is started")

    }

    override fun onActivityResumed(activity: Activity) {
        // Activity resumed
        println("App is resumed")

    }

    override fun onActivityPaused(activity: Activity) {
        // Activity paused
        println("App is paused")

    }

    override fun onActivityStopped(activity: Activity) {
        // Activity stopped
        println("App is stopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Activity state saved
    }

    override fun onActivityDestroyed(activity: Activity) {
        // Activity destroyed
        println("App is destroyed")

    }
}


