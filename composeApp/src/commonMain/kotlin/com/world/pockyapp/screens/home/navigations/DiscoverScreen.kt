package com.world.pockyapp.screens.home.navigations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform

@Composable
fun DiscoverScreen(navController: NavHostController) {

    LazyColumn(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {

        item {

            Text(
                text = "Friends",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }
        item {
            LazyRow {

                items(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) { item: Int ->

                    Row(modifier = Modifier.height(70.dp).width(70.dp)) {
                        Image(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp).clip(CircleShape),
                            painter = painterResource(Res.drawable.compose_multiplatform),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }

            }
        }

        item {
            Spacer(modifier = Modifier.size(15.dp))
        }
        item {

            Text(
                text = "Nearby moments >",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }
        item {
            LazyRow {

                items(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) { item: Int ->

                    Row {
                        Card(
                            backgroundColor = Color.LightGray,
                            modifier = Modifier.height(150.dp).width(90.dp)
                                .background(color = Color.Yellow)
                        ) {
                            Image(
                                contentScale = ContentScale.FillHeight,
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = null
                            )
                            Column {

                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))

                    }


                }

            }
        }
        item {
            Spacer(modifier = Modifier.size(15.dp))
        }
        item {

            Text(
                text = "Nearby posts >",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
        }

        items(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) { item: Int ->

            Column {
                Card(
                    backgroundColor = Color.LightGray,
                    elevation = 0.dp,
                    modifier = Modifier.height(400.dp).fillParentMaxWidth()
                        .background(color = Color.Yellow)
                ) {
                    Image(
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )
                    Column {

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