package com.world.pockyapp.screens.challengeDetails

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController

@Composable
actual fun ChallengeDetailsVideoComponent(
    mediaUrl: String,
    navController: NavController
) {

    println("ChallengeDetailsVideoComponent $mediaUrl")
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    // Create and remember ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Set media item
            setMediaItem(MediaItem.fromUri(mediaUrl))
            // Prepare the player
            prepare()
            // Add listener for play/pause state changes
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {

                }
            })
        }
    }

    // Update play/pause state when isPlaying changes
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }


    // Release player when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier.background(Color.Black).fillMaxSize()
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}