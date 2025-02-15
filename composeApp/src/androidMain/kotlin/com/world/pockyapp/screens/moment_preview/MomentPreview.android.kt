package com.world.pockyapp.screens.moment_preview

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_arrow_right_white
import pockyapp.composeapp.generated.resources.ic_close_black
import java.io.File

@OptIn(KoinExperimentalAPI::class)
@Composable
actual fun MomentPreview(
    navController: NavHostController,
    path: String,
) {

    val viewModel: MomentPreviewViewModel = koinViewModel()
    val imageData = convertImageToByteArray(Uri.parse(path.replace("$", "/")), LocalContext.current)
    println("MomentPreview $imageData")

    var isChecked by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }
    var showDialog3 by remember { mutableStateOf(false) }

    val title = remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val geolocator: Geolocator = Geolocator.mobile()

    val geoLocationModel by remember { mutableStateOf(GeoLocationModel()) }

    LaunchedEffect(isChecked) {
        if (!isChecked) {
            return@LaunchedEffect
        }
        title.value = "Fetching your location..."
        showDialog3 = true
        when (val result: GeolocatorResult = geolocator.current(priority = Priority.HighAccuracy)) {
            is GeolocatorResult.Success -> {
                val geocoder = Geocoder()
                val place = geocoder.placeOrNull(result.data.coordinates)
                geoLocationModel.latitude = result.data.coordinates.latitude
                geoLocationModel.longitude = result.data.coordinates.longitude
                geoLocationModel.street = place?.street.toString()
                geoLocationModel.country = place?.country.toString()
                geoLocationModel.postalCode = place?.postalCode.toString()
                geoLocationModel.name = place?.name.toString()

                println(geoLocationModel)
                showDialog3 = false
            }

            is GeolocatorResult.Error -> {
                showDialog3 = false
                when (result) {
                    is GeolocatorResult.NotSupported -> {
                        isChecked = false
                        title.value = "Enable GPS"
                        showDialog2 = true
                    }

                    is GeolocatorResult.NotFound -> {
                        isChecked = false
                    }

                    is GeolocatorResult.PermissionError -> {
                        isChecked = false
                    }

                    is GeolocatorResult.GeolocationFailed -> {
                        isChecked = false
                    }

                    else -> {
                        isChecked = false
                    }
                }
            }
        }


    }
    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "Close",
            onCancel = {
                showDialog = false
                navController.popBackStack()
            }
        )
    }
    if (showDialog2) {
        CustomDialogSuccess(
            title = title.value,
            action = "Close",
            onCancel = {
                showDialog2 = false
                //navController.popBackStack()
            }
        )
    }
    if (showDialog3) {
        CustomDialogLoading(
            title = title.value,
        )
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is MomentPreviewUiState.Loading -> {

            }

            is MomentPreviewUiState.Success -> {
                title.value = "Your moment has shared successfully"
                showDialog = true
            }

            is MomentPreviewUiState.Error -> {
                title.value = state.error.message
                showDialog = true
            }

            is MomentPreviewUiState.Idle -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray)
        ) {
            Image(
                painter = rememberAsyncImagePainter(path.replace("$", "/")),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.FillHeight,

                )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            ) {

                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Share to nearby", color = Color.White, fontSize = 13.sp)

                    Spacer(modifier = Modifier.width(10.dp))

                    Switch(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFF3C623),
                            uncheckedThumbColor = Color.Gray
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .height(50.dp)
                        .width(130.dp)
                        .padding(5.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            viewModel.shareMoment(imageData, isChecked, geoLocationModel)
                        }

                ) {

                    when (uiState) {
                        is MomentPreviewUiState.Loading -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(130.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            }
                        }

                        else -> {
                            Text("Share moment", color = Color.White)

                            Spacer(modifier = Modifier.size(15.dp))

                            Image(
                                painter = painterResource(Res.drawable.ic_arrow_right_white),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                    }
                }
            }
        }

        Image(
            painter = painterResource(Res.drawable.ic_close_black),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
                .clickable {
                    navController.popBackStack()
                })
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
