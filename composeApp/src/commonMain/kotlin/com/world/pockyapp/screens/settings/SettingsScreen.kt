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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.components.CustomDialog
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = koinViewModel()
) {

    val logout = viewModel.logoutState.collectAsState()
    val deleteAccount = viewModel.deleteAccountState.collectAsState()

    var showDialogLogout by remember { mutableStateOf(false) }
    var showDialogDeleteAccount by remember { mutableStateOf(false) }

    LaunchedEffect(logout.value) {
        if (logout.value == "logout") {
            navController.navigate(NavRoutes.SPLASH.route) {
                popUpTo(NavRoutes.SPLASH.route) {
                    inclusive = true
                }
            }

        }
    }
    LaunchedEffect(deleteAccount.value) {
        if (deleteAccount.value == "deleteAccount") {
            navController.navigate(NavRoutes.LOGIN.route) {
                popUpTo(NavRoutes.LOGIN.route) {
                    inclusive = true
                }
            }

        }
    }

    if (showDialogLogout) {
        CustomDialog(
            title = "Are you sure you want to logout?",
            action1 = "Cancel",
            action2 = "Logout",
            onCancel = { showDialogLogout = false },
            onDelete = {
                showDialogLogout = false
                viewModel.logout()
            }
        )
    }

    if (showDialogDeleteAccount) {
        CustomDialog(
            title = "Are you sure you want to delete your account?",
            action1 = "Cancel",
            action2 = "Delete",
            onCancel = { showDialogDeleteAccount = false },
            onDelete = {
                showDialogDeleteAccount = false
                viewModel.deleteAccount()
            }
        )
    }

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
                        .height(50.dp)
                        .clickable {
                            navController.navigate(NavRoutes.EDIT_PROFILE.route)
                        }.padding(start = 15.dp)) {
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
                        .height(50.dp)
                        .clickable {
                            navController.navigate(NavRoutes.CHANGE_PASSWORD.route)
                        }.padding(start = 15.dp)) {
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
                        .height(50.dp)
                        .clickable {
                            navController.navigate(NavRoutes.EDIT_LOCATION.route)
                        }.padding(start = 15.dp)) {
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
                        .height(50.dp)
                        .clickable {
                            navController.navigate(NavRoutes.BLOCKED.route)
                        }.padding(start = 15.dp)) {
                    Text(
                        text = "Blocked",
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
                        .height(50.dp)
                        .clickable {
                            //viewModel.logout()
                            showDialogLogout = true
                        }.padding(start = 15.dp)) {
                    Text(
                        text = "Logout",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(35.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color.Red, shape = RoundedCornerShape(15.dp))
                        .height(50.dp)
                        .clickable {
                            showDialogDeleteAccount = true
                            //viewModel.deleteAccount()
                        }.padding(start = 15.dp)) {
                    Text(
                        text = "Delete account",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

        }
    }


}