package com.world.pockyapp.screens.splash_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(navController: NavHostController, viewModel: SplashViewModel = koinViewModel()) {

    val splashState = viewModel.splashState.collectAsState()

    LaunchedEffect(splashState.value) {
        when (splashState.value) {
            "login" -> {
                navController.navigate(NavRoutes.LOGIN.route)
            }

            "home" -> {
                navController.navigate(NavRoutes.HOME.route)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {


    }

}