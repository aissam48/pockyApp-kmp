package com.world.pockyapp.screens.post_preview

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_arrow_right_white
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_pick_image

@Composable
fun PostPreview(navController: NavHostController, viewModel: PostViewModel = koinViewModel()) {

    val scope = rememberCoroutineScope()
    val photo = remember {
        mutableStateOf<ByteArray?>(null)
    }

    val state by viewModel.uiState.collectAsState()

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                photo.value = it
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (photo.value != null) {
            Image(
                modifier = Modifier.fillMaxSize().clickable {
                    singleImagePicker.launch()
                },
                bitmap = photo.value!!.toImageBitmap(),
                contentDescription = "Captured Image",
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center).clickable {
                    singleImagePicker.launch()
                },
                painter = painterResource(Res.drawable.ic_pick_image),
                contentDescription = "Captured Image",
                contentScale = ContentScale.Crop
            )
        }

        Image(
            painter = painterResource(Res.drawable.ic_close_black),
            contentDescription = null,
            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
                .clickable {
                    navController.popBackStack()
                })

        Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ).height(50.dp).width(130.dp)
                    .padding(10.dp).align(Alignment.CenterEnd).clickable {
                        if (photo.value != null) {
                            viewModel.setPost(photo.value!!)
                        }
                    }

            ) {

                when (state) {
                    is PostUiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.height(50.dp).width(130.dp)
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is PostUiState.Success -> {
                        Text("Share post", color = MaterialTheme.colorScheme.onPrimary)

                        Spacer(modifier = Modifier.size(15.dp))

                        Image(
                            painter = painterResource(Res.drawable.ic_arrow_right_white),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        navController.popBackStack()
                    }

                    is PostUiState.Error -> {
                        Text("Share post", color = MaterialTheme.colorScheme.onPrimary)

                        Spacer(modifier = Modifier.size(15.dp))

                        Image(
                            painter = painterResource(Res.drawable.ic_arrow_right_white),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    PostUiState.Idle -> {
                        Text("Share post", color = MaterialTheme.colorScheme.onPrimary)

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