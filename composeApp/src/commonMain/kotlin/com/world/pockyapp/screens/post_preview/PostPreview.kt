package com.world.pockyapp.screens.post_preview

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
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
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_arrow_right_white
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_pick_image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPreview(navController: NavHostController, viewModel: PostViewModel = koinViewModel()) {

    val scope = rememberCoroutineScope()
    val photo = remember { mutableStateOf<ByteArray?>(null) }
    var isChecked by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                photo.value = it
            }
        }
    )

    val geolocator: Geolocator = Geolocator.mobile()
    val geoLocationModel by remember { mutableStateOf(GeoLocationModel()) }

    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }
    var showDialog3 by remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }

    LaunchedEffect(isChecked) {
        if (!isChecked) return@LaunchedEffect

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
                showDialog3 = false
            }

            is GeolocatorResult.Error -> {
                showDialog3 = false
                when (result) {
                    is GeolocatorResult.NotSupported -> {
                        isChecked = false
                        title.value = "Please enable GPS to share location"
                        showDialog2 = true
                    }

                    else -> {
                        isChecked = false
                        title.value = "Unable to get your location"
                        showDialog2 = true
                    }
                }
            }
        }
    }

    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "OK",
            onCancel = {
                showDialog = false
                navController.popBackStack()
            }
        )
    }

    if (showDialog2) {
        CustomDialogSuccess(
            title = title.value,
            action = "OK",
            onCancel = { showDialog2 = false }
        )
    }

    if (showDialog3) {
        CustomDialogLoading(title = title.value)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PostUiState.Loading -> {}
            is PostUiState.Success -> {
                title.value = "Your post has been shared successfully"
                showDialog = true
            }

            is PostUiState.Error -> {
                title.value = state.error.message
                showDialog = true
            }

            is PostUiState.Idle -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        ) {
            // Image Preview Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (photo.value != null) Color.Transparent else Color(
                        0xFF2A2A2A
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { singleImagePicker.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    if (photo.value != null) {
                        Image(
                            bitmap = photo.value!!.toImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        Color(0xFFDFC46B).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Photo",
                                    tint = Color(0xFFDFC46B),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tap to select photo",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Choose from gallery",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
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
                    // Location Toggle
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
                                    text = "Let people nearby discover your post",
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
                                if (photo.value != null) Color(0xFFDFC46B) else Color.Gray.copy(
                                    alpha = 0.3f
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable(enabled = photo.value != null && uiState !is PostUiState.Loading) {
                                photo.value?.let {
                                    viewModel.setPost(it, isChecked, geoLocationModel)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (uiState) {
                            is PostUiState.Loading -> {
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
                                        text = "Share Post",
                                        color = if (photo.value != null) Color.White else Color.Gray,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (photo.value != null) {
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
}