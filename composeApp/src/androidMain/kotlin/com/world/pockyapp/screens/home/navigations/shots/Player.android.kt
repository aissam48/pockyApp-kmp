package com.world.pockyapp.screens.home.navigations.shots

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.ShotModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import org.jetbrains.compose.resources.painterResource
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.icon_comment
import pockyapp.composeapp.generated.resources.icon_like
import pockyapp.composeapp.generated.resources.icon_share
import pockyapp.composeapp.generated.resources.icon_unlike
import kotlin.random.Random

data class VideoPlayerItem(
    val player: ExoPlayer,
    var pageIndex: Int,
    var isReady: Boolean = false,
    var videoUrl: String = ""
)

@OptIn(UnstableApi::class)
@Composable
actual fun Player() {
    val viewModel: ShotsViewModel = koinViewModel()
    var items by remember { mutableStateOf<List<ShotModel>>(emptyList()) }
    var debugInfo by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(false) }

    val shotsState = viewModel.getShotsState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // LIMITED PLAYER POOL - Only 5 players maximum (like TikTok)
    val playerPool = remember {
        Array(5) { index ->
            VideoPlayerItem(
                player = ExoPlayer.Builder(context)
                    .setLoadControl(
                        DefaultLoadControl.Builder()
                            .setBufferDurationsMs(500, 3000, 200, 500)
                            .build()
                    )
                    .build()
                    .apply {
                        repeatMode = Player.REPEAT_MODE_ONE
                        volume = 1f
                        playWhenReady = false
                    },
                pageIndex = -1 // Not assigned yet
            )
        }
    }

    // Map page index to player from the pool
    var pageToPlayerMap by remember { mutableStateOf<Map<Int, VideoPlayerItem>>(emptyMap()) }

    // Load initial data
    LaunchedEffect(Unit) {
        debugInfo = "Calling viewModel.getShots()"
        viewModel.getShots()
    }

    // Handle API response
    LaunchedEffect(shotsState.value) {
        when (val state = shotsState.value) {
            is ResponseState.Success -> {
                debugInfo = "SUCCESS: Got ${state.data.size} videos"
                items = items + state.data
                isLoading = false
                println("SUCCESS: Total items now: ${items.size}")
            }

            is ResponseState.Loading -> {
                debugInfo = "LOADING..."
                isLoading = true
            }

            is ResponseState.Error -> {
                debugInfo = "ERROR: ${state.error.message}"
                isLoading = false
            }

            else -> {
                debugInfo = "IDLE"
            }
        }
    }

    // Show debug info if no items
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFDFC46B),
                    strokeWidth = 3.dp
                )
                Text(
                    text = debugInfo,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { items.size })

    // Load more videos when approaching end
    LaunchedEffect(pagerState.currentPage, items.size) {
        if (pagerState.currentPage >= items.size - 3 && !isLoading) {
            viewModel.getShots()
        }
    }

    // Smart player assignment and recycling
    fun assignPlayersToPages(centerPage: Int) {
        coroutineScope.launch {
            // Pages we want to have players for (current ±2)
            val targetPages = listOf(
                centerPage - 2,
                centerPage - 1,
                centerPage,
                centerPage + 1,
                centerPage + 2
            ).filter { it in 0 until items.size }

            println("🎯 Target pages: $targetPages for center: $centerPage")

            val newMapping = mutableMapOf<Int, VideoPlayerItem>()

            // First, keep existing players that are still needed
            targetPages.forEach { page ->
                val existingPlayer = pageToPlayerMap[page]
                if (existingPlayer != null) {
                    newMapping[page] = existingPlayer
                    println("♻️ Keeping existing player for page $page")
                }
            }

            // Find pages that need new players
            val pagesNeedingPlayers = targetPages.filter { !newMapping.containsKey(it) }

            // Find available players (not currently assigned to target pages)
            val availablePlayers = playerPool.filter { playerItem ->
                !newMapping.values.contains(playerItem)
            }

            // Assign available players to pages that need them
            pagesNeedingPlayers.zip(availablePlayers).forEach { (page, playerItem) ->
                val videoUrl =
                    "https://nearvibe.fra1.digitaloceanspaces.com/95836975-a6a4-4b1a-8481-a41e87e4326c"

                println("🔄 Assigning player to page $page: $videoUrl")

                try {
                    // Stop and clear previous media
                    playerItem.player.stop()
                    playerItem.player.clearMediaItems()

                    // Set new media
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    playerItem.player.setMediaItem(mediaItem)
                    playerItem.player.prepare()

                    // Update player item
                    playerItem.pageIndex = page
                    playerItem.isReady = false
                    playerItem.videoUrl = videoUrl

                    // Add ready listener
                    val listener = object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_READY) {
                                playerItem.isReady = true
                                playerItem.player.removeListener(this)
                                println("✅ Player ready for page $page")

                                // Auto-play if this is the current page
                                if (page == centerPage) {
                                    playerItem.player.playWhenReady = true
                                    println("▶️ Auto-playing current page $page")
                                }
                            }
                        }

                        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                            println("❌ Player error for page $page: ${error.message}")
                        }
                    }
                    playerItem.player.addListener(listener)

                    newMapping[page] = playerItem

                } catch (e: Exception) {
                    println("❌ Failed to assign player to page $page: ${e.message}")
                }
            }

            // Update the mapping
            pageToPlayerMap = newMapping

            println("📊 Player assignments: ${newMapping.keys.sorted()}")
        }
    }

    // Initialize players when items are loaded
    LaunchedEffect(items.size) {
        if (items.isNotEmpty() && pageToPlayerMap.isEmpty()) {
            assignPlayersToPages(0)
        }
    }

    // Handle page changes
    LaunchedEffect(pagerState.currentPage) {
        val currentPage = pagerState.currentPage
        println("📄 Page changed to: $currentPage")

        // Stop all players
        playerPool.forEach { playerItem ->
            playerItem.player.playWhenReady = false
        }

        // Play current page if ready
        pageToPlayerMap[currentPage]?.let { playerItem ->
            if (playerItem.isReady) {
                playerItem.player.playWhenReady = true
                println("▶️ Playing page $currentPage (ready)")
            } else {
                println("⏳ Page $currentPage not ready yet")
            }
        }

        // Reassign players for new position
        assignPlayersToPages(currentPage)
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            playerPool.forEach { playerItem ->
                try {
                    playerItem.player.stop()
                    playerItem.player.release()
                } catch (e: Exception) {
                    println("Error releasing player: ${e.message}")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val playerItem = pageToPlayerMap[page]
            val shot = items.getOrNull(page)

            Box(modifier = Modifier.fillMaxSize()) {
                // Video Player
                if (playerItem != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = playerItem.player
                                useController = false
                                setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                            }
                        },
                        update = { playerView ->
                            if (playerView.player != playerItem.player) {
                                playerView.player = playerItem.player
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Show loading if not ready
                    if (!playerItem.isReady) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = Color(0xFFDFC46B),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                } else {
                    // No player assigned to this page
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFDFC46B),
                            strokeWidth = 3.dp
                        )
                    }
                }

                // Social UI Overlay
                if (shot != null) {
                    SocialOverlay(
                        shot = shot,
                        modifier = Modifier.fillMaxSize(),
                        onLikeClick = { /* Handle like */ },
                        onCommentClick = { /* Handle comment */ },
                        onShareClick = { /* Handle share */ },
                        onFollowClick = { /* Handle follow */ },
                        onProfileClick = { /* Handle profile click */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SocialOverlay(
    shot: ShotModel,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onFollowClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Generate random engagement numbers for demo
    val likes = remember { Random.nextInt(100, 50000) }
    val comments = remember { Random.nextInt(10, 1000) }
    val views = remember { Random.nextInt(1000, 100000) }
    val isLiked = remember { mutableStateOf(false) }
    val isFollowing = remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Right side action buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture with Follow Button
            Box {
                AsyncImage(
                    model = getUrl(shot.profile?.photoID ?: ""),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onProfileClick() },
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder)
                )

            }

            // Like Button
            SocialActionButton(
                painter = if (isLiked.value) painterResource(Res.drawable.icon_like) else painterResource(
                    Res.drawable.icon_unlike
                ),
                count = if (isLiked.value) likes + 1 else likes,
                tint = if (isLiked.value) Color.Red else Color.White,
                onClick = {
                    isLiked.value = !isLiked.value
                    onLikeClick()
                }
            )

            // Comment Button
            SocialActionButton(
                painter = painterResource(Res.drawable.icon_comment),
                count = comments,
                tint = Color.White,
                onClick = onCommentClick
            )

            // Share Button
            SocialActionButton(
                painter = painterResource(Res.drawable.icon_share),
                count = null,
                tint = Color.White,
                onClick = onShareClick
            )

            // Views indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Views",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = formatCount(views),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom content info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth(0.7f)
        ) {
            // Username
            Text(
                text = "Aissam elboudi",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Caption/Description
            Text(
                text = "Amazing video! Check this out 🔥",
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Hashtags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                listOf("#fyp", "#viral", "#trending").forEach { hashtag ->
                    Text(
                        text = hashtag,
                        color = Color(0xFFDFC46B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Music/Audio info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Music",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Original Audio",
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Top gradient overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )
    }
}

@Composable
fun SocialActionButton(
    painter: Painter,
    count: Int?,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }

        if (count != null) {
            Text(
                text = formatCount(count),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${(count / 1_000_000f).let { "%.1f".format(it) }}M"
        count >= 1_000 -> "${(count / 1_000f).let { "%.1f".format(it) }}K"
        else -> count.toString()
    }
}