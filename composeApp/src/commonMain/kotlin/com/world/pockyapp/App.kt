package com.world.pockyapp

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.world.pockyapp.navigation.NavigationHost
import com.world.pockyapp.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    AppTheme {
        KoinContext {

            val navController = rememberNavController()
            NavigationHost(navController)

        }
    }
}