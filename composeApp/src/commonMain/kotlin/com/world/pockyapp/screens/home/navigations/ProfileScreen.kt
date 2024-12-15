package com.world.pockyapp.screens.home.navigations

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.ImagePicker
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_add_black
import pockyapp.composeapp.generated.resources.ic_add_post_black
import pockyapp.composeapp.generated.resources.ic_location_black
import pockyapp.composeapp.generated.resources.is_add_story_black

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {

    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )


    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        scaffoldState = scaffoldState,
        sheetBackgroundColor = Color.LightGray,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                        .clickable {
                            navController.navigate(NavRoutes.CAMERA.route)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.is_add_story_black),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = "Share Moment",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                        .clickable {
                            navController.navigate(NavRoutes.PICKER.route)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add_post_black),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = "Share Post",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }
            }

        }
    ) {
        LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

            item {

                Box(modifier = Modifier.size(150.dp)) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp).clip(CircleShape),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )

                    Icon(
                        painterResource(Res.drawable.ic_add_black),
                        null,
                        modifier = Modifier.align(Alignment.BottomEnd).clickable {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                }

            }

            item {
                Spacer(modifier = Modifier.size(20.dp))
            }

            item {
                Text(
                    text = "Aissam elboudi",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    maxLines = 2
                )
            }

            item {
                Spacer(modifier = Modifier.size(10.dp))
            }

            item {
                Row {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(20.dp).clip(CircleShape),
                        painter = painterResource(Res.drawable.ic_location_black),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(5.dp))

                    Text(
                        text = "Casablanca",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 2
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(10.dp))
            }

            item {
                Text(
                    text = "Don't talk until do it, i really mean it, don't put all your effort on a thing u don't know it so well",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )
            }

            item {
                Spacer(modifier = Modifier.size(20.dp))
            }

            items(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1).chunked(3)) { item ->

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(convertPxToDp(screenSize.value.first / 3).dp)
                            .clip(RectangleShape),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(convertPxToDp(screenSize.value.first / 3).dp)
                            .clip(RectangleShape),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(convertPxToDp(screenSize.value.first / 3).dp)
                            .clip(RectangleShape),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )
                }

            }

            item {
                Spacer(modifier = Modifier.size(80.dp))
            }

        }
        Layout(
            modifier = Modifier.fillMaxWidth().height(0.dp),
            measurePolicy = { measurables, constraints ->
                // Use the max width and height from the constraints
                val width = constraints.maxWidth
                val height = constraints.maxHeight

                screenSize.value = Pair(width, height)
                println("Width: $width, height: $height")

                layout(width, height) {

                }
            }
        )
    }


}

@Composable
fun convertPxToDp(px: Int): Float {
    val density = LocalDensity.current
    return with(density) {
        px.toDp().value
    }
}