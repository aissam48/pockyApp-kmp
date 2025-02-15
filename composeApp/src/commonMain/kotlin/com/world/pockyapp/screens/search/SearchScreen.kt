package com.world.pockyapp.screens.search

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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

@Composable
fun SearchScreen(navController: NavHostController, viewModel: SearchViewModel = koinViewModel()) {

    val search = remember {
        mutableStateOf("")
    }

    val users = viewModel.profilesState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

            Spacer(modifier = Modifier.padding(top = 20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier.size(23.dp).clickable {
                        navController.popBackStack()
                    },
                    painter = painterResource(Res.drawable.ic_back_black),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    text = "Search",
                    color = Color.Black,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.padding(top = 25.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(),
                value = search.value,
                onValueChange = {
                    search.value = it
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        if (it.isEmpty())
                            return@launch
                        viewModel.search(it)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                ),
                label = {
                    androidx.compose.material3.Text(
                        text = "Search...",
                        color = Color.White
                    )
                }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                items(users.value ?: emptyList()) { item: ProfileModel ->

                    Column(modifier = Modifier.clickable {
                        navController.navigate(NavRoutes.PROFILE_PREVIEW.route + "/${item.id}")
                    }) {
                        Row(
                            modifier = Modifier.height(70.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = getUrl(item.photoID),
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
                                        text = "${item.firstName} ${item.lastName}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.size(5.dp))

                                    Text(
                                        text = item.username,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))

                    }

                }

            }

        }

    }

}