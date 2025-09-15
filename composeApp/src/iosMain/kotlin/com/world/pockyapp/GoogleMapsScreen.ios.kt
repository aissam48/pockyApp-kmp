package com.world.pockyapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import kotlinx.cinterop.CValue
import platform.CoreLocation.CLLocationCoordinate2D
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.UIKit.UIImage
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIBezierPath
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLRequest
import platform.Foundation.dataTaskWithRequest
import platform.CoreGraphics.CGSizeMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGPointMake
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun GoogleMapsScreen() {

    val mapView = remember {
        GMSMapView()
    }

    // Function to create a circular image with specified size
    fun createCircularImage(image: UIImage, size: Double = 60.0): UIImage? {
        val imageSize = CGSizeMake(size, size)

        UIGraphicsBeginImageContextWithOptions(imageSize, false, 0.0)

        val rect = CGRectMake(0.0, 0.0, size, size)
        val path = UIBezierPath.bezierPathWithOvalInRect(rect)
        path.addClip()

        // Draw the image scaled to fit the circular bounds
        image.drawInRect(rect)

        val circularImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return circularImage
    }

    // Function to resize image to specific dimensions
    fun resizeImage(image: UIImage, newSize: Double = 60.0): UIImage? {
        val size = CGSizeMake(newSize, newSize)

        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)

        val rect = CGRectMake(0.0, 0.0, newSize, newSize)
        image.drawInRect(rect)

        val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return resizedImage
    }

    // Function to load image from URL
    suspend fun loadImageFromURL(imageUrl: String): UIImage? = suspendCancellableCoroutine { continuation ->
        val url = NSURL.URLWithString(imageUrl)
        if (url == null) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val request = NSURLRequest.requestWithURL(url)
        val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
            if (error != null) {
                println("Error loading image: ${error.localizedDescription}")
                continuation.resume(null)
            } else if (data != null) {
                val image = UIImage.imageWithData(data)
                continuation.resume(image)
            } else {
                continuation.resume(null)
            }
        }
        task.resume()

        continuation.invokeOnCancellation {
            task.cancel()
        }
    }

    // Move camera and marker setup to LaunchedEffect to avoid crashes
    LaunchedEffect(Unit) {
        try {
            val cameraPosition = GMSCameraPosition.cameraWithLatitude(
                latitude = 1.35,
                longitude = 103.87,
                zoom = 14.0f
            )
            val cameraUpdate = GMSCameraUpdate.setCamera(cameraPosition)
            mapView.moveCamera(cameraUpdate)

            // Create marker first with default icon
            val marker = GMSMarker().apply {
                position = cValue<CLLocationCoordinate2D> {
                    latitude = 1.35
                    longitude = 103.87
                }
                title = "Singapore"
                snippet = "Loading custom image..."
                map = mapView
            }

            // Load custom image from internet and update marker
            val imageUrl = "https://fastly.picsum.photos/id/1/200/300.jpg?hmac=jH5bDkLr6Tgy3oAg5khKCHeunZMHq0ehBZr6vGifPLY"
            val originalImage = loadImageFromURL(imageUrl)

            if (originalImage != null) {
                // Create circular image with custom size (60x60 pixels)
                val circularImage = createCircularImage(originalImage, 60.0)

                if (circularImage != null) {
                    marker.icon = circularImage
                    marker.snippet = "Circular custom image loaded"
                } else {
                    // Fallback: just resize the image if circular creation fails
                    val resizedImage = resizeImage(originalImage, 60.0)
                    marker.icon = resizedImage
                    marker.snippet = "Resized custom image loaded"
                }
            } else {
                marker.snippet = "Failed to load custom image"
                println("Failed to load image from URL: $imageUrl")
            }

        } catch (e: Exception) {
            // Handle any exceptions that might occur during map setup
            println("Error setting up map: ${e.message}")
        }
    }

    UIKitView(
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative
        ),
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { view ->
            // Any updates can be handled here
        }
    )
}