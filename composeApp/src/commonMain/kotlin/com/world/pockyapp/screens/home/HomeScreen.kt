package com.world.pockyapp.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.home.navigations.conversations.ChatScreen
import com.world.pockyapp.screens.home.navigations.discover.DiscoverScreen
import com.world.pockyapp.screens.profile.ProfileScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_chat_black
import pockyapp.composeapp.generated.resources.ic_discover_black
import pockyapp.composeapp.generated.resources.ic_profile_black
import pockyapp.composeapp.generated.resources.ic_search_black


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
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.onPrimary)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 15.dp, start = 10.dp, end = 10.dp)
        ) {

            Text(
                "PockyApp",
                fontSize = 25.sp,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
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
            Spacer(modifier = Modifier.size(20.dp))

        }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0XFFFAF9F6),
                    contentColor = Color.Yellow
                ) {
                    BottomNavigationItem(
                        selected = selected == 0,
                        onClick = {
                            selected = 0
                            viewModel.selectedScreen = 0
                        },
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_discover_black),
                                contentDescription = null
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
                                painter = painterResource(Res.drawable.ic_chat_black),
                                contentDescription = null
                            )
                        }
                    )

                }
            }
        ) {

            Column() {
                when (selected) {
                    0 -> {
                        DiscoverScreen(navController)
                    }

                    1 -> {
                        ChatScreen(navController)
                    }
                }
            }

        }
    }
}
