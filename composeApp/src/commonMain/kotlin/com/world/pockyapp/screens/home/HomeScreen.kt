package com.world.pockyapp.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.home.navigations.conversations.ChatScreen
import com.world.pockyapp.screens.home.navigations.discover.DiscoverScreen
import com.world.pockyapp.screens.home.navigations.profile.ProfileScreen
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_chat_black
import pockyapp.composeapp.generated.resources.ic_discover_black
import pockyapp.composeapp.generated.resources.ic_profile_black
import pockyapp.composeapp.generated.resources.ic_search_black


@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = koinViewModel()) {

    var selected by remember {
        mutableIntStateOf(viewModel.selectedScreen)
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.onPrimary)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)
        ) {
            /*Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier.size(35.dp).clip(CircleShape).clickable {

                },
                contentDescription = null,
            )

            Spacer(modifier = Modifier.size(20.dp))*/

            Image(
                painter = painterResource(Res.drawable.ic_search_black),
                modifier = Modifier.size(25.dp).clickable {
                    navController.navigate(NavRoutes.SEARCH.route)
                },
                contentDescription = null,
            )
        }


        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
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

                    BottomNavigationItem(
                        selected = selected == 2,
                        onClick = {
                            selected = 2
                            viewModel.selectedScreen = 2
                        },
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_profile_black),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        ) {

            Column() {

                Spacer(modifier = Modifier.size(20.dp))
                when(selected){
                    0->{ DiscoverScreen(navController) }
                    1->{ ChatScreen(navController) }
                    2->{ ProfileScreen(navController) }
                }


            }


        }


    }


}
