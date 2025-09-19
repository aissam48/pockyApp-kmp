package com.world.pockyapp.screens.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.world.pockyapp.Constant
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController, viewModel: SearchViewModel = koinViewModel()) {

    val search = remember { mutableStateOf("") }
    val users = viewModel.profilesState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    painter = painterResource(Res.drawable.ic_back_black),
                    contentDescription = "Back"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Search",
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Search Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                OutlinedTextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    value = search.value,
                    onValueChange = {
                        search.value = it
                        viewModel.search(it)
                    },
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
                            text = "Search for people...",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Results Section
            val usersList = users.value ?: emptyList()

            if (search.value.isNotEmpty() && usersList.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Color.Gray.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 32.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No users found",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Try searching with a different keyword",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else if (search.value.isEmpty()) {
                // Initial State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Color(0xFFDFC46B).copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👋",
                                fontSize = 32.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Find people",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Search for friends and connect with them",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Results Header
                Text(
                    text = "Results",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Search Results
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(usersList) { user ->
                        ModernUserItem(
                            user = user,
                            onClick = {
                                navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${user.id}")
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ModernUserItem(
    user: ProfileModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                AsyncImage(
                    model = getUrl(user.photoID),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "@${user.username}",
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    maxLines = 1
                )

                if (user.city.isNotEmpty() && user.country.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${user.city}, ${user.country}",
                        color = Color.Gray.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            // Arrow indicator
            Text(
                text = "›",
                color = Color.Gray,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}