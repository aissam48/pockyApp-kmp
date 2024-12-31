package com.world.pockyapp.screens.moment_preview

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_arrow_right_white
import java.io.File

@Composable
actual fun MomentPreview(
    navController: NavHostController,
    path: String
) {

    val imageData = convertImageToByteArray(Uri.parse(path.replace("$", "/")), LocalContext.current)
    println("MomentPreview $imageData")
    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = rememberAsyncImagePainter(path.replace("$", "/")),
            contentDescription = "Captured Image",
            modifier = Modifier.fillMaxSize(),
            contentScale= ContentScale.FillHeight
        )

        Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                    .padding(10.dp).align(Alignment.CenterEnd).clickable {
                        /*if (photo.value != null) {
                            //viewModel.setPost(photo.value!!)
                        }*/
                    }

            ) {
                Text("Share moment", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.size(15.dp))
                Image(
                    painter = painterResource(Res.drawable.ic_arrow_right_white),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }

}

private fun convertImageToByteArray(uri: Uri, context: Context): ByteArray {
    val file = uri.path?.let { File(it) }
    val contentUri =
        file?.let { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) }

    return contentUri?.let { it ->
        context.contentResolver.openInputStream(it)?.use {
        it.readBytes()
    }
    } ?: ByteArray(0)
}
