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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_view


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoriesViewer(
    users: List<ProfileModel>,
    initialUserIndex: Int = 0,
    onStoriesFinished: () -> Unit,
    navController: NavHostController,
    back: Boolean,
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
                    val momentID = user.moments[currentStoryIndex].postID
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
                back = back,
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
    back: Boolean,
    viewModel: MomentsViewModel
) {

    if (currentStoryIndex > userStories.moments.size-1){
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
        progressAnimation.snapTo(0f)
        progressValue = 0f
        progressAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 5000,
                easing = LinearEasing
            )
        )
        onStoryFinished()
    }

    LaunchedEffect(progressAnimation.value) {
        if (!isHolding) {
            progressValue = progressAnimation.value
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Story Image
        AsyncImage(
            model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${story.postID}",
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

        // Touch controls
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {

                    awaitEachGesture {
                        // Wait for down event
                        val down = awaitFirstDown()
                        isHolding = true
                        CoroutineScope(Dispatchers.Main).launch {
                            progressAnimation.stop()
                        }
                        val offset = down.position
                        val up = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                            waitForUpOrCancellation()
                        }

                        if (up != null) {
                            isHolding = false
                            println("Clicked!")
                            if (offset.x < size.width / 2) {
                                onTapLeft()
                            } else {
                                onTapRight()
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
                .padding(top = 48.dp)
                .clickable {
                    if (back) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${userStories.id}")
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.size(23.dp).clickable {
                    navController.popBackStack()
                },
                painter = painterResource(Res.drawable.ic_back_black),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(15.dp))
            Row {
                AsyncImage(
                    model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${userStories.photoID}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${userStories.firstName} ${userStories.lastName}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(15.dp))
            Text(text = "${story.views.size}", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(5.dp))
            Image(painter = painterResource(Res.drawable.ic_view), contentDescription = null, modifier = Modifier.size(25.dp))

        }
    }
}

// Example usage
@Composable
fun MomentsScreen(
    navController: NavHostController,
    moments: List<ProfileModel>,
    index: String?,
    back: Boolean,
    viewModel: MomentsViewModel = koinViewModel()
) {
    val sampleUsers = moments

    StoriesViewer(
        users = sampleUsers,
        onStoriesFinished = {
            navController.popBackStack()
        },
        initialUserIndex = index?.toIntOrNull() ?: 0,
        navController = navController,
        back = back,
        viewModel = viewModel
    )
}