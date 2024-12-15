package com.world.pockyapp

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import com.world.pockyapp.di.appModule

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(listOf(iosModule, appModule))
        }
    }) {
    App()
}