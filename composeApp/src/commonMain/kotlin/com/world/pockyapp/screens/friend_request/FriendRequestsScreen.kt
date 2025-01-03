package com.world.pockyapp.screens.friend_request

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.FriendRequestModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black

@Composable
fun FriendRequestsScreen(
    navController: NavHostController, viewModel: FriendRequestsViewModel = koinViewModel()
) {

    val friendRequestsState = viewModel.friendRequestsState.collectAsState()
    val acceptRequestState = viewModel.acceptRequestState.collectAsState()
    val rejectRequestState = viewModel.rejectRequestState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFriendRequests()
    }

    when (val state = acceptRequestState.value) {
        is AcceptRequestsUiState.Loading -> {

        }

        is AcceptRequestsUiState.Success -> {
            viewModel.getFriendRequests()
        }

        is AcceptRequestsUiState.Error -> {

        }
    }

    when (val state = rejectRequestState.value) {
        is RejectRequestsUiState.Loading -> {

        }

        is RejectRequestsUiState.Success -> {
            viewModel.getFriendRequests()
        }

        is RejectRequestsUiState.Error -> {

        }
    }


    Scaffold(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(top = 15.dp, start = 10.dp, end = 10.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier.size(23.dp).clickable {
                        navController.popBackStack()
                    },
                    painter = painterResource(Res.drawable.ic_back_black),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    text = "Friend's Requests",
                    color = Color.Black,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.size(25.dp))

            when (val state = friendRequestsState.value) {
                is FriendRequestsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is FriendRequestsUiState.Success -> {
                    LazyColumn {
                        items(state.data) { item: FriendRequestModel ->

                            Column {

                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${item.profile.id}")
                                    }) {

                                    Image(
                                        painter = rememberAsyncImagePainter(getUrl(item.profile.photoID)),
                                        modifier = Modifier.size(40.dp).clip(CircleShape),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                    )
                                    Spacer(modifier = Modifier.size(5.dp))

                                    Text(
                                        text = "${item.profile.firstName} ${item.profile.lastName}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.weight(1f))

                                    Box(
                                        modifier = Modifier.background(
                                            color = Color.Green, shape = RoundedCornerShape(10.dp)
                                        ).height(30.dp).width(60.dp).clickable {
                                            viewModel.acceptFriendRequest(item.id)
                                        }, contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Accept",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.size(8.dp))
                                    Box(
                                        modifier = Modifier.background(
                                            color = Color.Red, shape = RoundedCornerShape(10.dp)
                                        ).height(30.dp).width(60.dp).clickable {
                                            viewModel.rejectFriendRequest(item.id)
                                        }, contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Reject",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                }
                                Spacer(modifier = Modifier.size(15.dp))
                            }

                        }
                    }
                }

                is FriendRequestsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
