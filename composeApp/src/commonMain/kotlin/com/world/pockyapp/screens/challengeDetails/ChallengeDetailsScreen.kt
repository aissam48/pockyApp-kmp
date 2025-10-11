package com.world.pockyapp.screens.challengeDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.network.models.model.ChallengeModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder

@Composable
fun ChallengeDetailsScreen(challengeId: String, navController: NavHostController) {

    val viewModel: ChallengeDetailsViewModel = koinViewModel()

    val challengeDetailsState = viewModel.challengeDetailsState.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.getChallengeDetails(challengeId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {


            when (val state = challengeDetailsState.value) {
                is ResponseState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
                is ResponseState.Success -> {

                    item {
                        ChallengeVideoSection(
                            challenge = state.data,
                            isPlaying = isPlaying,
                            onPlayStateChanged = { isPlaying = it },
                            onBackClick = { navController.popBackStack() },
                            navController = navController
                        )
                    }
                    item {
                        ChallengeInfoSection(
                            challenge = state.data
                        ){

                        }
                    }


                }
                is ResponseState.Error -> {

                }
                else -> {

                }
            }

        }
    }
}

@Composable
fun ChallengeVideoSection(
    challenge: ChallengeModel,
    isPlaying: Boolean,
    onPlayStateChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f) // TikTok-style aspect ratio
    ) {
        // Video Player
        /*VideoPlayerComponent(
            videoUrl = challenge.mediaUrl,
            isPlaying = isPlaying,
            onPlayStateChanged = onPlayStateChanged,
            modifier = Modifier.fillMaxSize()
        )*/
        println("ChallengeDetailsVideoComponent 1")

        ChallengeDetailsVideoComponent(mediaUrl = challenge.mediaUrl, navController = navController)
        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        )

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(10.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Play/Pause Overlay
        AnimatedVisibility(
            visible = !isPlaying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .clickable { onPlayStateChanged(true) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Challenge Title Overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(30.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = challenge.difficulty,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Text(
                        text = challenge.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = challenge.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VideoPlayerComponent(
    videoUrl: String,
    isPlaying: Boolean,
    onPlayStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // For demo purposes, show a placeholder
    // In real implementation, use ExoPlayer here
    Box(
        modifier = modifier
            .background(Color.Gray)
            .clickable { onPlayStateChanged(!isPlaying) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Video",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Challenge Video",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tap to ${if (isPlaying) "pause" else "play"}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ChallengeInfoSection(
    challenge: ChallengeModel,
    onOwnerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Owner Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOwnerClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box {
                    AsyncImage(
                        model = challenge.profile.photoUrl,
                        contentDescription = "Owner profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(Res.drawable.ic_placeholder),
                        error = painterResource(Res.drawable.ic_placeholder)
                    )

                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${challenge.profile.firstName} ${challenge.profile.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Challenge Creator • ${challenge.createdAt}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "View profile",
                    tint = Color.Black
                )
            }

            Divider(color = Color.Red)

            // Description
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Blue
                )
                Text(
                    text = challenge.description,
                    fontSize = 14.sp,
                    color = Color.Blue,
                    lineHeight = 20.sp
                )
            }

            // Rules
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Rules & Requirements",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Blue
                )
                Text(
                    text = challenge.rules,
                    fontSize = 14.sp,
                    color = Color.Blue,
                    lineHeight = 20.sp
                )
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Face,
                    count = challenge.participants.size,
                    label = "Participants"
                )
                StatItem(
                    icon = Icons.Default.Favorite,
                    count = challenge.participants.size,
                    label = "Likes"
                )
                StatItem(
                    icon = Icons.Default.Share,
                    count = 245,
                    label = "Shares"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    count: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Magenta,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = count.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Green
        )
    }
}

