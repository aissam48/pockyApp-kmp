package com.world.pockyapp.screens.home.navigations.discover

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.ItemMapView
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.home.navigations.discover.UiState
import com.world.pockyapp.screens.moment_screen.MomentsViewModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import com.world.pockyapp.utils.Utils.formatCreatedAt
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder

// Design System
object DiscoverTheme {
    val SpacingXSmall = 4.dp
    val SpacingSmall = 8.dp
    val SpacingMedium = 10.dp
    val SpacingLarge = 24.dp
    val SpacingXLarge = 32.dp

    val Primary = Color(0xFF007AFF)
    val Secondary = Color(0xFFFF3040)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF8F9FA)
    val OnSurface = Color(0xFF1C1C1E)
    val OnSurfaceVariant = Color(0xFF8E8E93)
    val Border = Color(0xFFE5E5EA)

    val GradientPrimary = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCAF45))
    val GradientViewed = listOf(Color(0xFFE5E5EA), Color(0xFFE5E5EA))
    val GradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFFAFBFC), Color(0xFFFFFFFF))
    )
}

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
    val followingsPostsState by viewModel.followingsPostsState.collectAsState()

    var selectedTab by remember { mutableStateOf(FeedTab.Following) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getProfile()

        viewModel.loadMyDailyMoments()
        viewModel.loadFriendsMoments()

        viewModel.loadNearbyMoments()
        viewModel.loadNearbyPosts()

        viewModel.loadFollowingsMoments()
        viewModel.loadFollowingsPosts()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingMedium)
        ) {

            // Stories Section
            item {
                AnimatedStoriesSection(
                    myDailyMomentsState = myDailyMomentsState,
                    friendsMomentsState = friendsMomentsState,
                    profileState = profileState,
                    navController = navController
                )
            }

            item {
                Spacer(modifier = Modifier.height(DiscoverTheme.SpacingSmall))
            }

            // Tab Selector
            item {
                ModernTabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Content based on selected tab
            when (selectedTab) {
                FeedTab.Following -> {
                    followingsMomentsContent(
                        state = followingsMomentsState,
                        postsState = followingsPostsState,
                        profileState = profileState,
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                FeedTab.Nearby -> {
                    nearbyContent(
                        momentsState = nearbyMomentsState,
                        postsState = nearbyPostsState,
                        profileState = profileState,
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                FeedTab.Global -> {
                    item {
                        GlobalMapSection(navController = navController)
                    }
                }
            }
        }

    }
}

enum class FeedTab(val title: String, val icon: ImageVector) {
    Following("Following", Icons.Outlined.Notifications),
    Nearby("Nearby", Icons.Outlined.LocationOn),
    Global("Global", Icons.Outlined.MoreVert)
}

@Composable
fun AnimatedStoriesSection(
    myDailyMomentsState: UiState<List<MomentModel>>,
    friendsMomentsState: UiState<List<MomentModel>>,
    profileState: UiState<ProfileModel>,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Friends",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiscoverTheme.OnSurface
                )
            }

            Spacer(modifier = Modifier.height(DiscoverTheme.SpacingSmall))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingMedium)
            ) {
                // My Story
                item {
                    when (myDailyMomentsState) {
                        is UiState.Loading -> StoryItemSkeleton()
                        is UiState.Success -> {
                            MyStoryItem(
                                myDailyMoments = myDailyMomentsState.data,
                                profileState = profileState,
                                navController = navController
                            )
                        }

                        is UiState.Error -> StoryErrorItem()
                    }
                }

                // Friends Stories
                when (friendsMomentsState) {
                    is UiState.Loading -> {
                        items(5) { StoryItemSkeleton() }
                    }

                    is UiState.Success -> {
                        val friendsMoments = friendsMomentsState.data
                        val groupedMoments = friendsMoments
                            .groupBy { it.profile.id }
                            .map { it.value }

                        items(groupedMoments) { friendMoments ->
                            AnimatedStoryItem(
                                moments = friendMoments,
                                onClick = {
                                    // Navigate to moments
                                }
                            )
                        }
                    }

                    is UiState.Error -> {
                        item { StoryErrorItem() }
                    }
                }
            }
        }
    }
}

@Composable
fun MyStoryItem(
    myDailyMoments: List<MomentModel>,
    profileState: UiState<ProfileModel>,
    navController: NavHostController
) {
    val momentsViewModel: MomentsViewModel = koinViewModel()
    val hasUnviewed = myDailyMoments.any { !it.viewed }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box {
            StoryRing(
                hasUnviewed = hasUnviewed,
                size = 70.dp,
                onClick = {
                    if (myDailyMoments.isEmpty()) {
                        navController.navigate(NavRoutes.MY_PROFILE.route)
                    } else {
                        momentsViewModel.myID = myDailyMoments[0].profile.id
                        momentsViewModel.selectedIndex = 0
                        momentsViewModel.moments = mutableListOf(myDailyMoments)
                        navController.navigate(NavRoutes.MOMENTS.route)
                    }
                }
            ) {
                AsyncImage(
                    model = if (myDailyMoments.isEmpty()) {
                        (profileState as? UiState.Success)?.data?.photoUrl
                    } else {
                        myDailyMoments[0].mediaUrl
                    },
                    contentScale = ContentScale.Crop,
                    contentDescription = "My story",
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder)
                )
            }

            // Add button for empty state
            if (myDailyMoments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFFDFC46B), CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add story",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DiscoverTheme.SpacingXSmall))

        Text(
            text = "Your Moment",
            fontSize = 12.sp,
            color = DiscoverTheme.OnSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AnimatedStoryItem(
    moments: List<MomentModel>,
    onClick: () -> Unit
) {
    val hasUnviewed = moments.any { !it.viewed }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        StoryRing(
            hasUnviewed = hasUnviewed,
            size = 70.dp,
            onClick = onClick
        ) {
            AsyncImage(
                model = moments.first().profile.photoUrl,
                contentScale = ContentScale.Crop,
                contentDescription = "${moments.first().profile.firstName}'s story",
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder)
            )
        }

        Spacer(modifier = Modifier.height(DiscoverTheme.SpacingXSmall))

        Text(
            text = moments.first().profile.firstName,
            fontSize = 12.sp,
            color = DiscoverTheme.OnSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StoryRing(
    hasUnviewed: Boolean,
    size: Dp,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = if (hasUnviewed) Color.Gray else Color(0xFFDFC46B),
                shape = CircleShape
            )
            .padding(3.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DiscoverTheme.Surface, CircleShape)
                .clip(CircleShape),
            content = content
        )
    }
}

@Composable
fun ModernTabSelector(
    selectedTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.SurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            FeedTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) DiscoverTheme.Surface else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingXSmall)
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) Color(0xFFDFC46B) else Color(0xFF000000),
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = tab.title,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFDFC46B) else Color(0xFF000000)
                        )
                    }
                }
            }
        }
    }
}

fun LazyListScope.followingsMomentsContent(
    state: ResponseState<List<MomentModel>>,
    profileState: UiState<ProfileModel>,
    navController: NavHostController,
    viewModel: DiscoverViewModel,
    postsState: ResponseState<List<PostModel>>
) {
    when (state) {
        is ResponseState.Loading -> {
            item { LoadingSection() }
        }

        is ResponseState.Success -> {
            val moments = state.data
            if (moments.isNotEmpty()) {
                item {
                    MomentsGrid(
                        moments = moments,
                        profileState = profileState,
                        navController = navController
                    )
                }
            }
        }

        is ResponseState.Error -> {
            item {
                ErrorSection(
                    error = state.error,
                    onRetry = { viewModel.loadFollowingsMoments() }
                )
            }
        }

        else -> {}
    }

    when (postsState) {
        is ResponseState.Success -> {
            val posts = postsState.data
            if (posts.isNotEmpty()) {
                item {
                    SectionHeader(title = "Following Posts")
                }
                items(posts, key = { it.id }) { post ->
                    ModernPostCard(
                        post = post,
                        currentUserId = (profileState as? UiState.Success)?.data?.id,
                        onLikeClick = { clickedPost ->
                            (profileState as? UiState.Success)?.data?.id?.let { userId ->
                                viewModel.toggleLike(clickedPost.id, userId)
                            }
                        },
                        onProfileClick = { userId ->
                            if (userId == (profileState as? UiState.Success)?.data?.id) {
                                navController.navigate(NavRoutes.MY_PROFILE.route)
                            } else {
                                navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/$userId")
                            }
                        }
                    )
                }
            }
        }

        is ResponseState.Loading -> {
            item { LoadingSection() }
        }

        is ResponseState.Error -> {
            item {
                ErrorSection(
                    error = postsState.error,
                    onRetry = { viewModel.loadFollowingsPosts() }
                )
            }
        }

        is ResponseState.Idle -> {}
    }

}

fun LazyListScope.nearbyContent(
    momentsState: UiState<List<MomentModel>>,
    postsState: UiState<List<PostModel>>,
    profileState: UiState<ProfileModel>,
    navController: NavHostController,
    viewModel: DiscoverViewModel
) {
    // Nearby Moments
    when (momentsState) {
        is UiState.Success -> {
            val moments = momentsState.data
            if (moments.isNotEmpty()) {
                item {
                    MomentsGrid(
                        moments = moments,
                        profileState = profileState,
                        navController = navController
                    )
                }
            }
        }

        is UiState.Loading -> {
            item { LoadingSection() }
        }

        is UiState.Error -> {
            item {
                ErrorSection(
                    error = momentsState.error,
                    onRetry = { viewModel.loadNearbyMoments() }
                )
            }
        }
    }

    // Nearby Posts
    when (postsState) {
        is UiState.Success -> {
            val posts = postsState.data
            if (posts.isNotEmpty()) {
                items(posts, key = { it.id }) { post ->
                    ModernPostCard(
                        post = post,
                        currentUserId = (profileState as? UiState.Success)?.data?.id,
                        onLikeClick = { clickedPost ->
                            (profileState as? UiState.Success)?.data?.id?.let { userId ->
                                viewModel.toggleLike(clickedPost.id, userId)
                            }
                        },
                        onProfileClick = { userId ->
                            if (userId == (profileState as? UiState.Success)?.data?.id) {
                                navController.navigate(NavRoutes.MY_PROFILE.route)
                            } else {
                                navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/$userId")
                            }
                        }
                    )
                }
            }
        }

        is UiState.Loading -> {
            item { LoadingSection() }
        }

        is UiState.Error -> {
            item {
                ErrorSection(
                    error = postsState.error,
                    onRetry = { viewModel.loadNearbyPosts() }
                )
            }
        }
    }
}

@Composable
fun MomentsGrid(
    moments: List<MomentModel>,
    profileState: UiState<ProfileModel>,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.Surface),
    ) {
        Column {

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingSmall)
            ) {
                val groupedMoments = moments.groupBy { it.profile.id }.map { it.value }
                items(groupedMoments) { profileMoments ->
                    MomentCard(
                        moments = profileMoments,
                        onClick = {
                            // Navigate to moments viewer
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MomentCard(
    moments: List<MomentModel>,
    onClick: () -> Unit
) {
    val hasUnviewed = moments.any { !it.viewed }

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            AsyncImage(
                model = moments.first().mediaUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "${moments.first().profile.firstName}'s moment",
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder)
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Profile info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = moments.first().profile.photoUrl,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape),
                        contentDescription = "Post image",
                        placeholder = painterResource(Res.drawable.ic_placeholder),
                        error = painterResource(Res.drawable.ic_placeholder)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = moments.first().profile.firstName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = formatCreatedAt(moments.first().createdAt),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ModernPostCard(
    post: PostModel,
    currentUserId: String?,
    onLikeClick: (PostModel) -> Unit,
    onProfileClick: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val isLiked = post.likes.contains(currentUserId)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DiscoverTheme.SpacingMedium)
                    .clickable { onProfileClick(post.profile.id) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.profile.photoUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentDescription = "Profile",
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder)
                )

                Spacer(modifier = Modifier.width(DiscoverTheme.SpacingSmall))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${post.profile.firstName} ${post.profile.lastName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscoverTheme.OnSurface
                    )
                }

                Text(
                    text = formatCreatedAt(post.createdAt),
                    fontSize = 10.sp,
                    color = DiscoverTheme.OnSurfaceVariant
                )
            }

            // Image
            AsyncImage(
                model = post.mediaUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentDescription = "Post image",
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder)
            )

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DiscoverTheme.SpacingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedLikeButton(
                    isLiked = isLiked,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLikeClick(post)
                    }
                )

                Spacer(modifier = Modifier.width(DiscoverTheme.SpacingSmall))

                Text(
                    text = "${post.likes.size} likes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DiscoverTheme.OnSurface
                )

            }
        }
    }
}

@Composable
fun AnimatedLikeButton(
    isLiked: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )

    val rotation by animateFloatAsState(
        targetValue = if (isLiked) 360f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = if (isLiked) DiscoverTheme.Secondary.copy(alpha = 0.1f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .scale(scale)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isLiked) "Unlike" else "Like",
            tint = if (isLiked) DiscoverTheme.Secondary else DiscoverTheme.OnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GlobalMapSection(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(DiscoverTheme.SpacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Global Explorer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscoverTheme.OnSurface
                    )
                    Text(
                        text = "Discover moments worldwide",
                        fontSize = 14.sp,
                        color = DiscoverTheme.OnSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Global",
                    tint = DiscoverTheme.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(DiscoverTheme.SpacingMedium))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                ItemMapView(navController)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = DiscoverTheme.OnSurface,
        modifier = Modifier.padding(horizontal = DiscoverTheme.SpacingMedium)
    )
}

@Composable
fun LoadingSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.Surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingSmall)
            ) {
                CircularProgressIndicator(
                    color = DiscoverTheme.Primary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Loading...",
                    fontSize = 14.sp,
                    color = DiscoverTheme.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ErrorSection(
    error: ErrorModel,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(DiscoverTheme.SpacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                tint = DiscoverTheme.Secondary,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = "Something went wrong",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DiscoverTheme.Secondary,
                textAlign = TextAlign.Center
            )

            Text(
                text = error.message,
                fontSize = 14.sp,
                color = DiscoverTheme.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DiscoverTheme.Secondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Retry",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StoryItemSkeleton() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    brush = shimmerBrush(),
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(DiscoverTheme.SpacingXSmall))

        Box(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp)
                .background(
                    brush = shimmerBrush(),
                    shape = RoundedCornerShape(6.dp)
                )
        )
    }
}

@Composable
fun StoryErrorItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    DiscoverTheme.Border,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                tint = DiscoverTheme.OnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(DiscoverTheme.SpacingXSmall))

        Text(
            text = "Error",
            fontSize = 12.sp,
            color = DiscoverTheme.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        DiscoverTheme.Border.copy(alpha = 0.6f),
        DiscoverTheme.Border.copy(alpha = 0.2f),
        DiscoverTheme.Border.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}