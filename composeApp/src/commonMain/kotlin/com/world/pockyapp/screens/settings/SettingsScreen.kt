package com.world.pockyapp.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    // Modern color scheme
    val backgroundColor = Color(0xFFFFFFFF)
    val cardBackground = Color.White
    val primaryGold = Color(0xFFDFC46B)
    val textPrimary = Color(0xFF212529)
    val textSecondary = Color(0xFF6C757D)
    val dangerColor = Color(0xFFE53E3E)

    LaunchedEffect(logout.value) {
        if (logout.value == "logout") {
            navController.navigate(NavRoutes.SPLASH.route) {
                popUpTo(NavRoutes.SPLASH.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(deleteAccount.value) {
        if (deleteAccount.value == "deleteAccount") {
            navController.navigate(NavRoutes.LOGIN.route) {
                popUpTo(NavRoutes.LOGIN.route) { inclusive = true }
            }
        }
    }

    if (showDialogLogout) {
        ModernConfirmDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            confirmText = "Logout",
            onConfirm = {
                showDialogLogout = false
                viewModel.logout()
            },
            onDismiss = { showDialogLogout = false }
        )
    }

    if (showDialogDeleteAccount) {
        ModernConfirmDialog(
            title = "Delete Account",
            message = "Are you sure you want to permanently delete your account? This action cannot be undone.",
            confirmText = "Delete",
            onConfirm = {
                showDialogDeleteAccount = false
                viewModel.deleteAccount()
            },
            onDismiss = { showDialogDeleteAccount = false },
            isDestructive = true
        )
    }

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Modern Header
            item {
                ModernHeader(
                    title = "Settings",
                    onBackClick = { navController.popBackStack() },
                    textColor = textPrimary
                )
            }

            // Profile Section
            item {
                ModernSettingsSection(
                    title = "Profile",
                    backgroundColor = cardBackground
                ) {
                    ModernSettingsItem(
                        icon = "👤",
                        title = "Edit Profile",
                        subtitle = "Name, bio, photo",
                        onClick = { navController.navigate(NavRoutes.EDIT_PROFILE.route) }
                    )

                    ModernSettingsItem(
                        icon = "🔐",
                        title = "Change Password",
                        subtitle = "Update your password",
                        onClick = { navController.navigate(NavRoutes.CHANGE_PASSWORD.route) }
                    )

                    ModernSettingsItem(
                        icon = "📍",
                        title = "Location Settings",
                        subtitle = "Update your location",
                        onClick = { navController.navigate(NavRoutes.EDIT_LOCATION.route) },
                        showDivider = false
                    )
                }
            }

            // Account Control Section - NEW
            item {
                ModernSettingsSection(
                    title = "Account Control",
                    backgroundColor = cardBackground
                ) {

                    ModernSettingsItem(
                        icon = "\uD83D\uDDFA\uFE0F",
                        title = "Control Zone",
                        subtitle = "Control see moments and posts around you",
                        onClick = {
                            //navController.navigate("${NavRoutes.ACCOUNT_CONTROL.route}/followers")
                        }
                    )

                    ModernSettingsItem(
                        icon = "👥",
                        title = "Follower Visibility",
                        subtitle = "Control who can see your followers",
                        onClick = {
                            //navController.navigate("${NavRoutes.ACCOUNT_CONTROL.route}/followers")
                        }
                    )

                    ModernSettingsItem(
                        icon = "📢",
                        title = "Activity Status",
                        subtitle = "Show when you're active",
                        onClick = {
                            //navController.navigate("${NavRoutes.ACCOUNT_CONTROL.route}/activity")
                        }
                    )

                    ModernSettingsItem(
                        icon = "🔔",
                        title = "Notification Controls",
                        subtitle = "Manage notification preferences",
                        onClick = {
                            //navController.navigate(
                            //"${
                            //NavRoutes.ACCOUNT_CONTROL.route}/notifications")
                        },
                        showDivider = false
                    )
                }
            }

            // Privacy & Safety Section
            item {
                ModernSettingsSection(
                    title = "Privacy & Safety",
                    backgroundColor = cardBackground
                ) {
                    ModernSettingsItem(
                        icon = "🚫",
                        title = "Blocked Users",
                        subtitle = "Manage blocked accounts",
                        onClick = { navController.navigate(NavRoutes.BLOCKED.route) }
                    )

                    ModernSettingsItem(
                        icon = "🛡️",
                        title = "Safety Settings",
                        subtitle = "Content filtering and safety",
                        onClick = { /* Navigate to safety settings */ }
                    )

                    ModernSettingsItem(
                        icon = "📋",
                        title = "Data & Privacy",
                        subtitle = "Your data and privacy controls",
                        onClick = { /* Navigate to data privacy */ },
                        showDivider = false
                    )
                }
            }

            // Support Section
            item {
                ModernSettingsSection(
                    title = "Support",
                    backgroundColor = cardBackground
                ) {
                    ModernSettingsItem(
                        icon = "❓",
                        title = "Help Center",
                        subtitle = "Get help and support",
                        onClick = { /* Navigate to help */ }
                    )

                    ModernSettingsItem(
                        icon = "📞",
                        title = "Contact Us",
                        subtitle = "Reach out to our team",
                        onClick = { /* Navigate to contact */ }
                    )

                    ModernSettingsItem(
                        icon = "⭐",
                        title = "Rate App",
                        subtitle = "Share your feedback",
                        onClick = { /* Navigate to rate app */ },
                        showDivider = false
                    )
                }
            }

            // Account Actions Section
            item {
                ModernSettingsSection(
                    title = "Account",
                    backgroundColor = cardBackground
                ) {
                    ModernSettingsItem(
                        icon = "🚪",
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        onClick = { showDialogLogout = true },
                        showDivider = false
                    )
                }
            }

            // Danger Zone
            item {
                ModernDangerSection(
                    backgroundColor = Color(0xFFFFF5F5),
                    textColor = dangerColor
                ) {
                    ModernSettingsItem(
                        icon = "⚠️",
                        title = "Delete Account",
                        subtitle = "Permanently delete your account",
                        onClick = { showDialogDeleteAccount = true },
                        textColor = dangerColor,
                        showDivider = false
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun ModernHeader(
    title: String,
    onBackClick: () -> Unit,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ModernSettingsSection(
    title: String,
    backgroundColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF6C757D),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(content = content)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ModernDangerSection(
    backgroundColor: Color,
    textColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = "Danger Zone",
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = textColor.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(content = content)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ModernSettingsItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = Color(0xFF212529),
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Modern Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = if (textColor == Color(0xFF212529)) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFDFC46B).copy(alpha = 0.1f),
                                    Color(0xFFDFC46B).copy(alpha = 0.05f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    textColor.copy(alpha = 0.1f),
                                    textColor.copy(alpha = 0.05f)
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
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

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = if (textColor == Color(0xFF212529))
                        Color(0xFF6C757D)
                    else
                        textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Modern Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFF6C757D),
                modifier = Modifier.size(20.dp)
            )
        }

        // Divider
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 20.dp)
                    .background(Color(0xFFE9ECEF))
            )
        }
    }
}

@Composable
private fun ModernConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212529)
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFF6C757D),
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDestructive) Color(0xFFE53E3E) else Color(0xFFDFC46B)
                )
            ) {
                Text(
                    text = confirmText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF6C757D)
                )
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

// Account Control Screen for Follower Visibility
@Composable
fun AccountControlScreen(
    navController: NavHostController,
    controlType: String // "followers", "visibility", "activity", "notifications"
) {
    val backgroundColor = Color(0xFFF8F9FA)
    val cardBackground = Color.White
    val primaryGold = Color(0xFFDFC46B)
    val textPrimary = Color(0xFF212529)
    val textSecondary = Color(0xFF6C757D)

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                ModernHeader(
                    title = when (controlType) {
                        "followers" -> "Follower Visibility"
                        "visibility" -> "Profile Visibility"
                        "activity" -> "Activity Status"
                        "notifications" -> "Notifications"
                        else -> "Account Control"
                    },
                    onBackClick = { navController.popBackStack() },
                    textColor = textPrimary
                )
            }

            when (controlType) {
                "followers" -> {
                    item {
                        FollowerVisibilityControls(
                            backgroundColor = cardBackground,
                            primaryColor = primaryGold,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
                // Add other control types as needed
            }
        }
    }
}

@Composable
private fun FollowerVisibilityControls(
    backgroundColor: Color,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    var selectedOption by remember { mutableStateOf("everyone") }

    ModernSettingsSection(
        title = "Who can see your followers",
        backgroundColor = backgroundColor
    ) {
        Column {
            VisibilityOption(
                title = "Everyone",
                subtitle = "Anyone can see who follows you",
                isSelected = selectedOption == "everyone",
                onClick = { selectedOption = "everyone" },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Friends Only",
                subtitle = "Only your friends can see your followers",
                isSelected = selectedOption == "friends",
                onClick = { selectedOption = "friends" },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Nobody",
                subtitle = "Hide your followers from everyone",
                isSelected = selectedOption == "nobody",
                onClick = { selectedOption = "nobody" },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor,
                showDivider = false
            )
        }
    }
}

@Composable
private fun VisibilityOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    primaryColor: Color,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = textSecondary,
                    fontSize = 14.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = primaryColor,
                    unselectedColor = textSecondary
                )
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 20.dp)
                    .background(Color(0xFFE9ECEF))
            )
        }
    }
}