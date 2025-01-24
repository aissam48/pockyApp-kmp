package com.world.pockyapp.screens.moment_screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.components.CustomDialog
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_delete
import pockyapp.composeapp.generated.resources.ic_like
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.ic_unlike_black
import pockyapp.composeapp.generated.resources.ic_view


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoriesViewer(
    users: List<ProfileModel>,
    initialUserIndex: Int = 0,
    onStoriesFinished: () -> Unit,
    navController: NavHostController,
    myID: String,
    viewModel: MomentsViewModel
) {
    var currentUserIndex by remember { mutableStateOf(initialUserIndex) }
    var currentStoryIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = initialUserIndex,
        pageCount = { users.size }
    )

    val coroutineScope = rememberCoroutineScope()
    val indexFirstUnvViewed = users[currentUserIndex].moments.indexOfFirst { !it.viewed }

    currentStoryIndex = if (indexFirstUnvViewed == -1) 0 else indexFirstUnvViewed

    LaunchedEffect(pagerState.currentPage) {
        println("pagerState.currentPage ${pagerState.currentPage}")
        currentUserIndex = pagerState.currentPage
        val currentMoment = users[currentUserIndex]
        val indexFirstUnvViewedL = currentMoment.moments.indexOfFirst { !it.viewed }
        currentStoryIndex = if (indexFirstUnvViewedL == -1) 0 else indexFirstUnvViewedL
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { userIndex ->
            val user = users[userIndex]
            StoryPage(
                userStories = user,
                currentStoryIndex = if (userIndex == currentUserIndex) {
                    val momentID = user.moments[currentStoryIndex].momentID
                    viewModel.viewMoment(momentID, user.id)
                    currentStoryIndex
                } else {
                    val index = user.moments.indexOfFirst { !it.viewed }
                    if (index == -1) {
                        0
                    } else {
                        index
                    }
                },
                onStoryFinished = {
                    if (currentStoryIndex < user.moments.size - 1) {
                        currentStoryIndex++
                    } else {
                        if (currentUserIndex < users.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(currentUserIndex + 1)
                            }
                        } else {
                            onStoriesFinished()
                        }
                    }
                },
                onTapLeft = {
                    if (currentStoryIndex > 0) {
                        currentStoryIndex--
                    } else if (currentUserIndex > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentUserIndex - 1)
                        }
                    }
                },
                onTapRight = {
                    if (currentStoryIndex < user.moments.size - 1) {
                        currentStoryIndex++
                    } else if (currentUserIndex < users.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentUserIndex + 1)
                        }
                    }
                },
                navController = navController,
                myID = myID,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun StoryPage(
    userStories: ProfileModel,
    currentStoryIndex: Int,
    onStoryFinished: () -> Unit,
    onTapLeft: () -> Unit,
    onTapRight: () -> Unit,
    navController: NavHostController,
    myID: String,
    viewModel: MomentsViewModel
) {
    val deleteState by viewModel.deleteState.collectAsState()
    val likeState by viewModel.likeState.collectAsState()
    val unlikeState by viewModel.unLikeState.collectAsState()
    val scope = rememberCoroutineScope() // Use Compose's coroutine scope

    if (currentStoryIndex > userStories.moments.size - 1) {
        navController.popBackStack()
        return
    }
    val story = userStories.moments[currentStoryIndex]
    var progressValue by remember { mutableStateOf(0f) }
    val progressAnimation = remember {
        Animatable(0f)
    }

    var isHolding by remember { mutableStateOf(false) }

    LaunchedEffect(currentStoryIndex) {
        // Reset progress only when the story index changes
        progressAnimation.snapTo(0f) // Start at 0 for the new story
        progressValue = 0f // Reset the progress value for the new story

        if (!isHolding) {
            // Animate to the end if not holding
            progressAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 10000,
                    easing = LinearEasing
                )
            )
            onStoryFinished() // Trigger when animation completes
        }
    }

    LaunchedEffect(isHolding) {
        if (!isHolding) {
            // Resume the animation when holding is false
            progressAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (10000 * (1f - progressValue)).toInt(),
                    easing = LinearEasing
                )
            )
            onStoryFinished() // Trigger when animation completes
        }
    }

    LaunchedEffect(progressAnimation.value) {
        if (!isHolding) {
            // Continuously update progressValue only when not holding
            progressValue = progressAnimation.value
            //println("Progress: ${progressAnimation.value}")
        }
    }


    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CustomDialog(
            title = "Are you sure you want to delete this moment?",
            action1 = "Cancel",
            action2 = "Delete",
            onCancel = { showDialog = false },
            onDelete = {
                showDialog = false
                viewModel.deleteMoment(story.momentID)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Story Image
        AsyncImage(
            model = getUrl(story.momentID),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Progress indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            userStories.moments.forEachIndexed { index, _ ->
                LinearProgressIndicator(
                    progress = if (index < currentStoryIndex) 1f
                    else if (index == currentStoryIndex) progressValue
                    else 0f,
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp),
                    color = Color.White.copy(alpha = 0.4f),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()

                .pointerInput(Unit) {
                    awaitEachGesture {

                        var clickAble = true
                        val down = awaitFirstDown() // User touches the screen
                        isHolding = true

                        // Detect if the user holds for a certain duration
                        val holdJob = scope.launch {
                            delay(500) // Threshold for "long press" (e.g., 500ms)
                            if (isHolding) {
                                clickAble = false
                                println("Long press detected!")
                            }
                        }

                        val up = waitForUpOrCancellation() // Wait for user to lift their finger or cancel
                        holdJob.cancel() // Stop the hold detection once the finger is lifted or gesture canceled

                        if (up != null) {
                            isHolding = false
                            println("Finger removed after hold!") // This happens if the user held for a while

                            if (clickAble) {
                                if (down.position.x < size.width / 2) {
                                    onTapLeft()
                                } else {
                                    onTapRight()
                                }
                            }
                        }
                    }
                }

        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
        // User info
        Row(
            modifier = Modifier
                .padding(8.dp)
                .padding(top = 25.dp)
                .clickable {
                    if (userStories.id == myID) {
                        navController.navigate(NavRoutes.MY_PROFILE.route)
                    } else {
                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${userStories.id}")
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.size(23.dp).clickable {
                    navController.popBackStack()
                }.background(color = Color.White, shape = CircleShape),
                painter = painterResource(Res.drawable.ic_back_black),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(15.dp))
            Row {
                AsyncImage(
                    model = getUrl(userStories.photoID),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${userStories.firstName} ${userStories.lastName}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatCreatedAt(story.createdAt),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                colorFilter = ColorFilter.tint(color = Color.White),
                painter = if (story.liked)
                    painterResource(Res.drawable.ic_like)
                else painterResource(
                    Res.drawable.ic_unlike_black
                ),
                modifier = Modifier.size(40.dp).clickable {
                    if (story.likes.contains(myID)) {
                        story.likes.remove(myID)
                        viewModel.unLike(story.momentID)
                        story.liked = false
                    } else {
                        story.likes.add(myID)
                        viewModel.like(story.momentID)
                        story.liked = true
                    }
                },
                contentDescription = null
            )

            Spacer(modifier = Modifier.size(5.dp))

            androidx.compose.material.Text(
                text = story.likes.size.toString(),
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.size(20.dp))

            if (myID == story.ownerID) {
                Image(
                    painter = painterResource(Res.drawable.ic_delete),
                    modifier = Modifier.size(35.dp).clickable {
                        //viewModel.deleteMoment(story.postID)
                        showDialog = true
                    },
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${story.views.size}",
                    color = Color.Yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(5.dp))
                Image(
                    painter = painterResource(Res.drawable.ic_view),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )

            }
            Spacer(modifier = Modifier.size(50.dp))
        }
    }
}

// Example usage
@Composable
fun MomentsScreen(
    navController: NavHostController,
    moments: List<ProfileModel>,
    index: String?,
    myID: String?,
    viewModel: MomentsViewModel = koinViewModel()
) {

    val deleteMoment by viewModel.deleteState.collectAsState()
    LaunchedEffect(deleteMoment){
        if (deleteMoment == "success")
        navController.popBackStack()
    }

    StoriesViewer(
        users = moments,
        onStoriesFinished = {
            navController.popBackStack()
        },
        initialUserIndex = index?.toIntOrNull() ?: 0,
        navController = navController,
        myID = myID ?: "",
        viewModel = viewModel
    )
}