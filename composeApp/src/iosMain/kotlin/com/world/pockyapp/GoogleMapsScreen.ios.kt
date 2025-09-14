package com.world.pockyapp

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMapsScreen() {

    val mapView = remember {
        GMSMapView().apply {
            // Enable user interaction - this is critical
            userInteractionEnabled = true
            multipleTouchEnabled = true

            // Enable all gestures
            settings.scrollGestures = true
            settings.zoomGestures = true
            settings.tiltGestures = true
            settings.rotateGestures = true

            // Optional UI elements
            settings.myLocationButton = false // Set to true if you want location button
            settings.compassButton = true

            // Set camera position
            val cameraPosition = GMSCameraPosition.cameraWithLatitude(
                latitude = 1.35,
                longitude = 103.87,
                zoom = 14.0f
            )
            camera = cameraPosition
        }
    }

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            // Create a container view to ensure proper touch handling
            val containerView = UIView()
            containerView.addSubview(mapView)

            // Set constraints to fill the container
            mapView.translatesAutoresizingMaskIntoConstraints = false
            mapView.topAnchor.constraintEqualToAnchor(containerView.topAnchor).active = true
            mapView.leadingAnchor.constraintEqualToAnchor(containerView.leadingAnchor).active = true
            mapView.trailingAnchor.constraintEqualToAnchor(containerView.trailingAnchor).active = true
            mapView.bottomAnchor.constraintEqualToAnchor(containerView.bottomAnchor).active = true

            containerView
        },
        update = { containerView ->
            // Ensure map view is properly sized
            containerView.subviews.firstOrNull()?.let { mapView ->
                if (mapView is GMSMapView) {
                    mapView.setNeedsLayout()
                }
            }
        }
    )
}