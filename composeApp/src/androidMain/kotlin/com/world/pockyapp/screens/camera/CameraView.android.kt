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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

@Composable
actual fun CameraView(navController: NavHostController) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val hapticFeedback = LocalHapticFeedback.current

    var isFlashOn by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isCapturing by remember { mutableStateOf(false) }
    var showControlsPanel by remember { mutableStateOf(true) }

    var camera by remember { mutableStateOf<Camera?>(null) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val permissionState = remember { mutableStateOf(false) }

    // Animation states
    val captureButtonScale by animateFloatAsState(
        targetValue = if (isCapturing) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val controlsPanelAlpha by animateFloatAsState(
        targetValue = if (showControlsPanel) 1f else 0f,
        animationSpec = tween(300)
    )

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

    // Auto-hide controls after 3 seconds of inactivity
    LaunchedEffect(showControlsPanel) {
        if (showControlsPanel) {
            delay(3000)
            showControlsPanel = false
        }
    }

    if (!permissionState.value) return

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

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

        camera?.cameraControl?.enableTorch(isFlashOn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures {
                    showControlsPanel = !showControlsPanel
                }
            }
    ) {
        // Camera Preview with rounded corners for modern look
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp)) // Can be adjusted for rounded preview
        )

        // Top Status Bar with Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
                .alpha(controlsPanelAlpha)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                // Flash Status Indicator
                AnimatedVisibility(
                    visible = isFlashOn,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Yellow.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color.Yellow.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.icon_flash_on),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(Color.Yellow)
                            )
                            Text(
                                text = "Flash On",
                                color = Color.Yellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Camera Mode Indicator
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = if (lensFacing == CameraSelector.LENS_FACING_BACK) "Photo" else "Selfie",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom Controls with Modern Glass Effect
        AnimatedVisibility(
            visible = controlsPanelAlpha > 0f,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400, easing = EaseOutCubic)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 90.dp)
                    .alpha(controlsPanelAlpha),
                shape = RoundedCornerShape(32.dp),
                color = Color.Black.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )
                        .blur(0.5.dp)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Control buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Flash Control
                        ModernControlButton(
                            isActive = isFlashOn,
                            onClick = {
                                isFlashOn = !isFlashOn
                                camera?.cameraControl?.enableTorch(isFlashOn)
                            }
                        ) {
                            Image(
                                painter = if (isFlashOn) painterResource(Res.drawable.icon_flash_on)
                                else painterResource(Res.drawable.icon_flash_off),
                                contentDescription = "Flash",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    if (isFlashOn) Color.Yellow else Color.White
                                )
                            )
                        }

                        // Capture Button - Modern Design
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(captureButtonScale)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (!isCapturing) {
                                        isCapturing = true
                                        capturePhoto(context, imageCapture, navController) {
                                            isCapturing = false
                                        }
                                    }
                                }
                        ) {
                            // Outer ring with pulse animation
                            val infiniteTransition = rememberInfiniteTransition()
                            val pulseScale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(if (isCapturing) pulseScale else 1f)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            // Inner capture button
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White,
                                                Color.White.copy(alpha = 0.8f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(3.dp, Color.Black.copy(alpha = 0.2f), CircleShape)
                            ) {
                                if (isCapturing) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.Center)
                                            .background(Color.Red, RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }

                        // Camera Switch Control
                        ModernControlButton(
                            isActive = lensFacing == CameraSelector.LENS_FACING_FRONT,
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                                    CameraSelector.LENS_FACING_FRONT
                                else CameraSelector.LENS_FACING_BACK
                            }
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_change_camera),
                                contentDescription = "Switch Camera",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }
            }
        }

        // Capture Animation Overlay
        AnimatedVisibility(
            visible = isCapturing,
            enter = fadeIn(tween(100)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun ModernControlButton(
    isActive: Boolean = false,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .background(
                color = if (isActive) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                shape = CircleShape
            )
            .border(
                1.dp,
                if (isActive) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.2f),
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

private fun capturePhoto(
    context: android.content.Context,
    imageCapture: ImageCapture,
    navController: NavHostController,
    onComplete: () -> Unit
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
                onComplete()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraView", "Error capturing photo: ${exception.message}")
                onComplete()
            }
        }
    )
}