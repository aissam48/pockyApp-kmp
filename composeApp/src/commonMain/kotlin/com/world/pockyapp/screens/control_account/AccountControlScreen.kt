package com.world.pockyapp.screens.control_account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.world.pockyapp.screens.settings.ModernHeader


@Composable
fun AccountControlScreen(navController: NavHostController) {
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
                    title = "Follower Visibility",
                    onBackClick = { navController.popBackStack() },
                    textColor = textPrimary
                )
            }

            item {
                FollowerVisibilityControls(
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }
    }
}


@Composable
fun FollowerVisibilityControls(
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
fun ModernSettingsSection(
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