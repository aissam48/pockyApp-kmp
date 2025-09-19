package com.world.pockyapp.screens.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.navigation.NavHostController
import com.world.pockyapp.cameraViewController
import com.world.pockyapp.mapViewController
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.moment_preview.MomentPreviewViewModel
import com.world.pockyapp.screens.settings.SettingsViewModel
import platform.AVFoundation.*
import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import platform.darwin.NSObject
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_capture_white
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_close_white

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CameraView(navController: NavHostController) {

    val viewModel: MomentPreviewViewModel = koinViewModel()


    Box(modifier = Modifier.fillMaxSize()) {

        UIKitViewController(
            factory = {
                cameraViewController { data: ByteArray, fileName: String ->
                    // ✅ Handle data here (upload, save, etc.)
                    println("Captured file: $fileName with size=${data.size}")

                    viewModel.imageByteArray = data
                    viewModel.fileName = fileName
                    navController.navigate(NavRoutes.MOMENT_PREVIEW.route + "/encodedFilePath")

                }
            },
            modifier = Modifier.fillMaxSize().background(color = Color.Black),
            properties = UIKitInteropProperties(interactionMode = UIKitInteropInteractionMode.NonCooperative)
        )


        Image(
            painter = painterResource(Res.drawable.ic_close_black),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
                .clickable {
                    navController.popBackStack()
                }.background(color = Color.White, CircleShape))
    }


}
