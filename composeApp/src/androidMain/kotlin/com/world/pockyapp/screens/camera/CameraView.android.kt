package com.world.pockyapp.screens.camera

import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_change_camera
import pockyapp.composeapp.generated.resources.icon_circle_yellew
import pockyapp.composeapp.generated.resources.icon_flash_off
import pockyapp.composeapp.generated.resources.icon_flash_on
import java.io.File
import java.util.UUID

@Composable
actual fun CameraView(navController: NavHostController) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    var isFlashOn by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    var camera by remember { mutableStateOf<Camera?>(null) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val permissionState = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionState.value = isGranted
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "Camera permission is required to use this feature.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            permissionState.value = true
        } else {
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if (!permissionState.value) return

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Bind camera once permission is granted or lens facing changes
    LaunchedEffect(permissionState.value, lensFacing, isFlashOn) {
        if (!permissionState.value) return@LaunchedEffect

        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        camera = cameraProvider.bindToLifecycle(
            lifeCycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        // Apply flash
        camera?.cameraControl?.enableTorch(isFlashOn)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Capture & controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp, start = 20.dp, end = 20.dp)
        ) {

            // Capture Button
            Image(
                painter = painterResource(Res.drawable.icon_circle_yellew),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(80.dp)
                    .clickable {
                        capturePhoto(context, imageCapture, navController)
                    }
            )

            // Side Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color(0x80FFFFFF), shape = RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Switch Camera
                Image(
                    painter = painterResource(Res.drawable.ic_change_camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                                CameraSelector.LENS_FACING_FRONT
                            else CameraSelector.LENS_FACING_BACK
                        },
                    colorFilter = ColorFilter.tint(Color.White)
                )

                // Flash toggle
                Image(
                    painter = if (isFlashOn) painterResource(Res.drawable.icon_flash_on)
                    else painterResource(Res.drawable.icon_flash_off),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            isFlashOn = !isFlashOn
                            camera?.cameraControl?.enableTorch(isFlashOn)
                        },
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

private fun capturePhoto(
    context: android.content.Context,
    imageCapture: ImageCapture,
    navController: NavHostController
) {
    val photoFile = File(context.externalCacheDir, "${UUID.randomUUID()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                val encodedPath = savedUri.path?.replace("/", "$")
                navController.navigate("moment_preview/$encodedPath")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraView", "Error capturing photo: ${exception.message}")
            }
        }
    )
}
