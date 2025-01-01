package com.world.pockyapp.screens.home.navigations.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
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
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_like
import pockyapp.composeapp.generated.resources.ic_unlike_black

@Composable
fun DiscoverScreen(
    navController: NavHostController,
    viewModel: DiscoverViewModel = koinViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val friendsMomentsState by viewModel.friendsMomentsState.collectAsState()
    val nearbyMomentsState by viewModel.nearbyMomentsState.collectAsState()
    val nearbyPostsState by viewModel.nearbyPostsState.collectAsState()

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
            Spacer(modifier = Modifier.size(10.dp))
        }

        // Friends Moments Section
        item {

            Row (verticalAlignment = Alignment.CenterVertically){

                when (profileState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        ErrorSection(
                            message = (profileState as UiState.Error).message,
                            onRetry = { viewModel.getProfile() }
                        )
                    }
                    is UiState.Success -> {
                        val profile = (profileState as UiState.Success<ProfileModel>).data
                        ProfileSection(profile = profile, navController = navController)
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                Divider(modifier = Modifier.height(45.dp).width(1.dp), color = Color.Black)

                Spacer(modifier = Modifier.width(10.dp))

                when (friendsMomentsState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        ErrorSection(
                            message = (friendsMomentsState as UiState.Error).message,
                            onRetry = { viewModel.loadFriendsMoments() }
                        )
                    }
                    is UiState.Success -> {
                        val friends = (friendsMomentsState as UiState.Success<List<ProfileModel>>).data
                        FriendsMomentsSection(
                            friends = friends,
                            currentProfile = (profileState as? UiState.Success<ProfileModel>)?.data,
                            navController = navController
                        )
                    }
                }
            }

        }

        // Nearby Moments Section
        item {
            when (nearbyMomentsState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    ErrorSection(
                        message = (nearbyMomentsState as UiState.Error).message,
                        onRetry = { viewModel.loadNearbyMoments() }
                    )
                }
                is UiState.Success -> {
                    val nearbyMoments = (nearbyMomentsState as UiState.Success<List<ProfileModel>>).data
                    if (nearbyMoments.isNotEmpty()) {
                        NearbyMomentsSection(
                            moments = nearbyMoments,
                            currentProfile = (profileState as? UiState.Success<ProfileModel>)?.data,
                            navController = navController
                        )
                    }
                }
            }
        }

        // Nearby Posts Section

        item {
            Spacer(modifier = Modifier.size(20.dp))

        }
        when (nearbyPostsState) {
            is UiState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is UiState.Error -> {
                item {
                    ErrorSection(
                        message = (nearbyPostsState as UiState.Error).message,
                        onRetry = { viewModel.loadNearbyPosts() }
                    )
                }
            }
            is UiState.Success -> {
                val posts = (nearbyPostsState as UiState.Success<List<PostModel>>).data
                if (posts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Nearby posts >",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                    items(posts, key = { it.postID }) { post ->
                        PostItem(
                            post = post,
                            currentUserId = (profileState as? UiState.Success<ProfileModel>)?.data?.id,
                            onLikeClick = { clickedPost ->
                                (profileState as? UiState.Success<ProfileModel>)?.data?.id?.let { userId ->
                                    viewModel.toggleLike(clickedPost.postID, userId)
                                }
                            },
                            onProfileClick = { userId ->
                                if (userId == (profileState as? UiState.Success<ProfileModel>)?.data?.id) {
                                    navController.navigate(NavRoutes.MY_PROFILE.route)
                                } else {
                                    navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/$userId")
                                }
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}

@Composable
fun ErrorSection(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Retry",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onRetry)
                .padding(8.dp)
        )
    }
}

@Composable
fun ProfileSection(
    profile: ProfileModel,
    navController: NavHostController
) {
    val checkIfSeeAllMoments = profile.moments.find { !it.viewed }
    Box(
        modifier = Modifier
            .size(60.dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = if (checkIfSeeAllMoments != null) {
                        listOf(Color.Red, Color.Yellow, Color.White)
                    } else {
                        listOf(Color.Gray, Color.Gray, Color.Gray)
                    }
                ),
                shape = CircleShape
            ),
    ) {
        AsyncImage(
            model = if (profile.moments.isEmpty()) getUrl(profile.photoID) else getUrl(profile.moments[0].postID),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable {
                    if (profile.moments.isEmpty()) {
                        navController.navigate(NavRoutes.MY_PROFILE.route)
                    } else {
                        val modulesJson = Json.encodeToString(listOf(profile)).replace("/", "%")
                        navController.navigate(
                            NavRoutes.MOMENTS.route + "/${modulesJson}" + "/0" + "/${profile.id}"
                        )
                    }
                },
            contentDescription = null
        )
    }
}

@Composable
fun FriendsMomentsSection(
    friends: List<ProfileModel>,
    currentProfile: ProfileModel?,
    navController: NavHostController
) {
    LazyRow {
        items(friends, key = { it.id }) { friend ->
            MomentItem(
                profile = friend,
                currentUserId = currentProfile?.id,
                navController = navController
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
fun NearbyMomentsSection(
    moments: List<ProfileModel>,
    currentProfile: ProfileModel?,
    navController: NavHostController
) {
    Spacer(modifier = Modifier.size(20.dp))

    Text(
        text = "Nearby moments >",
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(5.dp))

    LazyRow {
        items(moments, key = { it.id }) { profile ->
            if (profile.moments.isNotEmpty()) {
                NearbyMomentItem(
                    profile = profile,
                    currentUserId = currentProfile?.id,
                    navController = navController
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostModel,
    currentUserId: String?,
    onLikeClick: (PostModel) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Profile Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { onProfileClick(post.profile.id) }
        ) {
            AsyncImage(
                model = getUrl(post.profile.photoID),
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
                model = getUrl(post.postID),
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
                    .clickable { onLikeClick(post) }
            )

            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = post.likes.size.toString(),
                color = Color.Black,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = formatCreatedAt(post.createdAt),
                color = Color.Black,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.size(20.dp))
    }
}

@Composable
fun MomentItem(
    profile: ProfileModel,
    currentUserId: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.height(60.dp).width(60.dp)) {
        val checkIfSeeAllMoments = profile.moments.find { !it.viewed }
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = if (checkIfSeeAllMoments != null) {
                            listOf(Color.Red, Color.Yellow, Color.White)
                        } else {
                            listOf(Color.Gray, Color.Gray, Color.Gray)
                        }
                    ),
                    shape = CircleShape
                ),
        ) {
            AsyncImage(
                model = if (profile.moments.isEmpty()) {
                    getUrl(profile.photoID)
                } else {
                    getUrl(profile.moments[0].postID)
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (profile.moments.isEmpty()) {
                            navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${profile.id}")
                        } else {

                            val modulesJson = Json.encodeToString(listOf(profile))
                                .replace("/", "%")
                            navController.navigate(
                                NavRoutes.MOMENTS.route + "/${modulesJson}" + "/0" + "/$currentUserId"
                            )
                        }
                    },
                contentDescription = null
            )
        }
    }
}

@Composable
fun NearbyMomentItem(
    profile: ProfileModel,
    currentUserId: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val checkIfSeeAllMoments = profile.moments.find { !it.viewed }
    Box(
        modifier = modifier
            .height(150.dp)
            .width(90.dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = if (checkIfSeeAllMoments != null) {
                        listOf(Color.Red, Color.Yellow, Color.White)
                    } else {
                        listOf(Color.Gray, Color.Gray, Color.Gray)
                    }
                ),
                shape = RoundedCornerShape(10.dp)
            ),
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = Color.LightGray,
            modifier = Modifier
                .height(150.dp)
                .width(90.dp)
        ) {
            AsyncImage(
                model = getUrl(profile.moments[0].postID),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        val modulesJson = Json.encodeToString(listOf(profile))
                            .replace("/", "%")
                        navController.navigate(
                            NavRoutes.MOMENTS.route + "/${modulesJson}" + "/0" + "/$currentUserId"
                        )
                    },
                contentDescription = null
            )
        }
    }
}

// Optional: Helper composable for common moment border
@Composable
private fun MomentBorder(
    hasUnviewedContent: Boolean,
    shape: androidx.compose.ui.graphics.Shape,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = if (hasUnviewedContent) {
                    listOf(Color.Red, Color.Yellow, Color.White)
                } else {
                    listOf(Color.Gray, Color.Gray, Color.Gray)
                }
            ),
            shape = shape
        )
    ) {
        content()
    }
}