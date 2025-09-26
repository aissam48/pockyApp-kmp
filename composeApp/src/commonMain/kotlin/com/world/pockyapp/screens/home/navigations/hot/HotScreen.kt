package com.world.pockyapp.screens.home.navigations.hot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.screens.profile.convertPxToDp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HotScreen(navController: NavHostController, viewModel: HotViewModel = koinViewModel()) {


    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }

    Scaffold(modifier = Modifier.fillMaxSize()) {

        LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {


            item {

                Spacer(modifier = Modifier.height(50.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .size((convertPxToDp(screenSize.value.first) - 50).dp)
                            .align(Alignment.Center)
                            .rotate(5f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black
                        )
                    ) {

                    }

                    Card(
                        modifier = Modifier
                            .size((convertPxToDp(screenSize.value.first) - 50).dp)
                            .align(Alignment.Center)
                            .rotate(-5f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDFC46B)
                        )
                    ) {

                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Ready for a new adventure?",
                                color = Color.White,
                                fontSize = 40.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Challenge around work, have fun and win",
                                color = Color.Black,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "You have a challenge in your mind ? post it and let people try it",
                                color = Color.Black,
                                fontSize = 18.sp
                            )

                        }

                    }
                }

            }

            item {
                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().height(55.dp).padding(horizontal = 20.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    colors = CardDefaults.cardColors(contentColor = Color.Black)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().background(color = Color(0xFF000000)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Post your challenge",
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f).padding(start = 20.dp)
                        )

                        Image(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Explore challenges",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 20.dp)
                )

            }

        }

        Layout(
            modifier = Modifier.fillMaxWidth().height(0.dp),
            measurePolicy = { measurables, constraints ->
                val width = constraints.maxWidth
                val height = constraints.maxHeight
                screenSize.value = Pair(width, height)
                layout(width, height) {}
            }
        )
    }


}
