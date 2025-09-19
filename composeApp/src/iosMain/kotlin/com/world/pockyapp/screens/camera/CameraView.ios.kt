package com.world.pockyapp.screens.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.navigation.NavHostController
import com.world.pockyapp.cameraViewController
import com.world.pockyapp.mapViewController
import com.world.pockyapp.screens.settings.SettingsViewModel
import platform.AVFoundation.*
import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import platform.darwin.NSObject
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_capture_white
import pockyapp.composeapp.generated.resources.ic_close_white

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CameraView(navController: NavHostController) {

    UIKitViewController(
        factory = cameraViewController,
        modifier = Modifier.fillMaxSize(),
        properties = UIKitInteropProperties(interactionMode = UIKitInteropInteractionMode.NonCooperative))
}
