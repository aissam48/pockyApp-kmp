package com.world.pockyapp.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.world.pockyapp.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_edit_black

@Composable
fun SettingsScreen(navController: NavHostController) {


    Scaffold(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp, top = 15.dp)
        ) {

            item {

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
                        text = "Settings",
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(50.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
                        .height(50.dp).padding(start = 15.dp)
                        .clickable {
                            navController.navigate(NavRoutes.EDIT_PROFILE.route)
                        }) {
                    Text(
                        text = "Edit profile",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(15.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
                        .height(50.dp).padding(start = 15.dp)
                        .clickable {
                            navController.navigate(NavRoutes.CHANGE_PASSWORD.route)
                        }) {
                    Text(
                        text = "Password",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(15.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
                        .height(50.dp).padding(start = 15.dp)
                        .clickable {
                            navController.navigate(NavRoutes.EDIT_LOCATION.route)
                        }) {
                    Text(
                        text = "Location",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(15.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
                        .height(50.dp).padding(start = 15.dp)
                        .clickable {

                        }) {
                    Text(
                        text = "Delete account",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

        }
    }


}