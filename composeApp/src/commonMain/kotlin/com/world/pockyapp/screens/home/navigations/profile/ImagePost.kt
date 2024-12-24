package com.world.pockyapp.screens.home.navigations.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant

@Composable
fun ImagePost(first: Int, postID: String) {

    Box(modifier = Modifier.size((convertPxToDp(first / 3)-8).dp)){
        AsyncImage(
            model = "http://${Constant.BASE_URL}:3000/api/v1/stream/media/$postID", // URL of the image
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }

}