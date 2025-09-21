package com.world.pockyapp.screens.moment_preview

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.network.models.model.GeoLocationModel
import com.world.pockyapp.screens.components.CustomDialogLoading
import com.world.pockyapp.screens.components.CustomDialogSuccess
import dev.jordond.compass.Priority
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.placeOrNull
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.mobile
import org.jetbrains.compose.resources.painterResource
import org.koin.core.annotation.KoinExperimentalAPI
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_arrow_right_white
import pockyapp.composeapp.generated.resources.ic_close_black
import java.io.File

// 🔹 Dialog state sealed class
sealed class DialogState {
    object None : DialogState()
    data class Success(val message: String) : DialogState()
    data class Error(val message: String) : DialogState()
    data class Loading(val message: String) : DialogState()
}

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
actual fun MomentPreview(
    navController: NavHostController,
    path: String,
    viewModel: MomentPreviewViewModel
) {
    val imageData = convertImageToByteArray(Uri.parse(path.replace("$", "/")), LocalContext.current)
    var isChecked by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    var dialogState by remember { mutableStateOf<DialogState>(DialogState.None) }
    var geoLocationModel by remember { mutableStateOf(GeoLocationModel()) }

    val geolocator: Geolocator = Geolocator.mobile()

    // 🔹 Handle location fetching when user enables toggle
    LaunchedEffect(isChecked) {
        if (!isChecked) return@LaunchedEffect

        dialogState = DialogState.Loading("Fetching your location...")

        when (val result: GeolocatorResult = geolocator.current(priority = Priority.HighAccuracy)) {
            is GeolocatorResult.Success -> {
                val geocoder = Geocoder()
                val place = geocoder.placeOrNull(result.data.coordinates)
                geoLocationModel = geoLocationModel.copy(
                    latitude = result.data.coordinates.latitude,
                    longitude = result.data.coordinates.longitude,
                    street = place?.street.orEmpty(),
                    country = place?.country.orEmpty(),
                    postalCode = place?.postalCode.orEmpty(),
                    name = place?.name.orEmpty()
                )
                dialogState = DialogState.None
            }
            is GeolocatorResult.Error -> {
                isChecked = false
                dialogState = when (result) {
                    is GeolocatorResult.NotSupported ->
                        DialogState.Error("Please enable GPS to share location")
                    else ->
                        DialogState.Error("Unable to get your location")
                }
            }
        }
    }


    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is MomentPreviewUiState.Success -> {
                dialogState = DialogState.Success("Your moment has been shared successfully")

            }
            is MomentPreviewUiState.Error -> {
                dialogState = DialogState.Error(state.error.message)
            }
            is MomentPreviewUiState.Loading -> {

            }
            else -> {}
        }
    }

    // 🔹 Show dialogs
    when (val state = dialogState) {
        is DialogState.Success -> CustomDialogSuccess(
            title = state.message,
            action = "OK",
            onCancel = {
                dialogState = DialogState.None
                navController.popBackStack()
            }
        )
        is DialogState.Error -> CustomDialogSuccess(
            title = state.message,
            action = "OK",
            onCancel = { dialogState = DialogState.None }
        )
        is DialogState.Loading -> CustomDialogLoading(title = state.message)
        DialogState.None -> {}
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        ) {
            // Moment Image Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(path.replace("$", "/")),
                    contentDescription = "Moment Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Controls Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1A1A1A),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Location Toggle Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFDFC46B).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFFDFC46B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Share to nearby",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Let people nearby see your moment",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            Switch(
                                checked = isChecked,
                                onCheckedChange = { isChecked = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFDFC46B),
                                    checkedTrackColor = Color(0xFFDFC46B).copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Share Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                Color(0xFFDFC46B),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable{
                                viewModel.shareMoment(imageData, isChecked, geoLocationModel)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (uiState) {
                            is MomentPreviewUiState.Loading -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Sharing...",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            else -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Share Moment",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Image(
                                        painter = painterResource(Res.drawable.ic_arrow_right_white),
                                        contentDescription = "Share",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Close Button
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .background(
                    Color.White.copy(alpha = 0.5f),
                    CircleShape
                )
                .clickable { navController.popBackStack() }
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_close_black),
                contentDescription = "Close",
                modifier = Modifier.size(20.dp)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUiState()
        }
    }
}

private fun convertImageToByteArray(uri: Uri, context: Context): ByteArray {
    val file = uri.path?.let { File(it) }
    val contentUri =
        file?.let { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) }

    return contentUri?.let { it ->
        context.contentResolver.openInputStream(it)?.use {
            it.readBytes()
        }
    } ?: ByteArray(0)
}
