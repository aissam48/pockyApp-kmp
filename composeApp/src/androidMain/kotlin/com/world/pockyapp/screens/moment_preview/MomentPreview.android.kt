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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.screens.post_preview.PostUiState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_arrow_right_white
import java.io.File

@OptIn(KoinExperimentalAPI::class)
@Composable
actual fun MomentPreview(
    navController: NavHostController,
    path: String,
) {

    val viewModel: MomentPreviewViewModel = koinViewModel()
    val imageData = convertImageToByteArray(Uri.parse(path.replace("$", "/")), LocalContext.current)
    println("MomentPreview $imageData")

    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val title = remember {
        mutableStateOf("")
    }

    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "Close",
            onCancel = {
                showDialog = false
                navController.popBackStack()
            }
        )
    }

    LaunchedEffect(uiState){
        when (val state = uiState) {
            is MomentPreviewUiState.Loading -> {

            }

            is MomentPreviewUiState.Success -> {
                title.value = "Your moment has shared successfully"
                showDialog = true
            }

            is MomentPreviewUiState.Error -> {
                title.value = state.message
                showDialog = true
            }

            is MomentPreviewUiState.Idle -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(path.replace("$", "/")),
            contentDescription = "Captured Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(15.dp)
                    ).height(50.dp).width(130.dp)
                    .padding(5.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        viewModel.shareMoment(imageData)
                    }

            ) {

                when (uiState) {
                    is MomentPreviewUiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.height(50.dp).width(130.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp))
                        }
                    }

                    else -> {
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
