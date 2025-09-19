package com.world.pockyapp.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.moment_screen.MomentsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_add_black
import pockyapp.composeapp.generated.resources.ic_add_post_black
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_be_friend
import pockyapp.composeapp.generated.resources.ic_location_black
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.ic_settings_black
import pockyapp.composeapp.generated.resources.is_add_story_black


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = koinViewModel()) {

    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val momentsViewModel: MomentsViewModel = koinViewModel()
    val profileState by viewModel.profileState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()

    val profile = remember { mutableStateOf(ProfileModel()) }

    LaunchedEffect(Unit) {
        viewModel.getProfile()
        delay(1000)
        viewModel.getMyPosts()
    }

    Scaffold(
        backgroundColor = Color(0xFFFFFFFF),
        topBar = {}
    ) { padding ->

        BottomSheetScaffold(
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetBackgroundColor = Color.White,
            sheetContent = {
                // Modern Bottom Sheet Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Handle bar
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(5.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.5.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Share Moment Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(NavRoutes.CAMERA.route)
                                scope.launch { scaffoldState.bottomSheetState.collapse() }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFC46B)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.is_add_story_black),
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = "Share Moment",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Share Post Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(NavRoutes.POST_PREVIEW.route)
                                scope.launch { scaffoldState.bottomSheetState.collapse() }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFC46B)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.ic_add_post_black),
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = "Share Post",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF))
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(23.dp).clickable {
                                navController.popBackStack()
                            },
                            painter = painterResource(Res.drawable.ic_back_black),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(Res.drawable.ic_be_friend),
                                contentDescription = null,
                                modifier = Modifier.size(35.dp)
                                    .clickable {
                                        navController.navigate(NavRoutes.FRIEND_REQUESTS.route)
                                    }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(Res.drawable.ic_settings_black),
                                contentDescription = null,
                                modifier = Modifier.size(35.dp)
                                    .clickable {
                                        navController.navigate(NavRoutes.SETTINGS.route)
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                when (val state = profileState) {
                    is ProfileUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF667eea),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }

                    is ProfileUiState.Success -> {
                        profile.value = state.profile

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(0.dp, RoundedCornerShape(24.dp)),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(140.dp)
                                            .border(
                                                width = 4.dp,
                                                brush = Brush.linearGradient(
                                                    colors = if (state.profile.moments.any { !it.viewed }) {
                                                        listOf(
                                                            Color(0xFFE91E63),
                                                            Color(0xFFFF9800),
                                                            Color(0xFFFFEB3B)
                                                        )
                                                    } else {
                                                        listOf(
                                                            Color.Gray.copy(alpha = 0.3f),
                                                            Color.Gray.copy(alpha = 0.3f)
                                                        )
                                                    }
                                                ),
                                                shape = CircleShape
                                            )
                                            .padding(4.dp),
                                    ) {
                                        AsyncImage(
                                            model = getUrl(state.profile.photoID),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .clickable {
                                                    if (state.profile.moments.isEmpty()) return@clickable

                                                    momentsViewModel.moments =
                                                        listOf(state.profile.moments)
                                                    momentsViewModel.myID = state.profile.id
                                                    momentsViewModel.selectedIndex = 0

                                                    navController.navigate(NavRoutes.MOMENTS.route)
                                                },
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )

                                        // Add Content Button
                                        Icon(
                                            painterResource(Res.drawable.ic_add_black),
                                            null,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .clickable {
                                                    scope.launch {
                                                        scaffoldState.bottomSheetState.expand()
                                                    }
                                                }
                                                .size(40.dp)
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Profile Info
                                    Text(
                                        text = "${state.profile.firstName} ${state.profile.lastName}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = "@${state.profile.username}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Location
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_location_black),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF757575)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${state.profile.country}, ${state.profile.city}",
                                            color = Color(0xFF757575),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                    }

                                    if (state.profile.description.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = state.profile.description,
                                            color = Color(0xFF424242),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 15.sp,
                                            lineHeight = 20.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Followers
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = state.profile.followers.toString(),
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Followers",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Following
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = state.profile.followings.toString(),
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Following",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Friends
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = state.profile.friendsCount.toString(),
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Friends",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Moments
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${state.profile.momentsCount}",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Moments",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Posts
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = when (val postsState = postsState) {
                                                is PostsUiState.Success -> "${postsState.posts.size}"
                                                else -> "0"
                                            },
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Posts",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Album Section
                        item {
                            Text(
                                text = "Your Moments",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(modifier = Modifier.fillMaxWidth()) {
                                val moments =
                                    state.profile.album.sortedByDescending { it.createdAt }
                                val groupedByDay: Map<LocalDate, List<MomentModel>> =
                                    state.profile.album
                                        .sortedByDescending { it.createdAt }
                                        .groupBy {
                                            val instant = Instant.parse(
                                                it.createdAt.replace(
                                                    " ",
                                                    "T"
                                                ) + ".120Z"
                                            )
                                            // Convert to local date
                                            instant.toLocalDateTime(TimeZone.currentSystemDefault()).date


                                        }

                                items(groupedByDay.values.toList()) { coupleOfMoments ->

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        when (coupleOfMoments.size) {
                                            1 ->
                                                Box(modifier = Modifier.clickable {
                                                    momentsViewModel.moments =
                                                        listOf(coupleOfMoments)
                                                    momentsViewModel.myID = state.profile.id
                                                    momentsViewModel.selectedIndex = 0
                                                    navController.navigate(NavRoutes.MOMENTS.route)

                                                }) {
                                                    CardMomentProfile(
                                                        myID = state.profile.id,
                                                        moment = coupleOfMoments[0],
                                                        navController = navController,
                                                        rotation = 0f
                                                    )
                                                }

                                            2 -> {
                                                Box(modifier = Modifier.clickable {
                                                    momentsViewModel.moments =
                                                        listOf(coupleOfMoments)
                                                    momentsViewModel.myID = state.profile.id
                                                    momentsViewModel.selectedIndex = 0
                                                    navController.navigate(NavRoutes.MOMENTS.route)

                                                }) {
                                                    CardMomentProfile(
                                                        myID = state.profile.id,
                                                        moment = coupleOfMoments[0],
                                                        navController = navController,
                                                        rotation = -5f
                                                    )
                                                    CardMomentProfile(
                                                        myID = state.profile.id,
                                                        moment = coupleOfMoments[1],
                                                        navController = navController,
                                                        rotation = 5f
                                                    )

                                                }
                                            }

                                            else -> {
                                                Box(modifier = Modifier.clickable {
                                                    momentsViewModel.moments =
                                                        listOf(coupleOfMoments)
                                                    momentsViewModel.myID = state.profile.id
                                                    momentsViewModel.selectedIndex = 0
                                                    navController.navigate(NavRoutes.MOMENTS.route)

                                                }) {
                                                    CardMomentProfile(
                                                        myID = state.profile.id,
                                                        moment = coupleOfMoments[0],
                                                        navController = navController,
                                                        rotation = 0f
                                                    )
                                                    CardMomentProfile(
                                                        myID = state.profile.id,
                                                        moment = coupleOfMoments[1],
                                                        navController = navController,
                                                        rotation = 5f
                                                    )


                                                    CardMomentProfile(
                                                        myID = state.profile.id,
                                                        moment = coupleOfMoments[2],
                                                        navController = navController,
                                                        rotation = -5f
                                                    )

                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        val instant = Instant.parse(
                                            coupleOfMoments.last().createdAt.replace(
                                                " ",
                                                "T"
                                            ) + ".120Z"
                                        )
                                        // Convert to local date
                                        val date =
                                            instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                        Text(
                                            text = "${date.dayOfMonth}/${date.monthNumber}/${date.year}",
                                            color = Color.Black,
                                            fontSize = 10.sp
                                        )
                                    }


                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                    }

                    is ProfileUiState.Error -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Error loading profile",
                                        color = Color(0xFFD32F2F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = state.error.message ?: "Unknown error",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                when (val state = postsState) {
                    is PostsUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF667eea),
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Loading posts...",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    is PostsUiState.Success -> {
                        if (state.posts.isNotEmpty()) {

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Your Posts",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            items(state.posts.chunked(3)) { rowPosts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    rowPosts.forEachIndexed { index, postModel ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    navController.navigate(NavRoutes.POST.route + "/${postModel.postID}" + "/${profile.value.id}")
                                                }
                                        ) {
                                            ImagePost(screenSize.value.first, postModel.postID) {
                                                navController.navigate(NavRoutes.POST.route + "/${postModel.postID}" + "/${profile.value.id}")
                                            }
                                        }
                                    }

                                    // Fill remaining spaces if less than 3 items
                                    repeat(3 - rowPosts.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        } else {
                            // No posts message
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(
                                                    Color(0xFF667eea).copy(alpha = 0.1f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "📸",
                                                fontSize = 32.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No posts yet",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Share your first post to get started",
                                            color = Color.Gray,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is PostsUiState.Error -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "⚠️",
                                        fontSize = 32.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Unable to load posts",
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Please try again later",
                                        color = Color(0xFFE65100),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            Layout(
                modifier = Modifier.fillMaxWidth().height(0.dp),
                measurePolicy = { measurables, constraints ->
                    val width = constraints.maxWidth
                    val height = constraints.maxHeight
                    screenSize.value = Pair(width, height)
                    layout(width, height) {}
                }
            )
        }
    }
}

@Composable
fun CardMomentProfile(
    myID: String,
    moment: MomentModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    rotation: Float = 0f
) {
    val viewModel: MomentsViewModel = koinViewModel()
    Box(
        modifier = modifier
            .height(150.dp)
            .width(90.dp).rotate(rotation),
    ) {
        androidx.compose.material.Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = Color.LightGray,
            modifier = Modifier
                .height(150.dp)
                .width(90.dp)
        ) {
            AsyncImage(
                model = getUrl(moment.momentID),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize(),
                contentDescription = null
            )
        }
    }
}

@Composable
fun convertPxToDp(px: Int): Float {
    val density = LocalDensity.current
    return with(density) {
        px.toDp().value
    }
}