package com.world.pockyapp.screens.home.navigations.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
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
import pockyapp.composeapp.generated.resources.ic_like
import pockyapp.composeapp.generated.resources.ic_unlike_black
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DiscoverScreen(
    navController: NavHostController,
    viewModel: DiscoverViewModel = koinViewModel()
) {

    val friendsMomentsState by viewModel.friendsMoments.collectAsState()
    val nearbyMomentsState by viewModel.nearbyMoments.collectAsState()
    val nearbyPostsState by viewModel.nearbyPosts.collectAsState()
    val unLikeState by viewModel.unLikePost.collectAsState()
    val likeState by viewModel.likePost.collectAsState()
    val profileState by viewModel.profileState.collectAsState()

    println("DiscoverScreen $friendsMomentsState")
    println("DiscoverScreen $nearbyMomentsState")
    println("DiscoverScreen $nearbyPostsState")
    println("DiscoverScreen $profileState")

    LaunchedEffect(Unit) {
        viewModel.getProfile()
        viewModel.loadFriendsMoments()
        viewModel.loadNearbyMoments()
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
                items(friendsMomentsState, key = { it.id }) { item: ProfileModel ->

                    if (item.moments.isEmpty()) {
                        //return@items
                    }
                    Row(modifier = Modifier.height(70.dp).width(70.dp)) {
                        val checkIfSeeAllMoments =
                            item.moments.find { !it.viewed }
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = if (checkIfSeeAllMoments != null) listOf(
                                            Color.Red,
                                            Color.Yellow,
                                            Color.White
                                        ) else listOf(
                                            Color.Gray,
                                            Color.Gray,
                                            Color.Gray
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                        ) {
                            AsyncImage(
                                model = if (item.moments.isEmpty()) getUrl(item.photoID) else getUrl(
                                    item.moments[0].postID
                                ),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(60.dp).clip(CircleShape).clickable {
                                    if (item.moments.isEmpty()) {
                                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${item.id}")
                                    } else {
                                        val modulesJson =
                                            Json.encodeToString(friendsMomentsState)
                                                .replace("/", "%")
                                        navController.navigate(
                                            NavRoutes.MOMENTS.route + "/${modulesJson}" + "/${
                                                friendsMomentsState.indexOf(
                                                    item
                                                )
                                            }" + "/" + { false }
                                        )
                                    }

                                },
                                contentDescription = null
                            )
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
                    nearbyMomentsState, key = { it.id }
                ) { item: ProfileModel ->

                    if (item.moments.isEmpty()) {
                        return@items
                    }
                    Row {

                        val checkIfSeeAllMoments =
                            item.moments.find { !it.viewed }
                        Box(
                            modifier = Modifier
                                .height(150.dp).width(90.dp)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = if (checkIfSeeAllMoments != null) listOf(
                                            Color.Red,
                                            Color.Yellow,
                                            Color.White
                                        ) else listOf(
                                            Color.Gray,
                                            Color.Gray,
                                            Color.Gray
                                        )
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                        ) {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                backgroundColor = Color.LightGray,
                                modifier = Modifier.height(150.dp).width(90.dp)

                            ) {
                                AsyncImage(
                                    model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${item.moments[0].postID}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clickable {
                                        val modulesJson =
                                            Json.encodeToString(nearbyMomentsState)
                                                .replace("/", "%")

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

        items(nearbyPostsState, key = { it.postID }) { item: PostModel ->

            PostItem(
                post = item,
                currentUserId = profileState?.id,
                onLikeClick = { clickedPost ->
                    profileState?.id?.let { userId ->
                        viewModel.toggleLike(clickedPost.postID, userId)
                    }
                },
                onProfileClick = { userId ->
                    navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/$userId")

                },
            )
        }

        item {
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}


@Composable
fun PostItem(
    post: PostModel,
    currentUserId: String?,
    onLikeClick: (PostModel) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Profile Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
                .clickable { post.profile.id.let { onProfileClick(it) } }
        ) {
            AsyncImage(
                model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${post.profile.photoID}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape),
                contentDescription = "Profile Photo"
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "${post.profile.firstName} ${post.profile.lastName}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Post Image
        Card(
            backgroundColor = Color.LightGray,
            elevation = 0.dp,
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${post.postID}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Post Image"
            )
        }

        // Like Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            val isLiked = post.likes.contains(currentUserId)
            Image(
                painter = if (isLiked) {

                    painterResource(Res.drawable.ic_like)
                } else {
                    painterResource(Res.drawable.ic_unlike_black)
                },
                contentDescription = if (isLiked) "Unlike" else "Like",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {

                        onLikeClick(post)
                    }
            )

            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = post.likes.size.toString(),
                color = Color.Black,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.size(20.dp))
    }
}
