package com.world.pockyapp.screens.createShot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.navigation.NavHostController
import com.world.pockyapp.createShotViewController
import com.world.pockyapp.mapViewController
import kotlin.native.ref.createCleaner

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CreateShotScreen(navController: NavHostController) {
    UIKitViewController(
        factory = createShotViewController,
        modifier = Modifier.fillMaxSize(),
        properties = UIKitInteropProperties(interactionMode = UIKitInteropInteractionMode.NonCooperative)
    )
}