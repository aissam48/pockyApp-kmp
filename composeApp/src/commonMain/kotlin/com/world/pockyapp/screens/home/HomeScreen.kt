package com.world.pockyapp.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.screens.google_maps.GoogleMapsScreen
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.home.navigations.conversations.ChatScreen
import com.world.pockyapp.screens.home.navigations.discover.DiscoverScreen
import com.world.pockyapp.screens.home.navigations.hot.HotScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_chat_black
import pockyapp.composeapp.generated.resources.ic_discover_black
import pockyapp.composeapp.generated.resources.ic_hot_black
import pockyapp.composeapp.generated.resources.ic_profile_black
import pockyapp.composeapp.generated.resources.ic_search_black
import pockyapp.composeapp.generated.resources.icon_world


@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = koinViewModel()) {

    var selected by remember {
        mutableIntStateOf(viewModel.selectedScreen)
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    scope.launch {
        snackbarHostState.showSnackbar(
            message = "test",
            actionLabel = "Dismiss",
            duration = SnackbarDuration.Short
        )
    }
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.White)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp, start = 10.dp, end = 10.dp)
        ) {

            Row() {
                Text(
                    "NearVibe",
                    fontSize = 25.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDFC46B)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    Image(
                        painter = painterResource(Res.drawable.ic_profile_black),
                        modifier = Modifier.size(33.dp).clickable {
                            navController.navigate(NavRoutes.MY_PROFILE.route)
                        },
                        contentDescription = null,
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(Res.drawable.ic_search_black),
                        modifier = Modifier.size(28.dp).clickable {
                            navController.navigate(NavRoutes.SEARCH.route)
                        },
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

        }

        Scaffold(
            bottomBar = {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 24.dp),
                    shadowElevation = 8.dp,
                    tonalElevation = 8.dp,
                    modifier = Modifier.padding(20.dp)
                ) {
                    NavigationBar(
                        modifier = Modifier.height(60.dp),
                        containerColor = Color(0xFFDFC46B),
                        contentColor = Color.Yellow
                    )
                    {
                        BottomNavigationItem(
                            selected = selected == 0,
                            onClick = {
                                selected = 0
                                viewModel.selectedScreen = 0
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_discover_black),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        )
                        BottomNavigationItem(
                            selected = selected == 1,
                            onClick = {
                                selected = 1
                                viewModel.selectedScreen = 1
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_hot_black),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        )

                        BottomNavigationItem(
                            selected = selected == 2,
                            onClick = {
                                selected = 2
                                viewModel.selectedScreen = 2
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_world),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        )

                        BottomNavigationItem(
                            selected = selected == 3,
                            onClick = {
                                selected = 3
                                viewModel.selectedScreen = 3
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_chat_black),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        )

                    }
                }

            }
        ) {

            Column() {
                when (selected) {
                    0 -> {
                        DiscoverScreen(navController)
                    }

                    1 -> {
                        HotScreen(navController)
                    }

                    2 -> {
                        GoogleMapsScreen(navController)
                    }

                    3 -> {
                        ChatScreen(navController)
                    }
                }
            }

        }
    }
}
