package com.world.pockyapp.screens.view_post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.screens.components.CustomDialog
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_delete
import pockyapp.composeapp.generated.resources.ic_like
import pockyapp.composeapp.generated.resources.ic_unlike_black

@Composable
fun ViewPostScreen(
    navController: NavHostController,
    postID: String,
    myID: String,
    viewModel: ViewPostViewModel = koinViewModel()
) {

    val postState by viewModel.postState.collectAsState()
    val deletePostState by viewModel.deleteState.collectAsState()

    val liked = remember {
        mutableStateOf(false)
    }

    val post = remember {
        mutableStateOf(PostModel())
    }
    LaunchedEffect(postState) {
        liked.value = post.value.likes.contains(myID)
    }

    LaunchedEffect(Unit) {
        viewModel.getPost(postID)
    }

    var showDialog by remember { mutableStateOf(false) }


    when (deletePostState) {
        is DeleteResultState.Loading -> {

        }

        is DeleteResultState.Success -> {
            navController.popBackStack()
        }

        is DeleteResultState.Error -> {

        }
    }

    if (showDialog) {
        CustomDialog(
            title = "Are you sure you want to delete this post?",
            action1 = "Cancel",
            action2 = "Delete",
            onCancel = { showDialog = false },
            onDelete = {
                showDialog = false
                viewModel.deletePost(postID)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when (val state = postState) {
            is PostResultState.Loading -> {

            }

            is PostResultState.Success -> {
                post.value = state.post
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(getUrl(postID)),
                    contentDescription = null
                )
            }

            is PostResultState.Error -> {

            }
        }

        Row {
            Spacer(modifier = Modifier.size(10.dp))
            Image(
                modifier = Modifier.size(23.dp).clickable {
                    navController.popBackStack()
                },
                painter = painterResource(Res.drawable.ic_back_black),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = if (liked.value)
                    painterResource(Res.drawable.ic_like)
                else painterResource(
                    Res.drawable.ic_unlike_black
                ),
                modifier = Modifier.size(40.dp).clickable {
                    if (post.value.likes.contains(myID)) {
                        post.value.likes.remove(myID)
                        viewModel.unLike(postID)
                        liked.value = false
                    } else {
                        post.value.likes.add(myID)
                        viewModel.like(postID)
                        liked.value = true
                    }
                },
                contentDescription = null
            )

            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = post.value.likes.size.toString(),
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.size(20.dp))

            if (myID == post.value.ownerID) {
                Image(
                    painter = painterResource(Res.drawable.ic_delete),
                    modifier = Modifier.size(35.dp).clickable {
                        showDialog = true
                        //viewModel.deletePost(postID)
                    },
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.size(50.dp))
        }
    }
}

