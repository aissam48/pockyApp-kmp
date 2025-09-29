package com.world.pockyapp.screens.home.navigations.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ChallengeModel
import com.world.pockyapp.screens.challengeDetails.ChallengeDetailsViewModel
import com.world.pockyapp.screens.home.navigations.discover.DiscoverTheme
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import com.world.pockyapp.utils.Utils.formatCreatedAt
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder

@Composable
fun ChallengesScreen(
    navController: NavHostController,
    viewModel: ChallengesViewModel = koinViewModel()
) {
    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }
    var selectedTab by remember { mutableStateOf(ChallengesScreenTab.Friends) }


    val challengeDetailsViewModel : ChallengeDetailsViewModel = koinViewModel()
    val challengesState = viewModel.challengesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.relationType = "Friends"
        viewModel.category = "All"
        viewModel.loadChallenges()
    }

    val categories = listOf("All", "Fitness", "Strength", "Mental", "Food", "Creative", "Other")
    val selectedCategory = remember { mutableStateOf("All") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFFFFFF)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header Section with Hero Card
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Hero Challenge Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    // Background decorative cards
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .offset(x = 8.dp, y = 8.dp)
                            .rotate(3f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f))
                    ) {}

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .offset(x = (-4).dp, y = 4.dp)
                            .rotate(-2f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6B6B).copy(
                                alpha = 0.3f
                            )
                        )
                    ) {}

                    // Main hero card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFDFC46B),
                                            Color(0xFFF4D03F)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Challenge the world and have some fun ",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Push your limits, compete with others",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    /*Badge(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                        contentColor = Color.White
                                    ) {
                                        Text("${challenges.size} Active", fontSize = 12.sp)
                                    }

                                    Badge(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                        contentColor = Color.White
                                    ) {
                                        Text("158 Players", fontSize = 12.sp)
                                    }*/
                                }
                            }
                        }
                    }
                }
            }

            // Post Challenge Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable {
                            navController.navigate(NavRoutes.CREATE_CHALLENGE.route)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                                .padding(6.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Post Your Challenge",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Create something epic for others to try",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Arrow",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Category Filter
            item {
                Column {
                    Text(
                        text = "Browse by Category",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ChallengeModernTabSelector(
                        selectedTab = selectedTab,
                        onTabSelected = {
                            selectedTab = it
                            viewModel.relationType = it.title
                            viewModel.loadChallenges()
                        }
                    )


                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                onClick = {
                                    selectedCategory.value = category
                                    viewModel.category = category
                                    viewModel.loadChallenges()
                                },
                                label = { Text(category) },
                                selected = selectedCategory.value == category,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFDFC46B),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Challenges Grid
            item {
                Text(
                    text = "Challenges",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            when (val state = challengesState.value) {
                is ResponseState.Loading -> {

                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {

                            CircularProgressIndicator()
                        }
                    }
                }

                is ResponseState.Success -> {
                    val challenges = state.data
                    items(challenges) { challenge ->
                        ChallengeCard(
                            challenge = challenge,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onClick = {
                                challengeDetailsViewModel.challengeId = challenge.id

                                navController.navigate(NavRoutes.CHALLENGE_DETAILS.route)
                            }
                        )
                    }
                }

                is ResponseState.Error -> {

                }

                is ResponseState.Idle -> {

                }
            }


            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Layout(
            modifier = Modifier.fillMaxWidth().height(0.dp),
            measurePolicy = { _, constraints ->
                val width = constraints.maxWidth
                val height = constraints.maxHeight
                screenSize.value = Pair(width, height)
                layout(width, height) {}
            }
        )
    }
}

@Composable
fun ChallengeCard(
    challenge: ChallengeModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = challenge.description,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Difficulty badge
                Badge(
                    containerColor = when (challenge.difficulty) {
                        "Easy" -> Color(0xFF4CAF50)
                        "Medium" -> Color(0xFFFF9800)
                        "Hard" -> Color(0xFFFF5722)
                        "Extreme" -> Color(0xFF9C27B0)
                        else -> Color(0xFFFFC107)
                    }
                ) {
                    Text(
                        text = challenge.difficulty,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                // Participants
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Participants",
                        tint = Color(0xFFDFC46B),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${challenge.participants} Accepted this challenge",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Time left
                Text(
                    text = formatCreatedAt(challenge.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = challenge.profile.photoUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {

                        },
                    contentDescription = null,
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "${challenge.profile.firstName} ${challenge.profile.lastName}",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "@${challenge.profile.username}",
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }

            }
        }
    }
}

enum class ChallengesScreenTab(val title: String, val icon: ImageVector) {
    Friends("Friends", Icons.Outlined.Face),
    Following("Following", Icons.Outlined.Notifications),
    Nearby("Nearby", Icons.Outlined.LocationOn),
    Global("Global", Icons.Outlined.MoreVert)
}

@Composable
fun ChallengeModernTabSelector(
    selectedTab: ChallengesScreenTab,
    onTabSelected: (ChallengesScreenTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiscoverTheme.SpacingMedium),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DiscoverTheme.SurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            ChallengesScreenTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) DiscoverTheme.Surface else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DiscoverTheme.SpacingXSmall)
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) DiscoverTheme.Primary else DiscoverTheme.OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = tab.title,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) DiscoverTheme.Primary else DiscoverTheme.OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}