package com.world.pockyapp.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl

@Composable
fun ImagePost(first: Int, postID: String, clicked: () -> Unit) {

    Box(modifier = Modifier.size((convertPxToDp(first / 3) - 8).dp).clickable {
        clicked()
    }) {
        AsyncImage(
            model = getUrl(postID) , // URL of the image
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
        )
    }

}