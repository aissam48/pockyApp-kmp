package com.world.pockyapp.screens.followers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.components.ModernHeader
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersListScreen(
    navController: NavHostController,
    viewModel: FollowersViewModel = koinViewModel(),
    id: String
) {


    val followersState = viewModel.followersState.collectAsState()
    val profileState = viewModel.myProfileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMyProfile()
        viewModel.getFollowers(id)
    }

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(horizontal = 10.dp)
    ) { paddingValuesp ->


        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFFFFFFF))
        )
        {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            )
            {

                ModernHeader("Followers"){
                    navController.popBackStack()
                }
            }

            when (val state = followersState.value) {

                is ResponseState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFDFC46B),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                is ResponseState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.data.isEmpty()) {
                            EmptyFollowersState()
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(
                                    items = state.data,
                                    key = { it.id }
                                ) { follower ->
                                    FollowerCard(
                                        follower = follower,
                                        onCardClick = {
                                            if (profileState.value is ResponseState.Success) {
                                                if ((profileState.value as ResponseState.Success<ProfileModel>).data.id == follower.id) {
                                                    navController.navigate(NavRoutes.MY_PROFILE.route)
                                                } else {
                                                    navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${follower.id}")
                                                }
                                            } else {
                                                navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${follower.id}")
                                            }
                                        }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }


                    }
                }

                is ResponseState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.error.message,
                                color = Color(0xFFE53E3E),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                else -> {}
            }
        }

    }
}

@Composable
private fun FollowerCard(
    follower: ProfileModel,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Profile image with online indicator
            Box {
                AsyncImage(
                    model = getUrl(follower.photoID),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Color.Transparent,
                            CircleShape
                        ),
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder)
                )


            }

            Spacer(modifier = Modifier.width(12.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${follower.firstName} ${follower.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "@${follower.username}",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}

@Composable
private fun EmptyFollowersState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👥",
                fontSize = 64.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No followers yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "When people follows, they'll appear here",
                fontSize = 16.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
        }
    }
}
