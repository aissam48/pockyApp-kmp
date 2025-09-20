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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.screens.components.CustomDialog
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPostScreen(
    navController: NavHostController,
    postID: String,
    myID: String,
    viewModel: ViewPostViewModel = koinViewModel()
) {

    val postState by viewModel.postState.collectAsState()
    val deletePostState by viewModel.deleteState.collectAsState()

    val liked = remember { mutableStateOf(false) }
    val post = remember { mutableStateOf(PostModel()) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postState) {
        if (postState is PostResultState.Success) {
            post.value = (postState as PostResultState.Success).post
            liked.value = post.value.likes.contains(myID)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPost(postID)
    }

    LaunchedEffect(deletePostState) {
        when (deletePostState) {
            is DeleteResultState.Success -> {
                navController.popBackStack()
            }
            else -> {}
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFDFC46B),
                        strokeWidth = 3.dp
                    )
                }
            }

            is PostResultState.Success -> {
                // Full screen image
                AsyncImage(
                    model = getUrl(postID),
                    contentDescription = "Post Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay for better text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }

            is PostResultState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Failed to load post",
                                color = Color(0xFFE53E3E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            is PostResultState.Idle -> {

            }
        }

        // Top Navigation
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .background(
                    Color.White.copy(alpha = 0.5f),
                    CircleShape
                )
                .clickable { navController.popBackStack() }
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_back_black),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
        }

        // Action Panel
        if (postState is PostResultState.Success) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Like Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (liked.value) Color(0xFFE91E63).copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                )
                                .clickable {
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
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (liked.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (liked.value) "Unlike" else "Like",
                                tint = if (liked.value) Color(0xFFE91E63) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${post.value.likes.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        if (post.value.likes.isNotEmpty()) {
                            Text(
                                text = if (post.value.likes.size == 1) "like" else "likes",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Delete Button (only for post owner)
                    if (myID == post.value.ownerID) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color(0xFFEF4444).copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .clickable { showDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Post",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Post Info Overlay (Bottom)
        if (postState is PostResultState.Success && post.value.profile.firstName.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${post.value.profile.firstName} ${post.value.profile.lastName}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    if (post.value.geoLocation.country.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${post.value.geoLocation.country}, ${post.value.geoLocation.street}",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Posted recently",
                        color = Color.Gray.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Loading overlay for delete action
        if (deletePostState is DeleteResultState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFDFC46B),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Deleting post...",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}