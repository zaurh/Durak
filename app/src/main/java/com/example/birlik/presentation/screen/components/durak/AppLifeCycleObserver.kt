package com.example.birlik.presentation.screen.components.durak

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.birlik.data.AppLifecycleCallbacks

@Composable
fun AppLifecycleObserver(
    lifecycle: Lifecycle,
    onAppStopped: () -> Unit
) {
    DisposableEffect(Unit) {
        val callback = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                // App is stopped, invoke the provided callback
                onAppStopped.invoke()
            }
        }

        lifecycle.addObserver(callback)

        // Remove the observer when the composable is disposed
        onDispose {
            lifecycle.removeObserver(callback)
        }
    }
}

