package com.world.pockyapp.screens.show_moment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant
import com.world.pockyapp.network.models.model.PostModel
import kotlinx.coroutines.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_close_white

data class Story(
    val id: String,
    val userName: String,
    val userAvatar: DrawableResource, // Resource ID for avatar
    val storyImage: DrawableResource, // Resource ID for story image
    val duration: Long = 5 // Default story duration in seconds
)


@Composable
fun StoryProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp),
        backgroundColor = Color.Gray.copy(alpha = 0.3f),
        color = Color.White
    )
}


// Single story view
@Composable
fun StoryView(
    story: PostModel,
    onStoryCompleted: () -> Unit,
    onNextStory: () -> Unit,
    onPreviousStory: () -> Unit,
    back: Boolean
) {
    var progress by remember { mutableStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }

    // Story progress timer
    LaunchedEffect(key1 = story.postID, key2 = isHolding) {
        if (!isHolding) {
            while (progress < 2f) {
                delay(50) // Update every 50ms
                progress += 0.05f / (4)

                if (progress >= 2f) {
                    onStoryCompleted()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        awaitRelease()
                        isHolding = false

                        println("detectTapGestures -> onPress")
                    },
                    onDoubleTap = { /* Future feature like sharing */
                        println("detectTapGestures -> onDoubleTap")
                    },
                    onLongPress = { /* Future feature like pause */
                        println("detectTapGestures -> onLongPress")
                    }
                )
            }
    ) {
        // Story Image
        AsyncImage(
            model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${story.postID}",
            contentDescription = "Story Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Progress Bars
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // User Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                AsyncImage(
                    model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/${story.postID}",
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = story.createdAt,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StoryProgressBar(
                    progress = progress,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Stories Container
@Composable
fun StoriesContainer(stories: List<PostModel>, navController: NavHostController, back: Boolean) {
    var currentStoryIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Current Story View
        key(stories[currentStoryIndex].postID) {
            StoryView(
                story = stories[currentStoryIndex],
                onStoryCompleted = {
                    if (currentStoryIndex < stories.size - 1) {
                        currentStoryIndex++
                    }
                },
                onNextStory = {
                    if (currentStoryIndex < stories.size - 1) {
                        currentStoryIndex++
                    }
                },
                onPreviousStory = {
                    if (currentStoryIndex > 0) {
                        currentStoryIndex--
                    }
                }, back
            )
        }

        Image(
            painter = painterResource(Res.drawable.ic_close_white),
            contentDescription = null,
            modifier = Modifier.padding(10.dp).size(40.dp).clickable {
                navController.popBackStack()
            }.align(Alignment.TopEnd)
        )
    }
}

// Example Usage in a Composable
@Composable
fun ShowMoments(navController: NavHostController, moments: List<PostModel>, back: Boolean = false) {
    println("momentsSize" + moments.size)
    val sampleStories = moments

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        StoriesContainer(stories = sampleStories, navController, back)
    }
}