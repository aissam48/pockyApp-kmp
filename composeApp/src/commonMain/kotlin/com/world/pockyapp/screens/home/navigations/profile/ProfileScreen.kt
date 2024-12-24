package com.world.pockyapp.screens.home.navigations.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant
import com.world.pockyapp.navigation.NavRoutes
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_add_black
import pockyapp.composeapp.generated.resources.ic_add_post_black
import pockyapp.composeapp.generated.resources.ic_location_black
import pockyapp.composeapp.generated.resources.ic_settings_black
import pockyapp.composeapp.generated.resources.is_add_story_black

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = koinViewModel()) {


    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val profileState by viewModel.profileState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()

    // Trigger initial data load
    LaunchedEffect(Unit) {
        viewModel.getProfile()
        viewModel.getMyPosts()
    }

    Scaffold { padding ->
        when (val state = profileState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfileUiState.Success -> {
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
                                        navController.navigate(NavRoutes.POST_PREVIEW.route)
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

                            Box(modifier = Modifier.fillMaxWidth()) {

                                Box(modifier = Modifier.size(150.dp)) {
                                    Image(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(150.dp).clip(CircleShape).clickable {
                                            val modulesJson =
                                                Json.encodeToString(listOf(state.profile)).replace("/", "%")
                                            navController.navigate(
                                                NavRoutes.MOMENTS.route + "/${modulesJson}" + "/${0}" + "/" + { true }
                                            )
                                        },
                                        painter = if (state.profile.photoID.isEmpty()) painterResource(
                                            Res.drawable.compose_multiplatform
                                        ) else rememberAsyncImagePainter("http://${Constant.BASE_URL}:3000/api/v1/stream/media/${state.profile.photoID}"),
                                        contentDescription = null
                                    )


                                    Icon(
                                        painterResource(Res.drawable.ic_add_black),
                                        null,
                                        modifier = Modifier.align(Alignment.BottomEnd).clickable {
                                            scope.launch {
                                                scaffoldState.bottomSheetState.expand()
                                            }
                                        }.size(40.dp)
                                    )
                                }

                                Image(
                                    painter = painterResource(Res.drawable.ic_settings_black),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp).align(Alignment.TopEnd).clickable {
                                        navController.navigate(NavRoutes.SETTINGS.route)
                                    }
                                )

                            }

                        }

                        item {
                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        item {
                            Text(
                                text = state.profile.firstName,
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
                                    text = "${state.profile.country}, ${state.profile.city}",
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
                                text = state.profile.description,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 2
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        items(postsState.chunked(3)) { item ->

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                item.forEachIndexed { index, postModel ->
                                    ImagePost(screenSize.value.first, item[index].postID)
                                    if (postModel != item.last()) {
                                        Spacer(modifier = Modifier.size(3.dp))
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.size(3.dp))

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

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }


}

@Composable
fun convertPxToDp(px: Int): Float {
    val density = LocalDensity.current
    return with(density) {
        px.toDp().value
    }
}