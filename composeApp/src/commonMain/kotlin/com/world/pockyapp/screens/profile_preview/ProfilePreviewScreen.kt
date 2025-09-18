package com.world.pockyapp.screens.profile_preview

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.profile.ImagePost
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_be_friend
import pockyapp.composeapp.generated.resources.ic_block_black
import pockyapp.composeapp.generated.resources.ic_chat_bleu
import pockyapp.composeapp.generated.resources.ic_chat_request_blue
import pockyapp.composeapp.generated.resources.ic_location_black
import pockyapp.composeapp.generated.resources.ic_more_black
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.ic_report_black

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfilePreviewScreen(
    navController: NavHostController,
    id: String,
    viewModel: ProfilePreviewViewModel = koinViewModel()
) {

    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val profileState by viewModel.profileState.collectAsState()
    val beFriendState by viewModel.beFriendState.collectAsState()
    val unFriendState by viewModel.unFriendState.collectAsState()
    val myProfileState by viewModel.myProfileState.collectAsState()
    val blockState by viewModel.blockState.collectAsState()
    val unBlockState by viewModel.unBlockState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()
    val sendChatRequestState by viewModel.sendChatRequestState.collectAsState()
    val responseChatRequestState by viewModel.responseChatRequestState.collectAsState()

    val myProfile = remember { mutableStateOf(ProfileModel()) }
    val profile = remember { mutableStateOf(ProfileModel()) }
    val posts = remember { mutableStateOf(mutableSetOf<PostModel>()) }

    LaunchedEffect(Unit) {
        viewModel.getMyProfile()
        viewModel.getProfile(id = id)
        viewModel.getPosts(id = id)
    }

    LaunchedEffect(responseChatRequestState, beFriendState, unFriendState, sendChatRequestState) {
        viewModel.getProfile(id = id)
    }

    when (val state = myProfileState) {
        is MyProfileState.Loading -> {}
        is MyProfileState.Success -> {
            myProfile.value = state.profile
        }

        is MyProfileState.Error -> {}
    }

    when (val state = blockState) {
        is BlockState.Loading -> {}
        is BlockState.Success -> {
            viewModel.getProfile(id = id)
            viewModel.getPosts(id = id)
            posts.value = mutableSetOf()
        }

        is BlockState.Error -> {}
        is BlockState.Idle -> {}
    }

    when (val state = unBlockState) {
        is UnBlockState.Loading -> {}
        is UnBlockState.Success -> {
            viewModel.getProfile(id = id)
            viewModel.getPosts(id = id)
        }

        is UnBlockState.Error -> {}
        is UnBlockState.Idle -> {}
    }

    Scaffold(
        backgroundColor = Color(0xFFF5F5F5),
        topBar = {
            // Modern Chat Request Header
            if (profile.value.chatRequest != null && profile.value.chatRequest?.status == "NOT_YET") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFDFC46B),
                                    Color(0xFFDFC46B),
                                    Color(0xFFFFFFFF),
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    if (myProfile.value.id == profile.value.chatRequest?.senderID) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Chat Request Sent",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Waiting for response...",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    } else if (myProfile.value.id.isNotEmpty() && myProfile.value.id != profile.value.chatRequest?.senderID) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Incoming Chat Request",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Accept Button
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF4CAF50),
                                            shape = RoundedCornerShape(25.dp)
                                        )
                                        .height(45.dp)
                                        .width(120.dp)
                                        .clickable {
                                            viewModel.responseRequestChat(
                                                profile.value.chatRequest?.id ?: "",
                                                true,
                                                profile.value.chatRequest?.senderID ?: ""
                                            )
                                        }
                                        .shadow(4.dp, RoundedCornerShape(25.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Accept",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Reject Button
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFF44336),
                                            shape = RoundedCornerShape(25.dp)
                                        )
                                        .height(45.dp)
                                        .width(120.dp)
                                        .clickable {
                                            viewModel.responseRequestChat(
                                                profile.value.chatRequest?.id ?: "",
                                                false,
                                                profile.value.chatRequest?.senderID ?: ""
                                            )
                                        }
                                        .shadow(4.dp, RoundedCornerShape(25.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Decline",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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

                    // Friend Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (profile.value.friend == "NO") {
                                    viewModel.beFriend(profile.value.id)
                                } else if (profile.value.friend == "YES") {
                                    viewModel.removeFriend(profile.value.id)
                                }
                                scope.launch { scaffoldState.bottomSheetState.collapse() }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_be_friend),
                                contentDescription = null,
                                tint = Color(0xFF2196F3)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = when (profile.value.friend) {
                                    "NO" -> "Add Friend"
                                    "YES" -> "Remove Friend"
                                    else -> "Request Sent"
                                },
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Block Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (profile.value.block) {
                                    "NO" -> viewModel.block(profile.value.id)
                                    "BLOCKER" -> viewModel.unBlock(profile.value.id)
                                }
                                scope.launch { scaffoldState.bottomSheetState.collapse() }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_block_black),
                                contentDescription = null,
                                tint = Color(0xFFFF5722)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = when (profile.value.block) {
                                    "NO" -> "Block User"
                                    "BLOCKER" -> "Unblock User"
                                    else -> ""
                                },
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Report Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(NavRoutes.REPORT_PROFILE.route + "/${profile.value.id}")
                                scope.launch { scaffoldState.bottomSheetState.collapse() }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_report_black),
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = "Report User",
                                color = Color.Black,
                                fontSize = 16.sp
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
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 16.dp)
            ) {

                // Header with back button
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, CircleShape)
                                .clickable { navController.popBackStack() }
                                .shadow(2.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Res.drawable.ic_back_black),
                                contentDescription = null
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                when (val state = profileState) {
                    is ProfilePreviewUiState.Loading -> {
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

                    is ProfilePreviewUiState.Success -> {
                        profile.value = state.profile

                        when (profile.value.block) {
                            "NO" -> {
                                item {
                                    // Profile Header Card
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(0.dp, RoundedCornerShape(24.dp)),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        )
                                        {
                                            // Profile Picture and Action Buttons
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                // Profile Picture with Story Border
                                                val checkIfSeeAllMoments =
                                                    profile.value.moments.find { !it.viewed }
                                                Box(
                                                    modifier = Modifier
                                                        .size(140.dp)
                                                        .border(
                                                            width = 4.dp,
                                                            brush = Brush.linearGradient(
                                                                colors = if (checkIfSeeAllMoments != null) {
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
                                                        model = getUrl(profile.value.photoID),
                                                        contentDescription = "",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape)
                                                            .clickable {
                                                                if (profile.value.moments.isEmpty()) return@clickable
                                                                val modulesJson =
                                                                    Json.encodeToString(
                                                                        listOf(profile.value)
                                                                    ).replace("/", "%")
                                                                navController.navigate(
                                                                    NavRoutes.MOMENTS.route + "/${modulesJson}" + "/${0}" + "/${myProfile.value.id}"
                                                                )
                                                            },
                                                        placeholder = painterResource(Res.drawable.ic_placeholder),
                                                        error = painterResource(Res.drawable.ic_placeholder),
                                                    )
                                                }

                                                // Action Buttons
                                                Column {
                                                    when (true) {
                                                        (profile.value.chatRequest == null) -> {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .background(
                                                                        brush = Brush.linearGradient(
                                                                            colors = listOf(
                                                                                Color(
                                                                                    0xFF667eea
                                                                                ), Color(0xFF764ba2)
                                                                            )
                                                                        ),
                                                                        shape = CircleShape
                                                                    )
                                                                    .clickable {
                                                                        scope.launch {
                                                                            viewModel.sendRequestChat(
                                                                                profile.value.id
                                                                            )
                                                                        }
                                                                    }
                                                                    .shadow(4.dp, CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Image(
                                                                    painter = painterResource(Res.drawable.ic_chat_request_blue),
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(24.dp)
                                                                )
                                                            }
                                                        }

                                                        (profile.value.chatRequest?.status == "ACCEPTED") -> {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .background(
                                                                        brush = Brush.linearGradient(
                                                                            colors = listOf(
                                                                                Color(
                                                                                    0xFF4CAF50
                                                                                ), Color(0xFF8BC34A)
                                                                            )
                                                                        ),
                                                                        shape = CircleShape
                                                                    )
                                                                    .clickable {
                                                                        navController.navigate(
                                                                            NavRoutes.CHAT.route + "/${profile.value.conversationID}" + "/${profile.value.id}" + "/${profile.value.chatRequest?.id}"
                                                                        )
                                                                    }
                                                                    .shadow(4.dp, CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Image(
                                                                    painter = painterResource(Res.drawable.ic_chat_bleu),
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(24.dp)
                                                                )
                                                            }
                                                        }

                                                        else -> {}
                                                    }

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    Box(
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .background(
                                                                Color.White,
                                                                shape = CircleShape
                                                            )
                                                            .border(
                                                                2.dp,
                                                                Color.Gray.copy(alpha = 0.2f),
                                                                CircleShape
                                                            )
                                                            .clickable {
                                                                scope.launch {
                                                                    scaffoldState.bottomSheetState.expand()
                                                                }
                                                            }
                                                            .shadow(2.dp, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Image(
                                                            painter = painterResource(Res.drawable.ic_more_black),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(20.dp))

                                            // Profile Info
                                            Text(
                                                text = "${profile.value.firstName} ${profile.value.lastName}",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp
                                            )

                                            Text(
                                                text = "@${profile.value.username}",
                                                color = Color.Gray,
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
                                                    text = "${profile.value.country}, ${profile.value.city}",
                                                    color = Color(0xFF757575),
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            if (profile.value.description.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = profile.value.description,
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
                                    )
                                    {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            // Posts
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = when (val postsState = postsState) {
                                                        is PostsState.Success -> "${postsState.posts.size}"
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

                                            // Followers (you'll need to add this to your profile model)
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "O",
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
                                                    text = "0",
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

                                            // Stories/Moments
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "${profile.value.moments.size}",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp
                                                )
                                                Text(
                                                    text = "Stories",
                                                    color = Color.Gray,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                }

                                // Message Button
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                    ) {
                                        Row {
                                            Box (
                                                modifier = Modifier.weight(1f).height(40.dp)
                                                    .clickable {
                                                        if (profile.value.friend == "NO") {
                                                            viewModel.beFriend(profile.value.id)
                                                        } else if (profile.value.friend == "YES") {
                                                            viewModel.removeFriend(profile.value.id)
                                                        }
                                                    }.background(Color(0xFF000000), shape = RoundedCornerShape(10.dp)),
                                                ) {

                                                Text(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text = when (profile.value.friend) {
                                                        "NO" -> "Add Friend"
                                                        "YES" -> "Remove Friend"
                                                        else -> "Friending requested"
                                                    },
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )

                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Box (
                                                modifier = Modifier.weight(1f).height(40.dp)
                                                    .clickable {
                                                        when (true) {
                                                            (profile.value.chatRequest == null) -> {
                                                                scope.launch {
                                                                    viewModel.sendRequestChat(
                                                                        profile.value.id
                                                                    )
                                                                }
                                                            }

                                                            (profile.value.chatRequest?.status == "ACCEPTED") -> {
                                                                navController.navigate(
                                                                    NavRoutes.CHAT.route + "/${profile.value.conversationID}" + "/${profile.value.id}" + "/${profile.value.chatRequest?.id}"
                                                                )
                                                            }

                                                            else -> {}
                                                        }
                                                    }.background(Color(0xFF000000), shape = RoundedCornerShape(10.dp))
                                            ) {

                                                Text(
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text = when (true) {
                                                        (profile.value.chatRequest == null) -> "Request messaging"
                                                        (profile.value.chatRequest?.status == "ACCEPTED") -> "Message"
                                                        (profile.value.chatRequest?.status == "NOT_YET") -> "Messaging requested"
                                                        else -> "Request Messaging"
                                                    },
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )

                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Card (
                                                modifier = Modifier.weight(1f).height(40.dp)
                                                    .clickable {
                                                        if (profile.value.friend == "NO") {
                                                            viewModel.beFriend(profile.value.id)
                                                        } else if (profile.value.friend == "YES") {
                                                            viewModel.removeFriend(profile.value.id)
                                                        }
                                                    },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDFC46B)),
                                            ) {

                                                Text(
                                                    fontWeight = FontWeight.SemiBold,
                                                    text = when (profile.value.friend) {
                                                        "NO" -> "Add Friend"
                                                        "YES" -> "Remove Friend"
                                                        else -> "Request Sent"
                                                    },
                                                    color = Color.Black,
                                                    fontSize = 16.sp
                                                )

                                            }

                                        }


                                    }
                                }

                            }

                            "BLOCKED" -> {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "PockyApp User",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "This user's profile is not available",
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }

                            "BLOCKER" -> {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${profile.value.firstName} ${profile.value.lastName}",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(20.dp))

                                            Box(
                                                modifier = Modifier
                                                    .height(48.dp)
                                                    .width(140.dp)
                                                    .background(
                                                        brush = Brush.horizontalGradient(
                                                            colors = listOf(
                                                                Color(0xFF667eea),
                                                                Color(0xFF764ba2)
                                                            )
                                                        ),
                                                        shape = RoundedCornerShape(24.dp)
                                                    )
                                                    .clickable {
                                                        if (unBlockState !is UnBlockState.Loading) {
                                                            viewModel.unBlock(profile.value.id)
                                                        }
                                                    }
                                                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (unBlockState is UnBlockState.Loading) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Text(
                                                        text = "Unblock",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ProfilePreviewUiState.Error -> {
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

                // Posts Grid Section
                when (val state = postsState) {
                    is PostsState.Loading -> {
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

                    is PostsState.Success -> {
                        if (state.posts.isNotEmpty()) {
                            item {
                                // Posts Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Posts",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF667eea).copy(alpha = 0.1f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${state.posts.size}",
                                            color = Color(0xFF667eea),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
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
                                                    navController.navigate(NavRoutes.POST.route + "/${postModel.postID}" + "/${myProfile.value.id}")
                                                }
                                        ) {
                                            ImagePost(screenSize.value.first, postModel.postID) {
                                                navController.navigate(NavRoutes.POST.route + "/${postModel.postID}" + "/${myProfile.value.id}")
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
                                            text = "This user hasn't shared any posts",
                                            color = Color.Gray,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is PostsState.Error -> {
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

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            // Layout measurement for screen size
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