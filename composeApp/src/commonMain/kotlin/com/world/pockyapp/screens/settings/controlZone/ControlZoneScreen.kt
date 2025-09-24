package com.world.pockyapp.screens.settings.controlZone

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.math.roundToInt

data class ControlZoneSettings(
    val discoverabilityRange: Float = 5.0f, // kilometers
    val momentsVisibilityRange: Float = 2.0f,
    val postsVisibilityRange: Float = 10.0f,
    val friendsDiscoveryRange: Float = 1.0f,
    val isLocationSharingEnabled: Boolean = true,
    val isPreciseLocationEnabled: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlZoneScreen(
    navController: NavHostController
) {
    var controlSettings by remember { mutableStateOf(ControlZoneSettings()) }

    // Modern color scheme
    val backgroundColor = Color(0xFFF8F9FA)
    val cardBackground = Color.White
    val primaryGold = Color(0xFFDFC46B)
    val textPrimary = Color(0xFF212529)
    val textSecondary = Color(0xFF6C757D)
    val dangerColor = Color(0xFFE53E3E)
    val successColor = Color(0xFF28A745)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            ControlZoneTopBar(
                title = "Control Zone",
                onBackClick = { navController.popBackStack() },
                textColor = textPrimary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Zone Overview Card
            item {
                ZoneOverviewCard(
                    settings = controlSettings,
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Location Master Switch
            item {
                LocationMasterSwitch(
                    isEnabled = controlSettings.isLocationSharingEnabled,
                    onToggle = {
                        controlSettings = controlSettings.copy(isLocationSharingEnabled = it)
                    },
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    dangerColor = dangerColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Discoverability Range
            item {
                RangeControlCard(
                    title = "Discoverability Range",
                    subtitle = "How far people can discover your profile",
                    icon = "🔍",
                    currentValue = controlSettings.discoverabilityRange,
                    maxValue = 50f,
                    onValueChange = {
                        controlSettings = controlSettings.copy(discoverabilityRange = it)
                    },
                    enabled = controlSettings.isLocationSharingEnabled,
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Moments Visibility Range
            item {
                RangeControlCard(
                    title = "Moments Visibility",
                    subtitle = "Range for location-based moments sharing",
                    icon = "📸",
                    currentValue = controlSettings.momentsVisibilityRange,
                    maxValue = 25f,
                    onValueChange = {
                        controlSettings = controlSettings.copy(momentsVisibilityRange = it)
                    },
                    enabled = controlSettings.isLocationSharingEnabled,
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Posts Visibility Range
            item {
                RangeControlCard(
                    title = "Posts Visibility",
                    subtitle = "Range for location-based posts sharing",
                    icon = "📝",
                    currentValue = controlSettings.postsVisibilityRange,
                    maxValue = 100f,
                    onValueChange = {
                        controlSettings = controlSettings.copy(postsVisibilityRange = it)
                    },
                    enabled = controlSettings.isLocationSharingEnabled,
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Friends Discovery Range
            item {
                RangeControlCard(
                    title = "Friends Discovery",
                    subtitle = "Range for nearby friends notifications",
                    icon = "👥",
                    currentValue = controlSettings.friendsDiscoveryRange,
                    maxValue = 10f,
                    onValueChange = {
                        controlSettings = controlSettings.copy(friendsDiscoveryRange = it)
                    },
                    enabled = controlSettings.isLocationSharingEnabled,
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Advanced Settings
            item {
                AdvancedLocationSettings(
                    isPreciseLocationEnabled = controlSettings.isPreciseLocationEnabled,
                    onPreciseLocationToggle = {
                        controlSettings = controlSettings.copy(isPreciseLocationEnabled = it)
                    },
                    enabled = controlSettings.isLocationSharingEnabled,
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    dangerColor = dangerColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Privacy Tips
            item {
                PrivacyTipsCard(
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ControlZoneTopBar(
    title: String,
    onBackClick: () -> Unit,
    textColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Manage your location privacy",
                    fontSize = 14.sp,
                    color = Color(0xFF6C757D)
                )
            }
        }
    }
}

@Composable
private fun ZoneOverviewCard(
    settings: ControlZoneSettings,
    backgroundColor: Color,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛡️",
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Your Control Zone",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = if (settings.isLocationSharingEnabled) "Active" else "Disabled",
                        fontSize = 14.sp,
                        color = if (settings.isLocationSharingEnabled) Color(0xFF28A745) else Color(0xFFE53E3E),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStat(
                    value = "${settings.discoverabilityRange.roundToInt()}km",
                    label = "Discovery",
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                QuickStat(
                    value = "${settings.momentsVisibilityRange.roundToInt()}km",
                    label = "Moments",
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                QuickStat(
                    value = "${settings.postsVisibilityRange.roundToInt()}km",
                    label = "Posts",
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }
    }
}

@Composable
private fun QuickStat(
    value: String,
    label: String,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = textSecondary
        )
    }
}

@Composable
private fun LocationMasterSwitch(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    backgroundColor: Color,
    primaryColor: Color,
    dangerColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) backgroundColor else Color(0xFFFFF5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isEnabled) primaryColor.copy(alpha = 0.1f) else dangerColor.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnabled) "📍" else "🚫",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Location Sharing",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = if (isEnabled)
                        "Location-based features are active"
                    else
                        "All location features are disabled",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = primaryColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = dangerColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun RangeControlCard(
    title: String,
    subtitle: String,
    icon: String,
    currentValue: Float,
    maxValue: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean,
    backgroundColor: Color,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val progress = currentValue / maxValue
    val animatedProgress by animateFloatAsState(
        targetValue = if (enabled) progress else 0f,
        animationSpec = tween(1000)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (enabled) 4.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .background(
                            if (enabled) primaryColor.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) textPrimary else textSecondary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }

                Text(
                    text = "${currentValue.roundToInt()} km",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) primaryColor else textSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Progress Bar with Kilometers Scale
            KilometerProgressBar(
                progress = animatedProgress,
                currentValue = currentValue,
                maxValue = maxValue,
                enabled = enabled,
                primaryColor = primaryColor,
                textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Slider
            Slider(
                value = currentValue,
                onValueChange = onValueChange,
                valueRange = 0f..maxValue,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = if (enabled) primaryColor else textSecondary,
                    activeTrackColor = if (enabled) primaryColor else textSecondary,
                    inactiveTrackColor = Color(0xFFE9ECEF)
                )
            )

            // Range labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0 km",
                    fontSize = 12.sp,
                    color = textSecondary
                )
                Text(
                    text = "${maxValue.roundToInt()} km",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        }
    }
}

@Composable
private fun KilometerProgressBar(
    progress: Float,
    currentValue: Float,
    maxValue: Float,
    enabled: Boolean,
    primaryColor: Color,
    textSecondary: Color
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Background track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    Color(0xFFE9ECEF),
                    RoundedCornerShape(6.dp)
                )
        )

        // Progress track with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(12.dp)
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.7f),
                                primaryColor
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                textSecondary.copy(alpha = 0.3f),
                                textSecondary.copy(alpha = 0.3f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(6.dp)
                )
        )

        // Distance markers
        val markers = listOf(0.25f, 0.5f, 0.75f)
        markers.forEach { markerPosition ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(markerPosition)
                    .height(12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(12.dp)
                        .background(Color.White)
                )
            }
        }

        // Current position indicator
        if (enabled && progress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(primaryColor, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun AdvancedLocationSettings(
    isPreciseLocationEnabled: Boolean,
    onPreciseLocationToggle: (Boolean) -> Unit,
    enabled: Boolean,
    backgroundColor: Color,
    primaryColor: Color,
    dangerColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Advanced Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) textPrimary else textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Precise Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (enabled) textPrimary else textSecondary
                    )
                    Text(
                        text = "Share exact coordinates instead of approximate area",
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }

                Switch(
                    checked = isPreciseLocationEnabled && enabled,
                    onCheckedChange = onPreciseLocationToggle,
                    enabled = enabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = dangerColor,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = textSecondary.copy(alpha = 0.3f)
                    )
                )
            }

            if (isPreciseLocationEnabled && enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = dangerColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚠️ Precise location sharing may compromise your privacy",
                        fontSize = 12.sp,
                        color = dangerColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyTipsCard(
    backgroundColor: Color,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💡", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Privacy Tips",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val tips = listOf(
                "Lower ranges provide better privacy protection",
                "Disable precise location for general area sharing",
                "Review your settings regularly",
                "Consider your safety when adjusting ranges"
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "•",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }
            }
        }
    }
}