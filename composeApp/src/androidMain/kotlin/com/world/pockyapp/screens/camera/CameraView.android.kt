package com.world.pockyapp.screens.camera

import androidx.compose.runtime.Composable
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_capture_white
import pockyapp.composeapp.generated.resources.ic_change_camera
import pockyapp.composeapp.generated.resources.ic_close_white
import java.io.File
import java.util.UUID

@Composable
actual fun CameraView(navController: NavHostController) {
    val context = LocalContext.current
    val lifeCycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val lensFacing = remember {
        mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
    }

    var savedUri by remember { mutableStateOf<Uri?>(null) }

    val cameraProvider = remember { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(Unit) {
        val cameraProviderInstance = cameraProviderFuture.get()
        cameraProvider.value = cameraProviderInstance
        onDispose {
            cameraProviderInstance.unbindAll()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = {
                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing.intValue).build()

                cameraProvider.value?.bindToLifecycle(lifeCycleOwner, cameraSelector, preview, imageCapture)
            })

        DisposableEffect(Unit) {
            onDispose {
                cameraProviderFuture.get()?.unbindAll()
            }
        }

        fun capturePhoto() {
            val photoFile = File(context.externalCacheDir, "${UUID.randomUUID()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Log.e("onImageSaved", output.savedUri?.path ?: "")
                        savedUri = output.savedUri
                        val encodedFilePath = savedUri?.path?.replace("/", "$")
                        Log.e("onImageSaved", encodedFilePath ?: "")
                        navController.navigate(NavRoutes.MOMENT_PREVIEW.route + "/${encodedFilePath}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("onImageSavedError", exception.message ?: "")
                    }
                }
            )
        }

        Image(
            painter = painterResource(Res.drawable.ic_close_white),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
                .clickable {
                    navController.popBackStack()
                })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp, end = 20.dp, start = 20.dp)
        ) {

            Image(
                painter = painterResource(Res.drawable.ic_capture_white),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(80.dp)
                    .clickable {
                        capturePhoto()
                    })

            Image(
                painter = painterResource(Res.drawable.ic_change_camera),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .clickable {
                        cameraProvider.value?.unbindAll()
                        if (lensFacing.intValue == CameraSelector.LENS_FACING_BACK) {
                            lensFacing.intValue = CameraSelector.LENS_FACING_FRONT
                        } else {
                            lensFacing.intValue = CameraSelector.LENS_FACING_BACK
                        }
                    })
        }


    }
}