package com.world.pockyapp.screens.google_maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.navigation.NavHostController
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.screens.home.navigations.discover.UiState
import platform.CoreLocation.CLLocationCoordinate2D
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.UIKit.UIImage
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIBezierPath
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLRequest
import platform.Foundation.dataTaskWithRequest
import platform.CoreGraphics.CGSizeMake
import platform.CoreGraphics.CGRectMake
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.addOverlay
import kotlin.coroutines.resume

import kotlinx.cinterop.*
import platform.CoreLocation.*
import platform.MapKit.*
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class, KoinExperimentalAPI::class)
@Composable
/*
actual fun GoogleMapsScreen(navController: NavHostController) {

    val viewModel: GoogleMapsViewModel = koinViewModel()
    val globalMomentsState by viewModel.globalMomentsState.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.loadGlobalMoments()
    }

    val mapView = remember {
        GMSMapView()
    }

    val profiles = remember(globalMomentsState) {
        when (val state = globalMomentsState) {
            is UiState.Success -> {
                state.data
            }
            else -> emptyList()
        }
    }.filter { it.moments.isNotEmpty() }

    println("sdsdsdss-> " +profiles)

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

            for (item in profiles){

                val marker = GMSMarker().apply {
                    position = cValue<CLLocationCoordinate2D> {
                        latitude = item.moments[0].geoLocation.latitude
                        longitude = item.moments[0].geoLocation.longitude
                    }
                    map = mapView
                    appearAnimation = 44u
                }
                // Load custom image from internet and update marker
                //val imageUrl = "https://fastly.picsum.photos/id/1/200/300.jpg?hmac=jH5bDkLr6Tgy3oAg5khKCHeunZMHq0ehBZr6vGifPLY"
                val imageUrl = getUrl(item.moments[0].momentID)

                println("aqawaqwaq ${imageUrl}")

                val originalImage = loadImageFromURL(imageUrl)
                if (originalImage != null) {
                    // Create circular image with custom size (60x60 pixels)
                    val circularImage = createCircularImage(originalImage, 60.0)

                    if (circularImage != null) {
                        marker.icon = circularImage
                    } else {
                        // Fallback: just resize the image if circular creation fails
                        val resizedImage = resizeImage(originalImage, 60.0)
                        marker.icon = resizedImage
                    }
                } else {
                    println("Failed to load image from URL: $imageUrl")
                }
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
*/
/*
actual fun GoogleMapsScreen(navController: NavHostController) {
    UIKitView(
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative
        ),
        factory = {
            val mapView = MKMapView().apply {
                mapType = MKMapTypeStandard

            }

            // Sample points - replace with your data
            val points = listOf(
                CLLocationCoordinate2DMake(37.7749, -122.4194),
                CLLocationCoordinate2DMake(37.7849, -122.4094)
            )

            // Add circles for heatmap effect
            points.forEach { point ->
                val circle = MKCircle.circleWithCenterCoordinate(point, radius = 500.0)
                mapView.addOverlay(circle)
            }

            // Handle circle rendering
            mapView.delegate = object : NSObject(), MKMapViewDelegateProtocol {

                override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol): MKOverlayRenderer {
                    return MKCircleRenderer(circle = rendererForOverlay as MKCircle).apply {
                        fillColor = UIColor.redColor.colorWithAlphaComponent(0.3)
                        strokeColor = UIColor.clearColor
                    }
                }
            }


            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}*/

actual fun GoogleMapsScreen(navController: NavHostController) {
    UIKitView(
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative
        ),
        factory = {
            val mapView = MKMapView().apply {
                mapType = MKMapTypeStandard
            }

            val points = listOf(
                CLLocationCoordinate2DMake(31.6295, -7.9811), // center Marrakech
                CLLocationCoordinate2DMake(31.6340, -7.9900), // slightly west
                CLLocationCoordinate2DMake(31.6240, -7.9700)  // slightly east
            )
            // Add semi-transparent colored circles to simulate heat
            points.forEachIndexed { index, point ->
                val circle = MKCircle.circleWithCenterCoordinate(
                    coord = point,
                    radius = 300.0
                )
                mapView.addOverlay(circle)
            }


            mapView.delegate = object : NSObject(), MKMapViewDelegateProtocol {
                override fun mapView(
                    mapView: MKMapView,
                    rendererForOverlay: MKOverlayProtocol
                ): MKOverlayRenderer{
                    val renderer = MKCircleRenderer(rendererForOverlay)
                    renderer.fillColor = when ((0..2).random()) {
                        0 -> UIColor.redColor.colorWithAlphaComponent(0.3)
                        1 -> UIColor.yellowColor.colorWithAlphaComponent(0.3)
                        else -> UIColor.greenColor.colorWithAlphaComponent(0.3)
                    }
                    renderer.strokeColor = UIColor.clearColor

                    return renderer
                }

            }

            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}


