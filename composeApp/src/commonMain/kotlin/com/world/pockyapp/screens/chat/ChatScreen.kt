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
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant
import com.world.pockyapp.network.models.model.MessageModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_send_bleu
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    conversationID: String,
    profileID: String,
    viewModel: ChatViewModel = koinViewModel()
) {

    val message = remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<MessageModel>() }

    val newMessages by viewModel.messagesState.collectAsState()
    val newMessageState by viewModel.newMessageState.collectAsState()
    val me by viewModel.myProfileState.collectAsState()
    val profile by viewModel.profileState.collectAsState()

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
                if (profile?.photoID?.isEmpty() == true) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )
                } else {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape),
                        painter = rememberAsyncImagePainter("http://${Constant.BASE_URL}:3000/api/v1/stream/media/${profile?.photoID}"),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    text = "${profile?.firstName} ${profile?.lastName}",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
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
                            .widthIn(min = 100.dp, max = 300.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (item.senderID == profileID) Color.LightGray else Color.Gray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(item.content, color = Color.Black, fontSize = 15.sp)
                        }
                        Text("1 min", color = Color.LightGray, fontSize = 12.sp)
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
                            color = MaterialTheme.colorScheme.onPrimary
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