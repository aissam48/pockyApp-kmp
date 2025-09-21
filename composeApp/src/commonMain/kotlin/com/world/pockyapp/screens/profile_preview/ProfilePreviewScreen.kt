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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.moment_screen.MomentsViewModel
import com.world.pockyapp.screens.profile.CardMomentProfile
import com.world.pockyapp.screens.profile.ImagePost
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_block_black
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
    val momentsViewModel: MomentsViewModel = koinViewModel()

    val profileState by viewModel.profileState.collectAsState()
    val beFriendState by viewModel.beFriendState.collectAsState()
    val unFriendState by viewModel.unFriendState.collectAsState()
    val myProfileState by viewModel.myProfileState.collectAsState()
    val blockState by viewModel.blockState.collectAsState()
    val unBlockState by viewModel.unBlockState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()
    val sendChatRequestState by viewModel.sendChatRequestState.collectAsState()
    val responseChatRequestState by viewModel.responseChatRequestState.collectAsState()

    val acceptRequestState by viewModel.acceptRequestState.collectAsState()
    val rejectRequestState by viewModel.rejectRequestState.collectAsState()

    val followState by viewModel.followState.collectAsState()
    val unFollowState by viewModel.unFollowState.collectAsState()
    val cancelFriendRequestState by viewModel.cancelFriendRequestState.collectAsState()
    val cancelChatRequestState by viewModel.cancelChatRequestState.collectAsState()

    val momentsState by viewModel.momentsState.collectAsState()

    val myProfile = remember { mutableStateOf(ProfileModel()) }
    val profile = remember { mutableStateOf(ProfileModel()) }

    LaunchedEffect(Unit) {
        viewModel.getMyProfile()
        viewModel.getProfile(id = id)
        viewModel.getPosts(id = id)
        viewModel.getMoments(id = id)
    }

    LaunchedEffect(
        responseChatRequestState,
        beFriendState,
        unFriendState,
        sendChatRequestState,
        followState,
        unFollowState,
        acceptRequestState,
        rejectRequestState,
        cancelFriendRequestState,
        cancelChatRequestState
    ) {
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
        }

        is BlockState.Error -> {}
        is BlockState.Idle -> {}
    }

    when (val state = unBlockState) {
        is UnBlockState.Loading -> {}
        is UnBlockState.Success -> {
            viewModel.getProfile(id = id)
            viewModel.getPosts(id = id)
            viewModel.getMoments(id = id)
        }

        is UnBlockState.Error -> {}
        is UnBlockState.Idle -> {}
    }

    Scaffold(
        // backgroundColor = Color(0xFFF5F5F5),
        backgroundColor = Color(0xFFFFFFFF),
        topBar = {


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
                                painter = painterResource(Res.drawable.ic_block_black),
                                contentDescription = null,
                                tint = Color(0xFFFF5722)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = when (profile.value.block) {
                                    "NO" -> "Block"
                                    "BLOCKER" -> "Unblock"
                                    else -> ""
                                },
                                color = Color.Black,
                                fontSize = 14.sp
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
                                painter = painterResource(Res.drawable.ic_report_black),
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                text = "Report",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        )
        {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF))
            ) {
                // back button and more button
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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

                        Image(
                            painter = painterResource(Res.drawable.ic_more_black),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).clickable {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        )

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    Column {
                        if (profile.value.friendRequest != null
                            && profile.value.friendRequest?.senderID != myProfile.value.id && profile.value.friendRequest?.status == "NOT_YET"
                        ) {

                            Card(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(all = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {

                                    Box(
                                        modifier = Modifier.size(70.dp),
                                    )
                                    {
                                        AsyncImage(
                                            model = getUrl(myProfile.value.photoID),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .align(Alignment.CenterEnd),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )

                                        AsyncImage(
                                            model = getUrl(profile.value.photoID),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(50.dp)
                                                .align(Alignment.CenterStart),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )
                                    }

                                    Text(
                                        "👥 Friend Request",
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    )
                                    {
                                        // Reject Button
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFFFF0000).copy(alpha = 0.2f),
                                                    RoundedCornerShape(50)
                                                ) // light red background
                                                .height(40.dp)
                                                .width(120.dp)
                                                .clickable {
                                                    viewModel.rejectFriendRequest(profile.value.id)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (unFriendState is FriendState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.Red,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Reject",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Delete",
                                                        color = Color.Red,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Accept Button
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFF008000).copy(alpha = 0.2f),
                                                    RoundedCornerShape(50)
                                                ) // light green background
                                                .height(40.dp)
                                                .width(120.dp)
                                                .clickable {
                                                    viewModel.acceptFriendRequest(profile.value.id)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (beFriendState is FriendState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color(0xFF4CAF50),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Accept",
                                                        tint = Color(0xFF4CAF50),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Accept",
                                                        color = Color(0xFF4CAF50),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(15.dp))

                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        if (profile.value.chatRequest != null
                            && profile.value.chatRequest?.senderID != myProfile.value.id && profile.value.chatRequest?.status == "NOT_YET"
                        ) {

                            Card(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(all = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {

                                    Box(
                                        modifier = Modifier.size(70.dp),
                                    )
                                    {
                                        AsyncImage(
                                            model = getUrl(myProfile.value.photoID),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .align(Alignment.CenterEnd),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )

                                        AsyncImage(
                                            model = getUrl(profile.value.photoID),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(50.dp)
                                                .align(Alignment.CenterStart),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )
                                    }

                                    Text(
                                        "\uD83D\uDCAC Chat Request",
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    )
                                    {
                                        // Reject Button
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFFFF0000).copy(alpha = 0.2f),
                                                    RoundedCornerShape(50)
                                                ) // light red background
                                                .height(40.dp)
                                                .width(120.dp)
                                                .clickable {
                                                    viewModel.rejectFriendRequest(profile.value.id)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (unFriendState is FriendState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.Red,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Reject",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Delete",
                                                        color = Color.Red,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Accept Button
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFF008000).copy(alpha = 0.2f),
                                                    RoundedCornerShape(50)
                                                ) // light green background
                                                .height(40.dp)
                                                .width(120.dp)
                                                .clickable {
                                                    viewModel.acceptFriendRequest(profile.value.id)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (beFriendState is FriendState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color(0xFF4CAF50),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Accept",
                                                        tint = Color(0xFF4CAF50),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Accept",
                                                        color = Color(0xFF4CAF50),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(15.dp))

                                }
                            }
                        }
                    }
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
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(0.dp, RoundedCornerShape(24.dp))
                                            .padding(horizontal = 16.dp),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        )
                                        {
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
                                            )
                                            {
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
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    )
                                    {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {


                                            // Followers (you'll need to add this to your profile model)
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = profile.value.followers.toString(),
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
                                                    text = profile.value.followings.toString(),
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

                                            // Following
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = profile.value.friendsCount.toString(),
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

                                            // Stories/Moments
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "${profile.value.momentsCount}",
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
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                }

                                // Message Button
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(
                                                0xFFFFFFFF
                                            )
                                        ),
                                    ) {
                                        Row {
                                            Box(
                                                modifier = Modifier.weight(1f).height(40.dp)
                                                    .clickable {
                                                        if (profile.value.friendRequest != null) {
                                                            when (profile.value.friendRequest?.status) {
                                                                "ACCEPTED" -> {
                                                                    viewModel.removeFriend(profile.value.id)
                                                                }

                                                                "NOT_YET" -> {
                                                                    viewModel.cancelFriendRequest(
                                                                        profile.value.friendRequest?.id
                                                                            ?: ""
                                                                    )
                                                                }

                                                                "REJECTED" -> {
                                                                    viewModel.beFriend(profile.value.id)
                                                                }

                                                            }
                                                        } else {
                                                            viewModel.beFriend(profile.value.id)

                                                        }

                                                    }.background(
                                                        color =
                                                            if (profile.value.friendRequest != null) {
                                                                when (profile.value.friendRequest?.status) {
                                                                    "ACCEPTED" -> {
                                                                        Color(0xFFDFC46B)
                                                                    }

                                                                    "NOT_YET" -> {
                                                                        Color(0xFF808080)
                                                                    }

                                                                    "REJECTED" -> {
                                                                        Color(0xFF000000)
                                                                    }

                                                                    else -> {
                                                                        Color(0xFF000000)
                                                                    }
                                                                }
                                                            } else {
                                                                Color(0xFF000000)

                                                            },
                                                        shape = RoundedCornerShape(10.dp)
                                                    ),
                                            )
                                            {


                                                Text(
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text =
                                                        if (profile.value.friendRequest != null) {
                                                            when (profile.value.friendRequest?.status) {
                                                                "ACCEPTED" -> {
                                                                    "Remove Friend"
                                                                }

                                                                "NOT_YET" -> {
                                                                    "Friending requested"
                                                                }

                                                                "REJECTED" -> {
                                                                    "Add Friend"
                                                                }

                                                                else -> {
                                                                    "Add Friend"
                                                                }
                                                            }
                                                        } else {
                                                            "Add Friend"

                                                        },
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )

                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Box(
                                                modifier = Modifier.weight(1f).height(40.dp)
                                                    .clickable {
                                                        if (profile.value.chatRequest != null) {
                                                            when (profile.value.chatRequest?.status) {
                                                                "ACCEPTED" -> {
                                                                    navController.navigate(
                                                                        NavRoutes.CHAT.route + "/${profile.value.conversationID}" + "/${profile.value.id}" + "/${profile.value.chatRequest?.id}"
                                                                    )
                                                                }

                                                                "NOT_YET" -> {
                                                                    viewModel.cancelRequestChat(
                                                                        profile.value.chatRequest?.id
                                                                            ?: ""
                                                                    )
                                                                }

                                                                "REJECTED" -> {
                                                                    viewModel.sendRequestChat(
                                                                        profile.value.id
                                                                    )
                                                                }
                                                            }
                                                        } else {
                                                            viewModel.sendRequestChat(
                                                                profile.value.id
                                                            )
                                                        }

                                                    }.background(
                                                        color =
                                                            if (profile.value.chatRequest != null) {
                                                                when (profile.value.chatRequest?.status) {
                                                                    "ACCEPTED" -> {
                                                                        Color(0xFFDFC46B)
                                                                    }

                                                                    "NOT_YET" -> {
                                                                        Color(0xFF808080)
                                                                    }

                                                                    "REJECTED" -> {
                                                                        Color(0xFF000000)
                                                                    }

                                                                    else -> {
                                                                        Color(0xFF000000)
                                                                    }
                                                                }
                                                            } else {
                                                                Color(0xFF000000)

                                                            },
                                                        shape = RoundedCornerShape(10.dp)
                                                    ),
                                            )
                                            {


                                                Text(
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text =
                                                        if (profile.value.chatRequest != null) {
                                                            when (profile.value.chatRequest?.status) {
                                                                "ACCEPTED" -> {
                                                                    "Message"
                                                                }

                                                                "NOT_YET" -> {
                                                                    "Messaging requested"
                                                                }

                                                                "REJECTED" -> {
                                                                    "Request messaging"
                                                                }

                                                                else -> {
                                                                    "Request messaging"
                                                                }
                                                            }
                                                        } else {
                                                            "Request messaging"

                                                        },
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Box(
                                                modifier = Modifier.weight(1f).height(40.dp)
                                                    .clickable {
                                                        if (profile.value.follower) {
                                                            viewModel.unFollow(profile.value.id)
                                                        } else {
                                                            viewModel.follow(profile.value.id)
                                                        }
                                                    }.background(
                                                        color = Color(
                                                            when (profile.value.follower) {
                                                                true -> 0xFFDFC46B
                                                                false -> 0xFF000000
                                                            }
                                                        ),
                                                        shape = RoundedCornerShape(10.dp)
                                                    ),

                                                ) {

                                                Text(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text = when (profile.value.follower) {
                                                        true -> "Following"
                                                        false -> "Follow"
                                                    },
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )

                                            }

                                        }


                                    }
                                }

                            }

                            "BLOCKED" -> {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "NearVibe User",
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
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    )
                                    {
                                        Column(
                                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${profile.value.firstName} ${profile.value.lastName}",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(20.dp))

                                            Box(
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .width(90.dp)
                                                    .background(
                                                        color = Color(0xFFDFC46B),
                                                        shape = RoundedCornerShape(24.dp)
                                                    )
                                                    .clickable {
                                                        if (unBlockState !is UnBlockState.Loading) {
                                                            viewModel.unBlock(profile.value.id)
                                                        }
                                                    },
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
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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


                when (val state = momentsState) {
                    is MomentsState.Loading -> {
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
                                        text = "Loading moments...",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    is MomentsState.Success -> {

                        item {
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Moments",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (state.moments.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                                {

                                    val groupedByDay: Map<LocalDate, List<MomentModel>> =
                                        state.moments
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
                                                        momentsViewModel.myID = myProfile.value.id
                                                        momentsViewModel.selectedIndex = 0
                                                        navController.navigate(NavRoutes.MOMENTS.route)

                                                    }) {
                                                        CardMomentProfile(
                                                            myID = state.moments[0].profile.id,
                                                            moment = coupleOfMoments[0],
                                                            navController = navController,
                                                            rotation = 0f
                                                        )
                                                    }

                                                2 -> {
                                                    Box(modifier = Modifier.clickable {
                                                        momentsViewModel.moments =
                                                            listOf(coupleOfMoments)
                                                        momentsViewModel.myID = myProfile.value.id
                                                        momentsViewModel.selectedIndex = 0
                                                        navController.navigate(NavRoutes.MOMENTS.route)

                                                    }) {
                                                        CardMomentProfile(
                                                            myID = state.moments[0].profile.id,
                                                            moment = coupleOfMoments[0],
                                                            navController = navController,
                                                            rotation = -5f
                                                        )
                                                        CardMomentProfile(
                                                            myID = state.moments[0].profile.id,
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
                                                        momentsViewModel.myID = myProfile.value.id
                                                        momentsViewModel.selectedIndex = 0
                                                        navController.navigate(NavRoutes.MOMENTS.route)

                                                    }) {
                                                        CardMomentProfile(
                                                            myID = state.moments[0].profile.id,
                                                            moment = coupleOfMoments[0],
                                                            navController = navController,
                                                            rotation = 0f
                                                        )
                                                        CardMomentProfile(
                                                            myID = state.moments[0].profile.id,
                                                            moment = coupleOfMoments[1],
                                                            navController = navController,
                                                            rotation = 5f
                                                        )


                                                        CardMomentProfile(
                                                            myID = state.moments[0].profile.id,
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
                            } else {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp).padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                )
                                {
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
                                            text = "No moments yet",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "${profile.value.firstName} hasn't shared any moments",
                                            color = Color.Gray,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }


                        }

                    }

                    is MomentsState.Error -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp).padding(horizontal = 16.dp),
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

                    MomentsState.Idle -> {

                    }
                }

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

                        item {
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Posts",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        if (state.posts.isNotEmpty()) {

                            items(state.posts.chunked(3)) { rowPosts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                                        .padding(vertical = 20.dp).padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                )
                                {
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
                                            text = "${profile.value.firstName} hasn't shared any posts",
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
                                    .padding(vertical = 16.dp).padding(horizontal = 16.dp),
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
                modifier = Modifier.fillMaxWidth().height(0.dp).padding(horizontal = 16.dp),
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
fun CardMoment(
    myID: String,
    moment: MomentModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: MomentsViewModel = koinViewModel()
    Box(
        modifier = modifier
            .height(150.dp)
            .width(90.dp),
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
                    .fillMaxSize()
                    .clickable {

                        viewModel.moments = listOf(listOf(moment))
                        viewModel.selectedIndex = 0
                        viewModel.myID = myID

                        navController.navigate(
                            NavRoutes.MOMENTS.route
                        )
                    },
                contentDescription = null
            )
        }
    }
}
