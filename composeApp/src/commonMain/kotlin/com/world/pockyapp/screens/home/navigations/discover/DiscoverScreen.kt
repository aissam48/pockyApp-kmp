package com.world.pockyapp.screens.home.navigations.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.world.pockyapp.Constant
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_unlike_black
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DiscoverScreen(
    navController: NavHostController,
    viewModel: DiscoverViewModel = koinViewModel()
) {

    val friendsMomentsState by viewModel.friendsMoments.collectAsState()
    val nearbyMomentsState by viewModel.nearbyMoments.collectAsState()
    val nearbyPostsState by viewModel.nearbyPosts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFriendsMoments()
        delay(500)
        viewModel.loadNearbyMoments()
        delay(500)
        viewModel.loadNearbyPosts()
    }

    LazyColumn(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {

        item {

            Text(
                text = "Friends",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }
        item {
            LazyRow {
                items(friendsMomentsState) { item: ProfileModel ->

                    if (item.moments.isEmpty()) {
                        return@items
                    }
                    Row(modifier = Modifier.height(70.dp).width(70.dp)) {
                        AsyncImage(
                            model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${item.moments[0].postID}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp).clip(CircleShape).clickable {
                                val modulesJson =
                                    Json.encodeToString(friendsMomentsState).replace("/", "%")
                                navController.navigate(
                                    NavRoutes.MOMENTS.route + "/${modulesJson}" + "/${
                                        friendsMomentsState.indexOf(
                                            item
                                        )
                                    }" + "/" + { false }
                                )
                            },
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(15.dp))
        }
        item {

            Text(
                text = "Nearby moments >",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }
        item {
            LazyRow {

                items(
                    nearbyMomentsState
                ) { item: ProfileModel ->

                    if (item.moments.isEmpty()) {
                        return@items
                    }
                    Row {
                        Card(
                            backgroundColor = Color.LightGray,
                            modifier = Modifier.height(150.dp).width(90.dp)
                                .background(color = Color.Yellow)
                        ) {
                            AsyncImage(
                                model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${item.moments[0].postID}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clickable {
                                    val modulesJson =
                                        Json.encodeToString(nearbyMomentsState).replace("/", "%")

                                    navController.navigate(
                                        NavRoutes.MOMENTS.route + "/${modulesJson}" + "/${
                                            nearbyMomentsState.indexOf(
                                                item
                                            )
                                        }" + "/${false}"
                                    )

                                },
                                contentDescription = null
                            )
                            Column {

                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.size(15.dp))
        }
        item {

            Text(
                text = "Nearby posts >",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }

        items(nearbyPostsState) { item: PostModel ->

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(30.dp).clip(CircleShape).clickable {

                        },
                        painter = rememberAsyncImagePainter("http://${Constant.BASE_URL}:3000/api/v1/stream/media/${item.profile?.photoID}"),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.size(15.dp))

                    Text(
                        text = "${item.profile.firstName} ${item.profile.lastName}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Card(
                    backgroundColor = Color.LightGray,
                    elevation = 0.dp,
                    modifier = Modifier.height(400.dp).fillParentMaxWidth()
                        .background(color = Color.Yellow)
                ) {
                    AsyncImage(
                        model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${item.postID}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = null
                    )
                    Column {

                    }
                }
                Spacer(modifier = Modifier.size(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        painter = painterResource(Res.drawable.ic_unlike_black),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "123", color = Color.Black, fontSize = 18.sp)

                }
                Spacer(modifier = Modifier.size(20.dp))

            }
        }

        item {
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}
