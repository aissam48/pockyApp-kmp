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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.profile.ImagePost
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_be_friend
import pockyapp.composeapp.generated.resources.ic_chat_bleu
import pockyapp.composeapp.generated.resources.ic_chat_request_blue
import pockyapp.composeapp.generated.resources.ic_location_black
import pockyapp.composeapp.generated.resources.ic_more_black

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
    val postsState by viewModel.postsState.collectAsState()
    val sendChatRequestState by viewModel.sendChatRequestState.collectAsState()
    val responseChatRequestState by viewModel.responseChatRequestState.collectAsState()

    // Trigger initial data load
    LaunchedEffect(Unit) {
        viewModel.getMyProfile()
        viewModel.getProfile(id = id)
        viewModel.getPosts(id = id)
    }

    LaunchedEffect(responseChatRequestState, beFriendState, unFriendState, sendChatRequestState) {
        viewModel.getProfile(id = id)
    }

    Scaffold(topBar = {
        val status = profileState
        if (status is ProfilePreviewUiState.Success
            && status.profile.chatRequest != null
            && status.profile.chatRequest.status == "NOT_YET"
            && myProfileState?.id == status.profile.chatRequest.senderID
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    "You requested chat",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.size(15.dp))

            }
        } else {
            if (status is ProfilePreviewUiState.Success
                && status.profile.chatRequest != null
                && status.profile.chatRequest.status == "NOT_YET" &&
                myProfileState?.id?.isNotEmpty() == true && myProfileState?.id != status.profile.chatRequest.senderID
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        "Request of chat",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.size(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            modifier = Modifier.background(
                                color = Color.Green,
                                shape = RoundedCornerShape(10.dp)
                            ).height(30.dp).width(90.dp).clickable {
                                viewModel.responseRequestChat(
                                    status.profile.chatRequest.id,
                                    true,
                                    status.profile.chatRequest.senderID
                                )
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Accept",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier.background(
                                color = Color.Red,
                                shape = RoundedCornerShape(10.dp)
                            ).height(30.dp).width(90.dp).clickable {
                                viewModel.responseRequestChat(
                                    status.profile.chatRequest.id,
                                    false,
                                    status.profile.chatRequest.senderID
                                )
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Reject",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))

                }

            }
        }

    }) { padding ->
        when (val state = profileState) {
            is ProfilePreviewUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfilePreviewUiState.Success -> {
                BottomSheetScaffold(
                    sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetBackgroundColor = Color.LightGray,
                    sheetContent = {
                        Column(
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        ) {
                            Spacer(modifier = Modifier.size(20.dp))
                            Row(
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                                    .clickable {
                                        if (beFriendState.isEmpty() && state.profile.friend == "NO") {
                                            viewModel.beFriend(state.profile.id)
                                        } else {
                                            if (state.profile.friend == "YES") {
                                                viewModel.removeFriend(state.profile.id)
                                            }
                                        }
                                        scope.launch {
                                            scaffoldState.bottomSheetState.collapse()
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_be_friend),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    fontWeight = FontWeight.Bold,
                                    text = if (beFriendState.isEmpty() && state.profile.friend == "NO")
                                        "Be friend"
                                    else
                                        if (state.profile.friend == "YES")
                                            "Remove friend" else
                                            "Request has sent",
                                    color = Color.Black,
                                    fontSize = 15.sp
                                )
                            }

                        }

                    }
                ) {
                    LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                        item {
                            Spacer(modifier = Modifier.size(20.dp))
                        }
                        item {
                            Image(
                                modifier = Modifier.size(23.dp).clickable {
                                    navController.popBackStack()
                                },
                                painter = painterResource(Res.drawable.ic_back_black),
                                contentDescription = null
                            )

                        }
                        item {

                            Box(modifier = Modifier.fillMaxWidth()) {

                                Box(modifier = Modifier.size(150.dp)) {


                                    val checkIfSeeAllMoments =
                                        state.profile.moments.find { !it.viewed }
                                    Box(
                                        modifier = Modifier
                                            .size(150.dp)
                                            .border(
                                                width = 5.dp,
                                                brush = Brush.linearGradient(
                                                    colors = if (checkIfSeeAllMoments != null) listOf(
                                                        Color.Red,
                                                        Color.Yellow,
                                                        Color.White
                                                    ) else listOf(
                                                        Color.Gray,
                                                        Color.Gray,
                                                        Color.Gray
                                                    )
                                                ),
                                                shape = CircleShape
                                            ),
                                    ) {
                                        Image(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(150.dp).clip(CircleShape)
                                                .clickable {
                                                    if (state.profile.moments.isEmpty()){
                                                        return@clickable
                                                    }
                                                    val modulesJson =
                                                        Json.encodeToString(listOf(state.profile))
                                                            .replace("/", "%")
                                                    navController.navigate(
                                                        NavRoutes.MOMENTS.route + "/${modulesJson}" + "/${0}" + "/${myProfileState?.id}"
                                                    )
                                                },
                                            painter = if (state.profile.photoID.isEmpty()) painterResource(
                                                Res.drawable.compose_multiplatform
                                            ) else rememberAsyncImagePainter(getUrl(state.profile.photoID)),
                                            contentDescription = null
                                        )
                                    }
                                }

                                Row(modifier = Modifier.align(Alignment.TopEnd)) {

                                    when (true) {
                                        (state.profile.chatRequest == null) -> {
                                            Image(
                                                painter = painterResource(Res.drawable.ic_chat_request_blue),
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                                    .clickable {
                                                        scope.launch {
                                                            viewModel.sendRequestChat(state.profile.id)
                                                        }
                                                    }
                                            )
                                        }

                                        (state.profile.chatRequest.status == "ACCEPTED") -> {
                                            Image(
                                                painter = painterResource(Res.drawable.ic_chat_bleu),
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                                    .clickable {
                                                        navController.navigate(NavRoutes.CHAT.route + "/${state.profile.conversationID}" + "/${state.profile.id}")
                                                    }
                                            )
                                        }

                                        else -> {}
                                    }

                                    Spacer(modifier = Modifier.size(15.dp))
                                    Image(
                                        painter = painterResource(Res.drawable.ic_more_black),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                            .clickable {
                                                scope.launch {
                                                    scaffoldState.bottomSheetState.expand()
                                                }
                                            }
                                    )
                                }

                            }

                        }

                        item {
                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        item {
                            Text(
                                text = state.profile.firstName,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                maxLines = 2
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        item {
                            Row {
                                Image(
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(20.dp).clip(CircleShape),
                                    painter = painterResource(Res.drawable.ic_location_black),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.size(5.dp))

                                Text(
                                    text = "${state.profile.country}, ${state.profile.city}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 2
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        item {
                            Text(
                                text = state.profile.description,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 2
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        items(postsState.chunked(3)) { item ->

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                item.forEachIndexed { index, postModel ->
                                    ImagePost(screenSize.value.first, item[index].postID) {
                                        navController.navigate(NavRoutes.POST.route + "/${item[index].postID}" + "/${myProfileState?.id}")
                                    }
                                    if (postModel != item.last()) {
                                        Spacer(modifier = Modifier.size(3.dp))
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.size(3.dp))

                        }

                        item {
                            Spacer(modifier = Modifier.size(40.dp))
                        }

                    }
                    Layout(
                        modifier = Modifier.fillMaxWidth().height(0.dp),
                        measurePolicy = { measurables, constraints ->

                            val width = constraints.maxWidth
                            val height = constraints.maxHeight

                            screenSize.value = Pair(width, height)
                            println("Width: $width, height: $height")

                            layout(width, height) {

                            }
                        }
                    )
                }
            }

            is ProfilePreviewUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

}
