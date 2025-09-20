package com.world.pockyapp.screens.chat

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.MessageModel
import com.world.pockyapp.screens.components.CustomDialog
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_placeholder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
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

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showDialogResult by remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }

    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "OK",
            onCancel = { showDialog = false }
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
            is CancelChatUiState.Loading -> {}
            is CancelChatUiState.Success -> {
                showDialog = true
                title.value = "This chat has been cancelled successfully"
            }
            is CancelChatUiState.Error -> {
                showDialog = true
                title.value = state.error.message
            }
            is CancelChatUiState.Idle -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFF8F9FA),
                                CircleShape
                            )
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(Res.drawable.ic_back_black),
                            contentDescription = "Back"
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Profile Section
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${profile?.id}")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.Gray.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            AsyncImage(
                                model = getUrl(profile?.photoID),
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                placeholder = painterResource(Res.drawable.ic_placeholder),
                                error = painterResource(Res.drawable.ic_placeholder),
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Cancel Chat Button
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFEF4444),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { showDialogResult = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "End Chat",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Messages Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF8F9FA))
                    .padding(horizontal = 16.dp),
                state = listState,
                reverseLayout = true,
            ) {
                items(messages) { messageItem ->
                    MessageBubble(
                        message = messageItem,
                        isFromMe = messageItem.senderID != profileID,
                        myProfile = me,
                        otherProfile = profile,
                        onProfileClick = { userId ->
                            navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/$userId")
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Modern Message Input
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        OutlinedTextField(
                            value = message.value,
                            onValueChange = { message.value = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                backgroundColor = Color.Transparent
                            ),
                            placeholder = {
                                Text(
                                    "Type a message...",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (message.value.isNotBlank()) Color(0xFFDFC46B) else Color.Gray.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .clickable(enabled = message.value.isNotBlank()) {
                                if (message.value.isNotBlank()) {
                                    val data = MessageModel(
                                        content = message.value,
                                        conversationID = conversationID,
                                        senderID = me?.id ?: "",
                                        id = Uuid.random().toString()
                                    )
                                    viewModel.sendMessage(data)
                                    message.value = ""
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            }
                            .shadow(if (message.value.isNotBlank()) 4.dp else 0.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = "Send Message",
                            tint = if (message.value.isNotBlank()) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: MessageModel,
    isFromMe: Boolean,
    myProfile: Any?,
    otherProfile: Any?,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (isFromMe) {
            // My message (right side)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Card(
                    modifier = Modifier.widthIn(min = 80.dp, max = 280.dp),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 4.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDFC46B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = message.content,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatCreatedAt(message.createdAt),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        } else {
            // Other person's message (left side)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Card(
                    modifier = Modifier.widthIn(min = 80.dp, max = 280.dp),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 20.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = message.content,
                        color = Color.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatCreatedAt(message.createdAt),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}