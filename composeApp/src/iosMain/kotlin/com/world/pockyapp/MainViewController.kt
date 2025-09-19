package com.world.pockyapp

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import com.world.pockyapp.di.appModule
import platform.UIKit.UIViewController


fun MainViewController(
    mapUIViewController: () -> UIViewController,
    cameraUIViewController: (onMediaCaptured: (ByteArray, String) -> Unit) -> UIViewController
) = ComposeUIViewController(
    configure = {
        startKoin {
            modules(listOf(iosModule, appModule))
        }
    }) {
    mapViewController = mapUIViewController
    cameraViewController = cameraUIViewController


    App()
}

lateinit var mapViewController: () -> UIViewController
lateinit var cameraViewController: ((ByteArray, String) -> Unit) -> UIViewController