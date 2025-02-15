package com.world.pockyapp.screens.home.navigations.conversations

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ChatRequestModel
import com.world.pockyapp.network.models.model.ConversationModel
import com.world.pockyapp.screens.home.navigations.conversations.UIState
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_placeholder

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    viewModel: ConversationsViewModel = koinViewModel()
) {

    val chatRequestsMoments by viewModel.chatRequestsMoments.collectAsState()
    val conversationsState by viewModel.conversationsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
        delay(500)
        viewModel.loadChatRequests()
    }

    LazyColumn(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {

        item {
            when (chatRequestsMoments) {
                is UIState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UIState.Error -> {
                    Text(
                        text = "Failed to load chat requests.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
                is UIState.Success -> {
                    val chatRequests = (chatRequestsMoments as UIState.Success<List<ChatRequestModel>>).data
                    if (chatRequests.isNotEmpty()) {
                        Text(
                            text = "Chat's requests",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.size(10.dp))

                        LazyRow {
                            items(chatRequests) { item: ChatRequestModel ->

                                Row(modifier = Modifier.height(120.dp).width(100.dp).clickable {
                                    navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${item.sendProfile.id}")
                                }) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        AsyncImage(
                                            model = getUrl(item.sendProfile.photoID),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(75.dp).clip(CircleShape),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )
                                        Spacer(modifier = Modifier.size(5.dp))

                                        Text(
                                            text = "${item.sendProfile.firstName} ${item.sendProfile.lastName}",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 13.sp,
                                            maxLines = 2,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(15.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(15.dp))
        }

        item {
            Text(
                text = "Chats",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        item {
            Spacer(modifier = Modifier.size(10.dp))
        }

        when (conversationsState) {
            is UIState.Loading -> {

                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is UIState.Error -> {
                item {
                    Text(
                        text = "Failed to load conversations.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
            is UIState.Success -> {
                val conversations = (conversationsState as UIState.Success<List<ConversationModel>>).data
                items(conversations) { item: ConversationModel ->

                    Column(modifier = Modifier.clickable {
                        navController.navigate(NavRoutes.CHAT.route + "/${item.id}" + "/${item.profile.id}" + "/${item.chatRequestID}")
                    }) {
                        Row(
                            modifier = Modifier.height(70.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = getUrl(item.profile.photoID),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(70.dp).clip(CircleShape),
                                placeholder = painterResource(Res.drawable.ic_placeholder),
                                error = painterResource(Res.drawable.ic_placeholder),
                            )
                            Spacer(modifier = Modifier.size(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${item.profile.firstName} ${item.profile.lastName}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.size(5.dp))

                                    Text(
                                        text = item.lastMessage?.content ?: "",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        maxLines = 2
                                    )
                                }

                                Text(
                                    text = formatCreatedAt(item.lastMessage?.createdAt),
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraLight,
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}
