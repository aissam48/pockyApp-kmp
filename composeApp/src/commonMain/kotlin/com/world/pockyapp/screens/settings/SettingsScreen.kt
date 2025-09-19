package com.world.pockyapp.screens.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.popBackStack()
                            },
                        painter = painterResource(Res.drawable.ic_back_black),
                        contentDescription = "Back"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Settings",
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }

            // Profile Settings Section
            item {
                Text(
                    text = "Profile",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = "👤",
                            title = "Edit Profile",
                            subtitle = "Name, bio, photo",
                            onClick = { navController.navigate(NavRoutes.EDIT_PROFILE.route) }
                        )

                        SettingsDivider()

                        SettingsItem(
                            icon = "🔐",
                            title = "Password",
                            subtitle = "Change your password",
                            onClick = { navController.navigate(NavRoutes.CHANGE_PASSWORD.route) }
                        )

                        SettingsDivider()

                        SettingsItem(
                            icon = "📍",
                            title = "Location",
                            subtitle = "Update your location",
                            onClick = { navController.navigate(NavRoutes.EDIT_LOCATION.route) },
                            isLast = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Privacy Section
            item {
                Text(
                    text = "Privacy & Safety",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    SettingsItem(
                        icon = "🚫",
                        title = "Blocked Users",
                        subtitle = "Manage blocked accounts",
                        onClick = { navController.navigate(NavRoutes.BLOCKED.route) },
                        isLast = true
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Account Section
            item {
                Text(
                    text = "Account",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    SettingsItem(
                        icon = "🚪",
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        onClick = { showDialogLogout = true },
                        isLast = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Danger Zone
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    SettingsItem(
                        icon = "⚠️",
                        title = "Delete Account",
                        subtitle = "Permanently delete your account",
                        onClick = { showDialogDeleteAccount = true },
                        textColor = Color(0xFFE53E3E),
                        isLast = true
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = Color.Black,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (textColor == Color.Black)
                        Color(0xFFDFC46B).copy(alpha = 0.1f)
                    else
                        Color(0xFFFFEBEE),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = if (textColor == Color.Black) Color.Gray else textColor.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Arrow Icon
        Text(
            text = "›",
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
            .background(Color.Gray.copy(alpha = 0.1f))
    )
}