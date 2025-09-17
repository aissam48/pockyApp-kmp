package com.world.pockyapp

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import com.world.pockyapp.di.appModule
import platform.UIKit.UIViewController


/*fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(listOf(iosModule, appModule))
        }
    }) {
    App()
}*/


fun MainViewController(
    mapUIViewController: () -> UIViewController
) = ComposeUIViewController(
    configure = {
        startKoin {
            modules(listOf(iosModule, appModule))
        }
    }) {
    mapViewController = mapUIViewController
    App()
}

lateinit var mapViewController: () -> UIViewController