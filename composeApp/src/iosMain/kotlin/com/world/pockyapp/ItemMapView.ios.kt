package com.world.pockyapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ItemMapView(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 10.dp).clickable{
                navController.navigate(NavRoutes.MAP_COMPONENT.route)
            }
    ) {
        UIKitViewController(
            factory = mapViewController,
            modifier = Modifier.fillMaxSize(),
            properties = UIKitInteropProperties(interactionMode = UIKitInteropInteractionMode.NonCooperative))
    }

}