package com.world.pockyapp.screens.google_maps

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.home.navigations.discover.UiState
import com.world.pockyapp.screens.moment_screen.MomentsViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.emptyList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
actual fun MapComponentScreen(navController: NavHostController) {
    val viewModel: GoogleMapsViewModel = koinViewModel()
    val globalMomentsState by viewModel.globalMomentsState.collectAsState()
    val momentsViewModel: MomentsViewModel = koinViewModel() // Koin inject

    //val heatmapData = remember { mutableStateOf(emptyList<WeightedLatLng>()) }
    //val moments = remember { mutableStateOf(emptyList<MomentModel>()) }

    LaunchedEffect(Unit) {
        viewModel.loadGlobalMoments()
    }

/*
    when(val state = globalMomentsState){
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            moments.value = state.data
            println("moments UiState.Success-> $moments")
            println("moments UiState.Success-> 1")
            if (state.data.isNotEmpty()){
                println("moments UiState.Success-> 2")
                heatmapData.value = state.data.map {
                    WeightedLatLng(
                        LatLng(it.geoLocation.latitude, it.geoLocation.longitude),
                        calculateMomentWeight(it)
                    )
                }
            }

            println("heatmapData UiState.Success-> ${heatmapData.value}")

        }

        is UiState.Error -> {

        }

    }
*/
    val heatmapData: List<WeightedLatLng> = when (val state = globalMomentsState) {
        is UiState.Success -> state.data.map {
            WeightedLatLng(
                LatLng(it.geoLocation.latitude, it.geoLocation.longitude),
                1.0
            )
        }
        else -> emptyList()
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(33.6, -7.6), 6f)
    }

    val gradient = remember {
        val colors = intArrayOf(
            Color.argb(0, 255, 255, 0),    // Transparent yellow (أصفر شفاف)
            Color.argb(255, 255, 255, 0),  // Yellow (نشاط منخفض)
            Color.argb(255, 255, 165, 0),  // Orange (نشاط متوسط)
            Color.argb(255, 255, 0, 0)     // Red (نشاط عالي جدًا)
        )
        val startPoints = floatArrayOf(
            0.0f, 0.3f, 0.7f, 1.0f
        )
        Gradient(colors, startPoints)
    }

    val heatmapProvider = remember(heatmapData, gradient) {
        if (heatmapData.isNotEmpty()) {
            HeatmapTileProvider.Builder()
                .weightedData(heatmapData)
                .gradient(gradient)
                .radius(50)
                .opacity(0.7)
                .build()
        } else {
            null
        }
    }


    Scaffold (modifier = Modifier.fillMaxSize()){ paddingValues ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.TERRAIN,
            ),
            onMapClick = { clickedLatLng ->
                println("ClickedOnMap: $clickedLatLng")
                if (globalMomentsState is UiState.Success){
                    val momentsAround = handleMapClick(clickedLatLng, (globalMomentsState as UiState.Success<List<MomentModel>>).data, cameraPositionState.position.zoom)

                    println(momentsAround)
                    momentsViewModel.moments = listOf<List<MomentModel>>(listOf(momentsAround.last(), momentsAround.last(),  momentsAround.last()),listOf(momentsAround.last(), momentsAround.last()),momentsAround)
                    momentsViewModel.selectedIndex = 0
                    momentsViewModel.myID = "d140fd44-4879-486f-b6a6-445a15f7d0f0"
                    navController.navigate(
                        NavRoutes.MOMENTS.route
                    )
                }
                //


            }
        ) {
            if (heatmapProvider != null) {
                TileOverlay(
                    tileProvider = heatmapProvider,
                    transparency = 0.0f
                )
            }

        }
    }

}

private fun handleMapClick(
    clickedLatLng: LatLng,
    moments: List<MomentModel>,
    zoomLevel: Float
): List<MomentModel> {
    val baseRadius = 20000.0
    val radiusInMeters = baseRadius / (zoomLevel / 6.0).pow(1.5)
    return moments.filter { point ->
        haversineDistance(
            clickedLatLng.latitude,
            clickedLatLng.longitude,
            point.geoLocation.latitude,
            point.geoLocation.longitude
        ) <= radiusInMeters
    }
}


private fun haversineDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val R = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}


