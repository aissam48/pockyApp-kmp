package com.world.pockyapp.screens.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.screens.google_maps.MapComponentScreen
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
import pockyapp.composeapp.generated.resources.nearvibe_logo

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = koinViewModel()) {

    var selected by remember {
        mutableIntStateOf(viewModel.selectedScreen)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
    ) {
        // Modern Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 0.dp
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFDFC46B).copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.nearvibe_logo),
                            contentDescription = "logo"
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "NearVibe",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        fontSize = 24.sp,
                    )
                }

                // Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFF8F9FA),
                                shape = CircleShape
                            )
                            .clickable {
                                navController.navigate(NavRoutes.SEARCH.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_search_black),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Search",
                        )
                    }

                    // Profile Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFDFC46B),
                                shape = CircleShape
                            )
                            .clickable {
                                navController.navigate(NavRoutes.MY_PROFILE.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_profile_black),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Profile",
                        )
                    }
                }
            }
        }

        Scaffold(
            backgroundColor = Color.Transparent,
            bottomBar = {
                // Modern Bottom Navigation
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 1.dp,
                    tonalElevation = 0.dp,
                ) {
                    NavigationBar(
                        modifier = Modifier.height(70.dp),
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        tonalElevation = 0.dp
                    ) {
                        // Discover
                        NavigationBarItem(
                            selected = selected == 0,
                            onClick = {
                                selected = 0
                                viewModel.selectedScreen = 0
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = if (selected == 0)
                                                Color(0xFFDFC46B)
                                            else
                                                Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_discover_black),
                                        contentDescription = "Discover",
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected == 0) Color.White else Color.Gray
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )

                        // Hot
                        NavigationBarItem(
                            selected = selected == 1,
                            onClick = {
                                selected = 1
                                viewModel.selectedScreen = 1
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = if (selected == 1)
                                                Color(0xFFDFC46B)
                                            else
                                                Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_hot_black),
                                        contentDescription = "Hot",
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected == 1) Color.White else Color.Gray
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )

                        // World/Map
                        NavigationBarItem(
                            selected = selected == 2,
                            onClick = {
                                selected = 2
                                viewModel.selectedScreen = 2
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = if (selected == 2)
                                                Color(0xFFDFC46B)
                                            else
                                                Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.icon_world),
                                        contentDescription = "Map",
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected == 2) Color.White else Color.Gray
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )

                        // Chat
                        NavigationBarItem(
                            selected = selected == 3,
                            onClick = {
                                selected = 3
                                viewModel.selectedScreen = 3
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = if (selected == 3)
                                                Color(0xFFDFC46B)
                                            else
                                                Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_chat_black),
                                        contentDescription = "Chat",
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected == 3) Color.White else Color.Gray
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (selected) {
                    0 -> DiscoverScreen(navController)
                    1 -> HotScreen(navController)
                    2 -> MapComponentScreen(navController)
                    3 -> ChatScreen(navController)
                }
            }
        }
    }
}