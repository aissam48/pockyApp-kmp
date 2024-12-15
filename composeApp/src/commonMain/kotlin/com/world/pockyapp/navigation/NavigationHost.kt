package com.world.pockyapp.navigation

import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.world.pockyapp.screens.ImagePicker
import com.world.pockyapp.screens.camera.CameraView
import com.world.pockyapp.screens.capture_preview.CapturePreview
import com.world.pockyapp.screens.home.HomeScreen
import com.world.pockyapp.screens.login.LoginScreen

@Composable
fun NavigationHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME.route,
    ) {

        composable(NavRoutes.LOGIN.route) {
            LoginScreen(navController)
        }

        composable(NavRoutes.REGISTER.route) {
            RegisterScreen(navController)
        }

        composable(NavRoutes.HOME.route) {
            HomeScreen(navController)
        }

        composable(NavRoutes.CAMERA.route) {
            CameraView(navController)
        }

        composable(route = "${NavRoutes.CAPTURE_PREVIEW.route}/{path}") { navBackStackEntry ->
            val path = navBackStackEntry.arguments?.getString("path") ?: ""
            CapturePreview(navController, path)
        }

        composable(NavRoutes.PICKER.route) {
            ImagePicker(navController)
        }

        /*ccomposable(NavRoutes.ADD_PRODUCT.route) {
            AddProductScreen(navController)
        }

        composable(route = "${NavRoutes.PRODUCT_PREVIEW.route}/{productId}") { navBackStackEntry ->
            val productId = navBackStackEntry.arguments?.getString("productId") ?: ""
            ProductPreviewScreen(navController, productId)
        }*/

    }
}