package com.world.pockyapp.screens.settings.controlAccount

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.network.models.model.ResponseMessageModel
import com.world.pockyapp.screens.components.ModernHeader
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ControlAccountScreen(
    navController: NavHostController,
    viewModel: ControlAccountViewModel = koinViewModel()
) {
    val backgroundColor = Color(0xFFF8F9FA)
    val cardBackground = Color.White
    val primaryGold = Color(0xFFDFC46B)
    val textPrimary = Color(0xFF212529)
    val textSecondary = Color(0xFF6C757D)

    val followersVisibilityState = viewModel.followersVisibilityStateFlow.collectAsState()
    val followingsVisibilityState = viewModel.followingsVisibilityStateFlow.collectAsState()
    val friendsVisibilityState = viewModel.friendsVisibilityStateFlow.collectAsState()
    val profileState = viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getProfile()
    }

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            item {
                ModernHeader("Your Network Visibility"){
                    navController.popBackStack()
                }
            }

            item {
                FollowerVisibilityControls(
                    backgroundColor = cardBackground,
                    primaryColor = primaryGold,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    followersVisibilityState = followersVisibilityState,
                    followingsVisibilityState = followingsVisibilityState,
                    friendsVisibilityState = friendsVisibilityState,
                    profileState = profileState,
                    viewModel = viewModel
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
    textSecondary: Color,
    friendsVisibilityState: State<ResponseState<ResponseMessageModel>>,
    followersVisibilityState: State<ResponseState<ResponseMessageModel>>,
    followingsVisibilityState: State<ResponseState<ResponseMessageModel>>,
    profileState: State<ResponseState<ProfileModel>>,
    viewModel: ControlAccountViewModel
) {
    var selectedOptionFriends by remember { mutableStateOf("everyone") }
    var selectedOptionFollowers by remember { mutableStateOf("everyone") }
    var selectedOptionFollowings by remember { mutableStateOf("everyone") }


    when (val state = profileState.value) {
        is ResponseState.Success -> {
            selectedOptionFriends = state.data.friendsVisibility.lowercase()
            selectedOptionFollowers = state.data.followersVisibility.lowercase()
            selectedOptionFollowings = state.data.followingsVisibility.lowercase()
        }

        else -> {

        }
    }


    ModernSettingsSection(
        title = "Who can see your friends",
        backgroundColor = backgroundColor
    )
    {
        Column {
            VisibilityOption(
                title = "Everyone",
                subtitle = "Anyone can see who follows you",
                isSelected = selectedOptionFriends == "everyone",
                onClick = {
                    selectedOptionFriends = "everyone"
                    viewModel.friendsVisibility("EVERYONE")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Friends Only",
                subtitle = "Only your friends can see your followers",
                isSelected = selectedOptionFriends == "friends",
                onClick = {
                    selectedOptionFriends = "friends"
                    viewModel.friendsVisibility("FRIENDS")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Nobody",
                subtitle = "Hide your followers from everyone",
                isSelected = selectedOptionFriends == "nobody",
                onClick = {
                    selectedOptionFriends = "nobody"
                    viewModel.friendsVisibility("NOBODY")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor,
                showDivider = false
            )
        }
    }

    ModernSettingsSection(
        title = "Who can see your followers",
        backgroundColor = backgroundColor
    )
    {
        Column {
            VisibilityOption(
                title = "Everyone",
                subtitle = "Anyone can see who follows you",
                isSelected = selectedOptionFollowers == "everyone",
                onClick = {
                    selectedOptionFollowers = "everyone"
                    viewModel.followersVisibility("EVERYONE")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Friends Only",
                subtitle = "Only your friends can see your followers",
                isSelected = selectedOptionFollowers == "friends",
                onClick = {
                    selectedOptionFollowers = "friends"
                    viewModel.followersVisibility("FRIENDS")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Nobody",
                subtitle = "Hide your followers from everyone",
                isSelected = selectedOptionFollowers == "nobody",
                onClick = {
                    selectedOptionFollowers = "nobody"
                    viewModel.followersVisibility("NOBODY")

                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor,
                showDivider = false
            )
        }
    }

    ModernSettingsSection(
        title = "Who can see your followings",
        backgroundColor = backgroundColor
    )
    {
        Column {
            VisibilityOption(
                title = "Everyone",
                subtitle = "Anyone can see who follows you",
                isSelected = selectedOptionFollowings == "everyone",
                onClick = {
                    selectedOptionFollowings = "everyone"
                    viewModel.followingsVisibility("EVERYONE")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Friends Only",
                subtitle = "Only your friends can see your followers",
                isSelected = selectedOptionFollowings == "friends",
                onClick = {
                    selectedOptionFollowings = "friends"
                    viewModel.followingsVisibility("FRIENDS")
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryColor = primaryColor
            )

            VisibilityOption(
                title = "Nobody",
                subtitle = "Hide your followers from everyone",
                isSelected = selectedOptionFollowings == "nobody",
                onClick = {
                    selectedOptionFollowings = "nobody"
                    viewModel.followingsVisibility("NOBODY")
                },
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