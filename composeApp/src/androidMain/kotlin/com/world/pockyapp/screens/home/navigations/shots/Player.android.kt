package com.world.pockyapp.screens.home.navigations.shots

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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.ShotModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun Player() {
    val viewModel: ShotsViewModel = koinViewModel()
    var items by remember { mutableStateOf<List<ShotModel>>(emptyList()) }

    val shotsState = viewModel.getShotsState.collectAsState()
    println("itemsdqdeqsdeq-----> 3")

    // first load
    LaunchedEffect(Unit) {
        viewModel.getShots()
    }

    // update items when success
    LaunchedEffect(shotsState.value) {
        when (val state = shotsState.value) {
            is ResponseState.Success -> {
                println("itemsdqdeqsdeq-----> ${state.data}")
                items = items + state.data
            }
            else -> Unit
        }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { items.size }
    )

    // pagination trigger
    LaunchedEffect(pagerState.currentPage, items.size) {
        if (pagerState.currentPage >= items.size - 2 && items.isNotEmpty()) {
            viewModel.getShots()
        }
    }

    val context = LocalContext.current

    // Only 3 players: previous, current, next
    val players = remember {
        Array(3) { ExoPlayer.Builder(context).build() }
    }

    // Helper: bind url to player
    fun ExoPlayer.bindVideo(url: String, autoPlay: Boolean) {

        println("ozqjksdfnvsljml  3  $autoPlay $url")
        setMediaItem(MediaItem.fromUri(url))
        prepare()
        playWhenReady = autoPlay
    }

    // Map of page → player
    var playerMap by remember { mutableStateOf<Map<Int, ExoPlayer>>(emptyMap()) }

    // rebind players when page changes
    LaunchedEffect(pagerState.currentPage, items) {
        if (items.isEmpty()) return@LaunchedEffect

        val current = pagerState.currentPage
        val newMap = mutableMapOf<Int, ExoPlayer>()

        val indices = listOf(current - 1, current, current + 1)
            .filter { it in items.indices }

        indices.forEachIndexed { idx, page ->
            val player = players[idx]
            player.clearMediaItems()
            player.bindVideo(getUrl(items[page].id), autoPlay = page == current)
            newMap[page] = player
        }

        playerMap = newMap
    }

    // cleanup
    DisposableEffect(Unit) {
        onDispose { players.forEach { it.release() } }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (items.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                println("ozqjksdfnvsljml  2 pager $page")
                val player = playerMap[page]
                if (player != null) {
                    println("ozqjksdfnvsljml  1")
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                this.player = player
                                useController = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}