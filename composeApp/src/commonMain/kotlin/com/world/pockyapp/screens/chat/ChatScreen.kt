package com.world.pockyapp.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.MessageModel
import com.world.pockyapp.screens.components.CustomDialog
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.ic_send_bleu
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    conversationID: String,
    profileID: String,
    chatRequestID: String,
    viewModel: ChatViewModel = koinViewModel()
) {

    val message = remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<MessageModel>() }

    val newMessages by viewModel.messagesState.collectAsState()
    val newMessageState by viewModel.newMessageState.collectAsState()
    val me by viewModel.myProfileState.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val cancelConversation by viewModel.cancelConversationState.collectAsState()

    viewModel.conversationID = conversationID

    LaunchedEffect(newMessages) {
        messages.addAll(newMessages)
    }

    LaunchedEffect(Unit) {
        viewModel.getMyProfile()
        viewModel.getProfile(profileID)
        viewModel.getMessages(conversationID)
    }

    LaunchedEffect(newMessageState) {
        newMessageState?.let { messages.add(0, it) }
    }

    // Add scroll state and coroutine scope
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    var showDialogResult by remember { mutableStateOf(false) }

    val title = remember {
        mutableStateOf("")
    }

    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "Cancel",
            onCancel = {
                showDialog = false
            }
        )
    }

    if (showDialogResult) {
        CustomDialog(
            title = "Are you sure you want to cancel this chat?",
            action1 = "No",
            action2 = "Yes",
            onCancel = { showDialogResult = false },
            onDelete = {
                showDialogResult = false
                viewModel.cancelConversation(conversationID, chatRequestID)
            }
        )
    }
    LaunchedEffect(cancelConversation) {
        when (val state = cancelConversation) {
            is CancelChatUiState.Loading -> {

            }

            is CancelChatUiState.Success -> {
                showDialog = true
                title.value = "This chat has canceled successfully"
            }

            is CancelChatUiState.Error -> {
                showDialog = true
                title.value = state.error.message
            }

            is CancelChatUiState.Idle -> {}
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(23.dp)
                        .clickable { navController.popBackStack() },
                    painter = painterResource(Res.drawable.ic_back_black),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(15.dp))
                AsyncImage(
                    model = getUrl(profile?.photoID),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder),
                )
                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    text = "${profile?.firstName} ${profile?.lastName}",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${profile?.id}")
                    }
                )
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.width(70.dp).height(30.dp)
                        .background(color = Color.Red, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            showDialogResult = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel chat",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
                reverseLayout = true
            ) {
                items(messages) { item: MessageModel ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (item.senderID == profileID) Color(0XFFA3B288)
                                    else Color(0XFF8EB288),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp).align(
                                    if (item.senderID != profileID) Alignment.End
                                    else Alignment.Start
                                ).widthIn(min = 80.dp, max = 300.dp)
                        ) {
                            Text(item.content, color = Color.Black, fontSize = 15.sp)
                        }
                        Text(
                            formatCreatedAt(item.createdAt),
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.align(
                                if (item.senderID != profileID) Alignment.End
                                else Alignment.Start
                            )
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                    }
                }
            }

            // Message input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedTextField(
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(0.80f),
                    value = message.value,
                    onValueChange = { message.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = {
                        androidx.compose.material3.Text(
                            text = "Tap message...",
                            color = Color.Gray
                        )
                    }
                )

                Image(
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            if (message.value.isNotBlank()) {
                                val data = MessageModel(
                                    content = message.value,
                                    conversationID = conversationID,
                                    senderID = me?.id ?: ""
                                )
                                //messages.add(0, data)
                                viewModel.sendMessage(data)
                                message.value = ""
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        },
                    painter = painterResource(Res.drawable.ic_send_bleu),
                    contentDescription = null
                )
            }
        }
    }
}