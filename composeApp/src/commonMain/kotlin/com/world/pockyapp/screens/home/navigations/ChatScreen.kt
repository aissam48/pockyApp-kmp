package com.world.pockyapp.screens.home.navigations

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform

@Composable
fun ChatScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {

        item {

            Text(
                text = "Chat's requests",
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

                    Row(modifier = Modifier.height(120.dp).width(100.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(75.dp).clip(CircleShape),
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(5.dp))

                            Text(
                                text = "Aissam elboudi",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                maxLines = 2
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

        items(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) { item: Int ->

            Column {
                Row(
                    modifier = Modifier.height(70.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(70.dp).clip(CircleShape),
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(10.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                text = "Aissam elboudi",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.size(5.dp))

                            Text(
                                text = "last message",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }

                        Text(
                            text = "23:21",
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