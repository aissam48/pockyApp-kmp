package com.world.pockyapp.screens.home.navigations.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.world.pockyapp.ItemMapView
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.moment_screen.MomentsViewModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import com.world.pockyapp.utils.Utils.formatCreatedAt
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_like
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.ic_unlike_black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    navController: NavHostController,
    viewModel: DiscoverViewModel = koinViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val myDailyMomentsState by viewModel.myDailyMomentsState.collectAsState()
    val friendsMomentsState by viewModel.friendsMomentsState.collectAsState()
    val nearbyMomentsState by viewModel.nearbyMomentsState.collectAsState()
    val nearbyPostsState by viewModel.nearbyPostsState.collectAsState()
    val followingsMomentsState by viewModel.followingsMomentsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getProfile()
        viewModel.loadMyDailyMoments()
        viewModel.loadFriendsMoments()
        viewModel.loadNearbyMoments()
        viewModel.loadNearbyPosts()
        viewModel.loadFollowingsMoments()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(horizontal = 0.dp)
    ) {

        // Friends Stories Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Friends",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // My Story
                        when (myDailyMomentsState) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier.size(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFFDFC46B)
                                    )
                                }
                            }

                            is UiState.Success -> {
                                val myDailyMoments =
                                    (myDailyMomentsState as UiState.Success<List<MomentModel>>).data
                                ModernProfileSection(
                                    myDailyMomentsRandom = myDailyMoments,
                                    navController = navController,
                                    profileState = profileState
                                )
                            }

                            is UiState.Error -> {
                                Box(
                                    modifier = Modifier.size(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("❌", fontSize = 24.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Friends Stories
                        when (friendsMomentsState) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier.weight(1f).height(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFFDFC46B)
                                    )
                                }
                            }

                            is UiState.Success -> {
                                val friendsMoments =
                                    (friendsMomentsState as UiState.Success<List<MomentModel>>).data
                                val friends = friendsMoments.map { it.profile }.distinctBy { it.id }
                                val groupedFriendsMoments =
                                    mutableListOf<MutableList<MomentModel>>()

                                friends.forEach { friend ->
                                    val friendMoments =
                                        friendsMoments.filter { it.profile.id == friend.id }
                                    groupedFriendsMoments.add(friendMoments.toMutableList())
                                }

                                LazyRow(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(groupedFriendsMoments) { friendMoments ->
                                        ModernMomentItem(
                                            friendMoments = friendMoments,
                                            currentUserId = (profileState as? UiState.Success<ProfileModel>)?.data?.id,
                                            navController = navController,
                                            groupedFriendsMoments = groupedFriendsMoments
                                        )
                                    }
                                }
                            }

                            is UiState.Error -> {
                                Box(
                                    modifier = Modifier.weight(1f).height(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Unable to load stories",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Nearby Moments Section
        when (followingsMomentsState) {
            is ResponseState.Loading -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFDFC46B),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }

            is ResponseState.Success -> {

                val friendsMoments =
                    (followingsMomentsState as ResponseState.Success<List<MomentModel>>).data
                val friends = friendsMoments.map { it.profile }.distinctBy { it.id }
                val groupedFriendsMoments =
                    mutableListOf<MutableList<MomentModel>>()

                friends.forEach { friend ->
                    val friendMoments =
                        friendsMoments.filter { it.profile.id == friend.id }
                    groupedFriendsMoments.add(friendMoments.toMutableList())
                }

                if (friendsMoments.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Following",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(groupedFriendsMoments) { profileMoments ->
                                        if (profileMoments.isNotEmpty()) {
                                            ModernNearbyMomentItem(
                                                profileMoments = profileMoments,
                                                currentUserId = (profileState as? UiState.Success<ProfileModel>)?.data?.id,
                                                navController = navController,
                                                groupedFriendsMoments = groupedFriendsMoments
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            is ResponseState.Error -> {
                item {
                    ModernErrorSection(
                        error = (nearbyMomentsState as UiState.Error).error,
                        onRetry = { viewModel.loadNearbyMoments() }
                    )
                }
            }

            else -> {}
        }

        // Nearby Moments Section
        when (nearbyMomentsState) {
            is UiState.Loading -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFDFC46B),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }

            is UiState.Success -> {

                val nearbyMoments = (nearbyMomentsState as UiState.Success<List<MomentModel>>).data
                val friends = nearbyMoments.map { it.profile }.distinctBy { it.id }
                val groupedFriendsMoments =
                    mutableListOf<MutableList<MomentModel>>()

                friends.forEach { friend ->
                    val friendMoments =
                        nearbyMoments.filter { it.profile.id == friend.id }
                    groupedFriendsMoments.add(friendMoments.toMutableList())
                }

                if (nearbyMoments.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Nearby Moments",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                )
                                {
                                    items(groupedFriendsMoments) { profileMoments ->
                                        if (profileMoments.isNotEmpty()) {
                                            ModernNearbyMomentItem(
                                                profileMoments = profileMoments,
                                                currentUserId = (profileState as? UiState.Success<ProfileModel>)?.data?.id,
                                                navController = navController,
                                                groupedFriendsMoments = groupedFriendsMoments
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            is UiState.Error -> {
                item {
                    ModernErrorSection(
                        error = (nearbyMomentsState as UiState.Error).error,
                        onRetry = { viewModel.loadNearbyMoments() }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Global",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            ItemMapView(navController)
        }

        // Nearby Posts Section
        when (nearbyPostsState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFDFC46B),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            is UiState.Success -> {
                val posts = (nearbyPostsState as UiState.Success<List<PostModel>>).data
                if (posts.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Posts",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    items(posts, key = { it.postID }) { post ->
                        ModernPostItem(
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

            is UiState.Error -> {
                item {
                    ModernErrorSection(
                        error = (nearbyPostsState as UiState.Error).error,
                        onRetry = { viewModel.loadNearbyPosts() }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ModernErrorSection(
    error: ErrorModel,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Something went wrong",
                color = Color(0xFFE53E3E),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = error.message,
                color = Color(0xFFE53E3E),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFFE53E3E),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Retry",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ModernProfileSection(
    myDailyMomentsRandom: List<MomentModel>,
    navController: NavHostController,
    profileState: UiState<ProfileModel>
) {

    val myDailyMoments = myDailyMomentsRandom.sortedBy { it.createdAt }
    val momentsViewModel: MomentsViewModel = koinViewModel()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val checkIfSeeAllMoments = myDailyMoments.find { !it.viewed }
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = if (checkIfSeeAllMoments != null) {
                            listOf(
                                Color(0xFFE91E63),
                                Color(0xFFFF9800),
                                Color(0xFFFFEB3B)
                            )
                        } else {
                            listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.3f)
                            )
                        }
                    ),
                    shape = CircleShape
                )
                .padding(2.dp),
        ) {
            AsyncImage(
                model = if (myDailyMoments.isEmpty()) {
                    if (profileState is UiState.Success) {
                        getUrl(profileState.data.photoID)
                    } else {
                        getUrl("")
                    }
                } else {
                    getUrl(myDailyMoments[0].momentID)
                },
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable {
                        if (myDailyMoments.isEmpty()) {
                            navController.navigate(NavRoutes.MY_PROFILE.route)
                        } else {
                            momentsViewModel.myID = myDailyMoments[0].profile.id
                            momentsViewModel.selectedIndex = 0
                            momentsViewModel.moments = mutableListOf(myDailyMoments)

                            navController.navigate(
                                NavRoutes.MOMENTS.route
                            )
                        }
                    },
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your Moment",
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ModernMomentItem(
    friendMoments: MutableList<MomentModel>,
    currentUserId: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    groupedFriendsMoments: MutableList<MutableList<MomentModel>>
) {

    val momentsViewModel: MomentsViewModel = koinViewModel()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val checkIfSeeAllMoments = friendMoments.find { !it.viewed }
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = if (checkIfSeeAllMoments != null) {
                            listOf(
                                Color(0xFFE91E63),
                                Color(0xFFFF9800),
                                Color(0xFFFFEB3B)
                            )
                        } else {
                            listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.3f)
                            )
                        }
                    ),
                    shape = CircleShape
                )
                .padding(2.dp),
        ) {
            AsyncImage(
                model = getUrl(friendMoments[0].profile.photoID),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable {

                        momentsViewModel.moments = groupedFriendsMoments
                        momentsViewModel.myID = currentUserId
                        momentsViewModel.selectedIndex =
                            groupedFriendsMoments.indexOf(friendMoments)
                        navController.navigate(
                            NavRoutes.MOMENTS.route
                        )
                    },
                contentDescription = null,
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = friendMoments[0].profile.firstName,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun ModernNearbyMomentItem(
    profileMoments: MutableList<MomentModel>,
    currentUserId: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    groupedFriendsMoments: MutableList<MutableList<MomentModel>>
) {
    val momentsViewModel: MomentsViewModel = koinViewModel()
    val checkIfSeeAllMoments = profileMoments.find { !it.viewed }
    Card(
        modifier = modifier
            .width(110.dp)
            .height(150.dp)
            .clickable {
                momentsViewModel.moments = groupedFriendsMoments
                momentsViewModel.myID = currentUserId
                momentsViewModel.selectedIndex =
                    groupedFriendsMoments.indexOf(profileMoments)
                navController.navigate(
                    NavRoutes.MOMENTS.route
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            AsyncImage(
                model = getUrl(profileMoments[0].momentID),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null,
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder),
            )

            if (checkIfSeeAllMoments != null) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(8.dp)
                        .background(Color(0xFFE91E63), CircleShape)
                        .align(Alignment.TopEnd)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                Text(
                    text = profileMoments[0].profile.firstName,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ModernPostItem(
    post: PostModel,
    currentUserId: String?,
    onLikeClick: (PostModel) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    )
    {
        Column() {
            // Profile Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clickable { onProfileClick(post.profile.id) }
            ) {
                AsyncImage(
                    model = getUrl(post.profile.photoID),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentDescription = "Profile Photo",
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${post.profile.firstName} ${post.profile.lastName}",
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${post.geoLocation.country}, ${post.geoLocation.street}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = formatCreatedAt(post.createdAt),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                AsyncImage(
                    model = getUrl(post.postID),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Post Image",
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Like Section
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isLiked = post.likes.contains(currentUserId)
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(
                            if (isLiked) Color(0xFFE91E63).copy(alpha = 0.1f) else Color.Gray.copy(
                                alpha = 0.1f
                            ),
                            CircleShape
                        )
                        .clickable { onLikeClick(post) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = if (isLiked) {
                            painterResource(Res.drawable.ic_like)
                        } else {
                            painterResource(Res.drawable.ic_unlike_black)
                        },
                        contentDescription = if (isLiked) "Unlike" else "Like",
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${post.likes.size} likes",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(25.dp))
}