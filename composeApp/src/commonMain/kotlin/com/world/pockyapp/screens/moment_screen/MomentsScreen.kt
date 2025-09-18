package com.world.pockyapp.screens.moment_screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.screens.components.CustomDialog
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.*

// Story duration constants
private const val STORY_DURATION_MS = 5000L
private const val LONG_PRESS_THRESHOLD_MS = 200L

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StoriesViewer(
    moments: List<List<MomentModel>>,
    initialUserIndex: Int = 0,
    onStoriesFinished: () -> Unit,
    navController: NavHostController,
    myID: String,
    viewModel: MomentsViewModel
) {
    val pagerState = rememberPagerState(
        initialPage = initialUserIndex,
        pageCount = { moments.size }
    )

    var currentUserIndex by remember { mutableStateOf(initialUserIndex) }
    var isPagerSettled by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Track when pager is settled and which page is active
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        currentUserIndex = pagerState.currentPage
        isPagerSettled = !pagerState.isScrollInProgress
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { userIndex ->
            val momentsOfUser = moments[userIndex]

            StoryUserPage(
                userStories = momentsOfUser,
                isActive = userIndex == currentUserIndex && isPagerSettled,
                isPagerSettled = isPagerSettled,
                onNavigateToNextUser = {
                    if (currentUserIndex < moments.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentUserIndex + 1)
                        }
                    } else {
                        onStoriesFinished()
                    }
                },
                onNavigateToPreviousUser = {
                    if (currentUserIndex > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentUserIndex - 1)
                        }
                    }
                },
                onExit = onStoriesFinished,
                navController = navController,
                myID = myID,
                viewModel = viewModel,
                pageOffset = pagerState.currentPageOffsetFraction
            )
        }

        // Close button (Instagram-like X button)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(32.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
                .clickable { onStoriesFinished() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StoryUserPage(
    userStories: List<MomentModel>,
    isActive: Boolean,
    isPagerSettled: Boolean,
    onNavigateToNextUser: () -> Unit,
    onNavigateToPreviousUser: () -> Unit,
    onExit: () -> Unit,
    navController: NavHostController,
    myID: String,
    viewModel: MomentsViewModel,
    pageOffset: Float = 0f
) {
    // Find first unviewed story or start from beginning
    val initialStoryIndex = remember(userStories) {
        userStories.indexOfFirst { !it.viewed }.takeIf { it != -1 } ?: 0
    }

    var currentStoryIndex by remember(userStories) { mutableStateOf(initialStoryIndex) }

    // Only show story content when this page is active and fully visible
    if (isActive && isPagerSettled && currentStoryIndex < userStories.size) {
        val currentStory = userStories[currentStoryIndex]

        // Mark story as viewed only when page is settled
        LaunchedEffect(currentStoryIndex, isPagerSettled) {
            if (isPagerSettled) {
                viewModel.viewMoment(currentStory.momentID, currentStory.ownerID)
            }
        }

        StoryPage(
            story = currentStory,
            storyIndex = currentStoryIndex,
            totalStories = userStories.size,
            shouldStartTimer = isPagerSettled,
            onStoryComplete = {
                if (currentStoryIndex < userStories.size - 1) {
                    currentStoryIndex++
                } else {
                    onNavigateToNextUser()
                }
            },
            onTapLeft = {
                if (currentStoryIndex > 0) {
                    currentStoryIndex--
                } else {
                    onNavigateToPreviousUser()
                }
            },
            onTapRight = {
                if (currentStoryIndex < userStories.size - 1) {
                    currentStoryIndex++
                } else {
                    onNavigateToNextUser()
                }
            },
            onProfileTap = {
                if (currentStory.ownerID == myID) {
                    navController.navigate(NavRoutes.MY_PROFILE.route)
                } else {
                    navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${currentStory.ownerID}")
                }
            },
            onExit = onExit,
            navController = navController,
            myID = myID,
            viewModel = viewModel
        )
    } else {
        // Show placeholder when page is not active
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (userStories.isNotEmpty() && currentStoryIndex < userStories.size) {
                AsyncImage(
                    model = getUrl(userStories[currentStoryIndex].momentID),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder)
                )
            }
        }
    }
}

@Composable
private fun StoryPage(
    story: MomentModel,
    storyIndex: Int,
    totalStories: Int,
    shouldStartTimer: Boolean,
    onStoryComplete: () -> Unit,
    onTapLeft: () -> Unit,
    onTapRight: () -> Unit,
    onProfileTap: () -> Unit,
    onExit: () -> Unit,
    navController: NavHostController,
    myID: String,
    viewModel: MomentsViewModel
) {
    val deleteState by viewModel.deleteState.collectAsState()
    val likeState by viewModel.likeState.collectAsState()
    val unlikeState by viewModel.unLikeState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    // Progress animation
    val progress = remember { Animatable(0f) }

    val coroutineScope = rememberCoroutineScope()

    // Handle story progression - only start when shouldStartTimer is true
    LaunchedEffect(storyIndex, isPressed, shouldStartTimer) {
        progress.snapTo(0f) // Always reset progress for new story

        if (shouldStartTimer && !isPressed) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = STORY_DURATION_MS.toInt(),
                    easing = LinearEasing
                )
            )
            onStoryComplete()
        } else if (!shouldStartTimer) {
            // Stop animation if pager is scrolling
            progress.stop()
        }
    }

    // Resume animation when user stops pressing and timer should be active
    LaunchedEffect(isPressed, shouldStartTimer) {
        if (!isPressed && shouldStartTimer) {
            val remainingTime = (STORY_DURATION_MS * (1f - progress.value)).toLong()
            if (remainingTime > 0) {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = remainingTime.toInt(),
                        easing = LinearEasing
                    )
                )
                onStoryComplete()
            }
        } else {
            progress.stop()
        }
    }

    // Handle delete dialog
    if (showDeleteDialog) {
        CustomDialog(
            title = "Delete this moment?",
            action1 = "Cancel",
            action2 = "Delete",
            onCancel = { showDeleteDialog = false },
            onDelete = {
                showDeleteDialog = false
                viewModel.deleteMoment(story.momentID)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Story background image
        AsyncImage(
            model = getUrl(story.momentID),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(Res.drawable.ic_placeholder),
            error = painterResource(Res.drawable.ic_placeholder)
        )

        // Dark gradient overlays for better readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Progress indicators
        StoryProgressIndicators(
            currentIndex = storyIndex,
            totalStories = totalStories,
            progress = progress.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Touch areas for navigation and pause
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var wasLongPress = false

                    detectTapGestures(
                        onPress = {
                            if (shouldStartTimer) {
                                isPressed = true
                                wasLongPress = false

                                val longPressJob = coroutineScope.launch {
                                    delay(LONG_PRESS_THRESHOLD_MS)
                                    wasLongPress = true
                                }

                                tryAwaitRelease()
                                longPressJob.cancel()
                                isPressed = false
                            }
                        },
                        onTap = { offset ->
                            if (shouldStartTimer && !wasLongPress) {
                                // Only register as tap if it wasn't a long press
                                if (offset.x < size.width * 0.3f) {
                                    onTapLeft()
                                } else if (offset.x > size.width * 0.7f) {
                                    onTapRight()
                                }
                            }
                        }
                    )
                }
        ) {
            // Left tap area (30% of screen)
            Box(modifier = Modifier.weight(0.3f).fillMaxHeight())
            // Middle area (40% of screen) - no action
            Box(modifier = Modifier.weight(0.4f).fillMaxHeight())
            // Right tap area (30% of screen)
            Box(modifier = Modifier.weight(0.3f).fillMaxHeight())
        }

        // Story header with user info
        StoryHeader(
            story = story,
            onProfileTap = onProfileTap,
            onBackTap = onExit,
            modifier = Modifier.padding(16.dp)
        )

        // Story actions (like, delete, views)
        StoryActions(
            story = story,
            myID = myID,
            onLikeToggle = { isLiked ->
                if (isLiked) {
                    viewModel.like(story.momentID)
                } else {
                    viewModel.unLike(story.momentID)
                }
            },
            onDeleteClick = { showDeleteDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun StoryProgressIndicators(
    currentIndex: Int,
    totalStories: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(totalStories) { index ->
            LinearProgressIndicator(
                progress = when {
                    index < currentIndex -> 1f
                    index == currentIndex -> progress
                    else -> 0f
                },
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun StoryHeader(
    story: MomentModel,
    onProfileTap: () -> Unit,
    onBackTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, CircleShape)
                .clickable { onBackTap() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_back_black),
                contentDescription = "Back",
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User profile info
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { onProfileTap() }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = getUrl(story.profile.photoID),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                placeholder = painterResource(Res.drawable.ic_placeholder),
                error = painterResource(Res.drawable.ic_placeholder)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "${story.profile.firstName} ${story.profile.lastName}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = formatCreatedAt(story.createdAt),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun StoryActions(
    story: MomentModel,
    myID: String,
    onLikeToggle: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Like button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .clickable {
                        val isCurrentlyLiked = story.likes.contains(myID)
                        if (isCurrentlyLiked) {
                            story.likes.remove(myID)
                            story.liked = false
                            onLikeToggle(false)
                        } else {
                            story.likes.add(myID)
                            story.liked = true
                            onLikeToggle(true)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if (story.liked) {
                        painterResource(Res.drawable.ic_like)
                    } else {
                        painterResource(Res.drawable.ic_unlike_black)
                    },
                    contentDescription = "Like",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (story.liked) Color.Red else Color.White
                    )
                )
            }

            if (story.likes.isNotEmpty()) {
                Text(
                    text = story.likes.size.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Views counter
        if (story.views.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_view),
                    contentDescription = "Views",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.8f))
                )
                Text(
                    text = story.views.size.toString(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Delete button (only for own stories)
        if (myID == story.ownerID) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

// Main screen composable
@Composable
fun MomentsScreen(
    navController: NavHostController,
    viewModel: MomentsViewModel = koinViewModel()
) {
    val deleteState by viewModel.deleteState.collectAsState()

    // Handle delete completion
    LaunchedEffect(deleteState) {
        if (deleteState == "success") {
            navController.popBackStack()
        }
    }

    StoriesViewer(
        moments = viewModel.moments,
        initialUserIndex = viewModel.selectedIndex ?: 0,
        onStoriesFinished = {
            navController.popBackStack()
        },
        navController = navController,
        myID = viewModel.myID ?: "",
        viewModel = viewModel
    )
}