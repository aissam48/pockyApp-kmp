package com.world.pockyapp.screens.moment_preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter

@Composable
expect fun MomentPreview(navController: NavHostController, path: String)