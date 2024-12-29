package com.world.pockyapp.screens.home.navigations.conversations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.world.pockyapp.utils.Utils.formatCreatedAt
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform

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

            if (chatRequestsMoments.isNotEmpty()){
                Text(
                    text = "Chat's requests",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

        }
        item {
            if (chatRequestsMoments.isNotEmpty()){
                Spacer(modifier = Modifier.size(10.dp))
            }

        }
        item {
            LazyRow {

                items(chatRequestsMoments) { item: ChatRequestModel ->

                    Row(modifier = Modifier.height(120.dp).width(100.dp).clickable {
                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${item.sendProfile.id}")
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = getUrl(item.sendProfile.photoID),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(75.dp).clip(CircleShape),
                                placeholder = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(5.dp))

                            Text(
                                text = "${item.sendProfile.firstName} ${item.sendProfile.lastName}",
                                color = MaterialTheme.colorScheme.primary,
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

        item {
            Spacer(modifier = Modifier.size(15.dp))
        }
        item {

            Text(
                text = "Chats",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }

        items(conversationsState) { item: ConversationModel ->

            Column(modifier = Modifier.clickable {
                navController.navigate(NavRoutes.CHAT.route + "/${item.id}" + "/${item.profile.id}" + "/${item.chatRequestID}")

            }) {
                Row(
                    modifier = Modifier.height(70.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = getUrl(item.profile.photoID),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(70.dp).clip(CircleShape),
                        placeholder = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
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
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.size(5.dp))

                            Text(
                                text = item.lastMessage?.content ?: "",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }

                        Text(
                            text = formatCreatedAt(item.lastMessage?.createdAt),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }


                }
                Spacer(modifier = Modifier.size(10.dp))

            }

        }
        item {
            Spacer(modifier = Modifier.size(80.dp))
        }
    }

}