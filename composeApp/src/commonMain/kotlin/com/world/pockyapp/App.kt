package com.world.pockyapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

            val systemBarsInsets = WindowInsets.systemBars.asPaddingValues()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .windowInsetsPadding(WindowInsets(0.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    println(getPlatform().name + "    fghjkllkjhgfdfghjk")
                    //Android 34
                    NavigationHost(navController)

                }
            }

            /* Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .background(Color.Black)
                     .padding(systemBarsInsets)
             ) {
                 NavigationHost(navController)
             }*/

        }
    }
}