package com.world.pockyapp.screens.challengeDetails

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder

// Design System
object ChallengeDetailsTheme {
    val SpacingXSmall = 4.dp
    val SpacingSmall = 8.dp
    val SpacingMedium = 16.dp
    val SpacingLarge = 24.dp
    val SpacingXLarge = 32.dp

    val Primary = Color(0xFFDFC46B)
    val Secondary = Color(0xFFF4D03F)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF8F9FA)
    val OnSurface = Color(0xFF1C1C1E)
    val OnSurfaceVariant = Color(0xFF6B7280)
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)

    val GradientOverlay = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
        startY = 0f,
        endY = 1000f
    )
}

// Data Classes
data class ChallengeModel(
    val id: String,
    val title: String,
    val description: String,
    val rules: String,
    val videoUrl: String,
    val category: String,
    val difficulty: String,
    val difficultyColor: Color,
    val owner: UserProfile,
    val createdAt: String,
    val participantsCount: Int,
    val likesCount: Int,
    val isLiked: Boolean,
    val isParticipated: Boolean
)

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
    val isVerified: Boolean = false
)

data class ParticipantModel(
    val id: String,
    val user: UserProfile,
    val videoUrl: String,
    val createdAt: String,
    val likesCount: Int,
    val isLiked: Boolean
)

@Composable
actual fun ChallengeDetailsScreen(
    challengeId: String,
    navController: NavHostController
) {
    // Mock data - replace with actual data loading
    val challenge = remember {
        ChallengeModel(
            id = challengeId,
            title = "30-Day Push-Up Challenge",
            description = "Build upper body strength with this progressive push-up challenge. Start with 10 push-ups and work your way up to 100!",
            rules = "• Perform push-ups with proper form\n• Take progress photos\n• Rest days are important\n• Stay consistent",
            videoUrl = "https://example.com/challenge.mp4",
            category = "Fitness",
            difficulty = "Medium",
            difficultyColor = Color(0xFFF59E0B),
            owner = UserProfile("1", "Sarah", "Johnson", "https://example.com/sarah.jpg", true),
            createdAt = "2 days ago",
            participantsCount = 1250,
            likesCount = 4820,
            isLiked = false,
            isParticipated = false
        )
    }

    val participants = remember {
        List(20) { index ->
            ParticipantModel(
                id = "participant_$index",
                user = UserProfile(
                    id = "user_$index",
                    firstName = listOf("Alex", "Emma", "John", "Lisa", "Mike", "Anna")[index % 6],
                    lastName = listOf("Smith", "Brown", "Wilson", "Davis", "Miller", "Taylor")[index % 6],
                    photoUrl = "https://example.com/user_$index.jpg"
                ),
                videoUrl = "https://example.com/participant_$index.mp4",
                createdAt = "${index + 1} hours ago",
                likesCount = (10..500).random(),
                isLiked = (index % 3) == 0
            )
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var showAllParticipants by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Video Section
            item {
                ChallengeVideoSection(
                    challenge = challenge,
                    isPlaying = isPlaying,
                    onPlayStateChanged = { isPlaying = it },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Challenge Info Section
            item {
                ChallengeInfoSection(
                    challenge = challenge,
                    onOwnerClick = {
                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${challenge.owner.id}")
                    }
                )
            }

            // Action Buttons
            item {
                ActionButtonsSection(
                    challenge = challenge,
                    onJoinClick = { /* Handle join challenge */ },
                    onLikeClick = { /* Handle like */ },
                    onShareClick = { /* Handle share */ }
                )
            }

            // Participants Section
            item {
                ParticipantsSection(
                    participants = if (showAllParticipants) participants else participants.take(6),
                    totalParticipants = challenge.participantsCount,
                    showAll = showAllParticipants,
                    onToggleShowAll = { showAllParticipants = !showAllParticipants },
                    onParticipantClick = { participant ->
                        // Navigate to participant's video
                    }
                )
            }
        }
    }
}

@Composable
fun ChallengeVideoSection(
    challenge: ChallengeModel,
    isPlaying: Boolean,
    onPlayStateChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f) // TikTok-style aspect ratio
    ) {
        // Video Player
        VideoPlayerComponent(
            videoUrl = challenge.videoUrl,
            isPlaying = isPlaying,
            onPlayStateChanged = onPlayStateChanged,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChallengeDetailsTheme.GradientOverlay)
        )

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(ChallengeDetailsTheme.SpacingMedium)
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
                .padding(ChallengeDetailsTheme.SpacingLarge)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingSmall)
                ) {
                    Text(
                        text = challenge.difficulty,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                challenge.difficultyColor,
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

                Spacer(modifier = Modifier.height(ChallengeDetailsTheme.SpacingSmall))

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
    val context = LocalContext.current

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
            verticalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingMedium)
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
            .padding(ChallengeDetailsTheme.SpacingMedium),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ChallengeDetailsTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(ChallengeDetailsTheme.SpacingLarge),
            verticalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingMedium)
        ) {
            // Owner Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOwnerClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingMedium)
            ) {
                Box {
                    AsyncImage(
                        model = challenge.owner.photoUrl,
                        contentDescription = "Owner profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(Res.drawable.ic_placeholder),
                        error = painterResource(Res.drawable.ic_placeholder)
                    )

                    if (challenge.owner.isVerified) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .background(ChallengeDetailsTheme.Primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${challenge.owner.firstName} ${challenge.owner.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChallengeDetailsTheme.OnSurface
                    )
                    Text(
                        text = "Challenge Creator • ${challenge.createdAt}",
                        fontSize = 14.sp,
                        color = ChallengeDetailsTheme.OnSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "View profile",
                    tint = ChallengeDetailsTheme.OnSurfaceVariant
                )
            }

            Divider(color = ChallengeDetailsTheme.SurfaceVariant)

            // Description
            Column(
                verticalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingSmall)
            ) {
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChallengeDetailsTheme.OnSurface
                )
                Text(
                    text = challenge.description,
                    fontSize = 14.sp,
                    color = ChallengeDetailsTheme.OnSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            // Rules
            Column(
                verticalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingSmall)
            ) {
                Text(
                    text = "Rules & Requirements",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChallengeDetailsTheme.OnSurface
                )
                Text(
                    text = challenge.rules,
                    fontSize = 14.sp,
                    color = ChallengeDetailsTheme.OnSurfaceVariant,
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
                    count = challenge.participantsCount,
                    label = "Participants"
                )
                StatItem(
                    icon = Icons.Default.Favorite,
                    count = challenge.likesCount,
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
        verticalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingXSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ChallengeDetailsTheme.Primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = formatCount(count),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ChallengeDetailsTheme.OnSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = ChallengeDetailsTheme.OnSurfaceVariant
        )
    }
}

@Composable
fun ActionButtonsSection(
    challenge: ChallengeModel,
    onJoinClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ChallengeDetailsTheme.SpacingMedium),
        horizontalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingMedium)
    ) {
        // Join Challenge Button
        Button(
            onClick = onJoinClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChallengeDetailsTheme.Primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = if (challenge.isParticipated) Icons.Default.CheckCircle else Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(ChallengeDetailsTheme.SpacingSmall))
            Text(
                text = if (challenge.isParticipated) "Joined" else "Join Challenge",
                fontWeight = FontWeight.SemiBold
            )
        }

        // Like Button
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier
                .background(
                    if (challenge.isLiked) Color.Red.copy(alpha = 0.1f) else ChallengeDetailsTheme.SurfaceVariant,
                    CircleShape
                )
        ) {
            Icon(
                imageVector = if (challenge.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = if (challenge.isLiked) Color.Red else ChallengeDetailsTheme.OnSurfaceVariant
            )
        }

        // Share Button
        IconButton(
            onClick = onShareClick,
            modifier = Modifier.background(ChallengeDetailsTheme.SurfaceVariant, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = ChallengeDetailsTheme.OnSurfaceVariant
            )
        }
    }
}

@Composable
fun ParticipantsSection(
    participants: List<ParticipantModel>,
    totalParticipants: Int,
    showAll: Boolean,
    onToggleShowAll: () -> Unit,
    onParticipantClick: (ParticipantModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ChallengeDetailsTheme.SpacingMedium),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ChallengeDetailsTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(ChallengeDetailsTheme.SpacingLarge)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Participants",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChallengeDetailsTheme.OnSurface
                    )
                    Text(
                        text = "${formatCount(totalParticipants)} people joined",
                        fontSize = 14.sp,
                        color = ChallengeDetailsTheme.OnSurfaceVariant
                    )
                }

                TextButton(onClick = onToggleShowAll) {
                    Text(
                        text = if (showAll) "Show Less" else "View All",
                        color = ChallengeDetailsTheme.Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(ChallengeDetailsTheme.SpacingMedium))

            // Participants Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingSmall),
                verticalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingSmall),
                modifier = Modifier.height(if (showAll) 600.dp else 240.dp)
            ) {
                items(participants) { participant ->
                    ParticipantCard(
                        participant = participant,
                        onClick = { onParticipantClick(participant) }
                    )
                }
            }
        }
    }
}

@Composable
fun ParticipantCard(
    participant: ParticipantModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(3f / 4f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Video Thumbnail (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 200f
                        )
                    )
            )

            // User Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(ChallengeDetailsTheme.SpacingSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingXSmall)
                ) {
                    AsyncImage(
                        model = participant.user.photoUrl,
                        contentDescription = "User avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(Res.drawable.ic_placeholder),
                        error = painterResource(Res.drawable.ic_placeholder)
                    )

                    Text(
                        text = participant.user.firstName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ChallengeDetailsTheme.SpacingXSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Likes",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = formatCount(participant.likesCount),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Helper Functions
fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}