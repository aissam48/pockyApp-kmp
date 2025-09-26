package com.world.pockyapp.screens.home.navigations.shots

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.ShotModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch

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
            Text(
                text = debugInfo,
                color = White,
                fontSize = 16.sp
            )
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
                //val videoUrl = getUrl(items[page].id)
                val videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"

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

    debugInfo = "Players: ${pageToPlayerMap.size}/5 | Page: ${pagerState.currentPage}/${items.size - 1}"

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

            if (playerItem != null) {
                Box(modifier = Modifier.fillMaxSize()) {
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
                                color = White,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            } else {
                // No player assigned to this page
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        color = White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Debug overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = debugInfo,
                color = White,
                fontSize = 12.sp,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.7f))
            )
        }
    }
}