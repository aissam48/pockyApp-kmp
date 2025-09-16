package com.world.pockyapp.screens.google_maps

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.screens.home.navigations.discover.UiState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
actual fun GoogleMapsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: GoogleMapsViewModel = koinViewModel()
    val globalMomentsState by viewModel.globalMomentsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadGlobalMoments()
    }


    val moments = remember(globalMomentsState) {
        when (val state = globalMomentsState) {
            is UiState.Success -> {
                state.data.flatMap { user -> user.moments }
            }
            else -> emptyList()
        }
    }

    val profiles = remember(globalMomentsState) {
        when (val state = globalMomentsState) {
            is UiState.Success -> {
                state.data
            }
            else -> emptyList()
        }
    }.filter { it.moments.isNotEmpty() }

    // Prepare heatmap data
    val heatmapData = remember(globalMomentsState) {
        when (val state = globalMomentsState) {
            is UiState.Success -> {
                val realData = state.data
                    .filter { it.moments.isNotEmpty() }
                    .flatMap { user ->
                        user.moments.mapNotNull { moment ->
                            // Ensure valid coordinates
                            if (moment.geoLocation.latitude != 0.0 && moment.geoLocation.longitude != 0.0) {
                                WeightedLatLng(
                                    LatLng(
                                        moment.geoLocation.latitude,
                                        moment.geoLocation.longitude
                                    ),
                                    calculateMomentWeight(moment) // Custom weight calculation
                                )
                            } else null
                        }
                    }

                realData

                //getSampleData()

            }

            else -> {
                emptyList()
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(33.6, -7.6), 6f)
    }

    val gradient = remember {
        val colors = intArrayOf(
            Color.argb(0, 0, 255, 0),      // Transparent
            Color.argb(255, 0, 255, 0),    // Green (low activity)
            Color.argb(255, 255, 255, 0),  // Yellow (medium activity)
            Color.argb(255, 255, 165, 0),  // Orange (high activity)
            Color.argb(255, 255, 0, 0)     // Red (very high activity)
        )
        val startPoints = floatArrayOf(0.0f, 0.3f, 0.5f, 0.7f, 1.0f)
        Gradient(colors, startPoints)
    }

    // Create heatmap provider
    val heatmapProvider = remember(heatmapData, gradient) {
        HeatmapTileProvider.Builder()
            .weightedData(heatmapData)
            .gradient(gradient)
            .radius(50) // Adjust radius as needed
            .opacity(0.7) // Make heatmap semi-transparent
            .build()
    }



    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.TERRAIN,
        ),
        onMapClick = { clickedLatLng ->
            println("ClickedOnMap: $clickedLatLng")
            val momentsAround = handleMapClick(clickedLatLng, moments)

            println(profiles)

            val modulesJson = Json.encodeToString(profiles).replace("/", "%")
            navController.navigate(
                NavRoutes.MOMENTS.route + "/${modulesJson}" + "/0" + "/d140fd44-4879-486f-b6a6-445a15f7d0f0"
            )
        }
    ) {
        TileOverlay(
            tileProvider = heatmapProvider,
            transparency = 0.0f
        )

    }

}

private fun handleMapClick(clickedLatLng: LatLng, heatmapData: List<MomentModel>): List<MomentModel> {
    val radiusInMeters = 20000.0
    val momentsInCircle = heatmapData.filter { point ->
        val distance = haversineDistance(
            clickedLatLng.latitude,
            clickedLatLng.longitude,
            point.geoLocation.latitude,
            point.geoLocation.longitude
        )
        println("distance -> $distance")
        println("radiusInMeters -> $radiusInMeters")
        distance <= radiusInMeters
    }
    println("ClickedOnMap: $clickedLatLng, found ${momentsInCircle.size} moments nearby")

    return momentsInCircle


    // You could show a bottom sheet, dialog, or snackbar with details
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


/**
 * Calculate weight for a moment based on various factors
 */
private fun calculateMomentWeight(moment: Any): Double {
    // This is a placeholder - adjust based on your actual moment data structure
    // You might want to consider:
    // - Number of likes/interactions
    // - Recency of the moment
    // - User engagement level
    // - etc.

    return try {
        // Example calculation - replace with actual logic
        when {
            // moment.likes > 100 -> 5.0
            // moment.likes > 50 -> 3.0
            // moment.likes > 10 -> 2.0
            else -> 1.0
        }
    } catch (e: Exception) {
        1.0 // Default weight
    }
}

/**
 * Fallback sample data for demonstration
 */
private fun getSampleData(): List<WeightedLatLng> {
    return listOf(
        // Major Moroccan cities with varying weights
        WeightedLatLng(LatLng(33.6, -7.6), 5.0),   // Casablanca - high activity
        WeightedLatLng(LatLng(31.6, -8.0), 3.0),   // Marrakech - medium activity
        WeightedLatLng(LatLng(35.7, -5.9), 4.0),   // Tangier - high activity
        WeightedLatLng(LatLng(34.0, -5.0), 2.0),   // Fez - low activity
        WeightedLatLng(LatLng(35.2, -3.9), 2.5),   // Al Hoceima - medium activity
        WeightedLatLng(LatLng(32.3, -9.2), 1.5),   // Safi - low activity
        WeightedLatLng(LatLng(35.8, -5.3), 3.5),   // Tetouan - medium-high activity
        WeightedLatLng(LatLng(33.9, -6.9), 4.5),   // Rabat - high activity

        // Add some random points for better visualization
        WeightedLatLng(LatLng(33.5, -7.5), 2.0),
        WeightedLatLng(LatLng(33.7, -7.7), 1.8),
        WeightedLatLng(LatLng(31.5, -8.1), 2.2),
        WeightedLatLng(LatLng(31.7, -7.9), 1.9),
        WeightedLatLng(LatLng(35.6, -5.8), 3.1),
        WeightedLatLng(LatLng(35.8, -6.0), 2.8)
    )
}