package com.world.pockyapp.screens.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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

@Composable
actual fun CameraView(navController: NavHostController) {

    /*var captureSession by remember { mutableStateOf<AVCaptureSession?>(null) }
    val photoOutput = remember { AVCapturePhotoOutput() }

    DisposableEffect(Unit) {
        captureSession = AVCaptureSession()
        setupCamera(captureSession!!, photoOutput)
        onDispose {
            captureSession?.stopRunning()
        }
    }*/

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview here - you'll need a ComposeCamera pod or custom native view

        Image(
            painter = painterResource(Res.drawable.ic_close_white),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
                .clickable { navController.popBackStack() }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_capture_white),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(80.dp)
                    .clickable {
                        //capturePhoto(photoOutput, navController)
                    }
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun setupCamera(captureSession: AVCaptureSession, photoOutput: AVCapturePhotoOutput) {
    captureSession.beginConfiguration()
    val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
    val input = device?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }
    if (input != null) {
        captureSession.addInput(input)
        captureSession.addOutput(photoOutput)
    }
    captureSession.commitConfiguration()
    captureSession.startRunning()
}

private fun capturePhoto(photoOutput: AVCapturePhotoOutput, navController: NavHostController) {
    val settings = AVCapturePhotoSettings.photoSettings()
    photoOutput.capturePhotoWithSettings(settings, object : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
        override fun captureOutput(
            output: AVCapturePhotoOutput,
            didFinishProcessingPhoto: AVCapturePhoto,
            error: NSError?
        ) {
            val imageData = didFinishProcessingPhoto.fileDataRepresentation()
            val documentsDirectory = NSFileManager.defaultManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).first() as NSURL
            val fileName = "${NSUUID.UUID().UUIDString}.jpg"
            val fileURL = documentsDirectory.URLByAppendingPathComponent(fileName)

            if (fileURL != null) {
                imageData?.writeToURL(fileURL, atomically = true)
            }
            val encodedFilePath = fileURL?.path?.replace("/", "$")
            navController.navigate("moment_preview/${encodedFilePath}")
        }
    })
}