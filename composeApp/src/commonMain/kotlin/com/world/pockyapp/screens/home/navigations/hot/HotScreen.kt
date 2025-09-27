package com.world.pockyapp.screens.home.navigations.hot

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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import org.koin.compose.viewmodel.koinViewModel

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val participants: Int,
    val timeLeft: String,
    val difficulty: String,
    val prize: String,
    val author: String
)

@Composable
fun HotScreen(navController: NavHostController, viewModel: HotViewModel = koinViewModel()) {
    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }

    // Sample data - replace with your actual data
    val challenges = remember {
        listOf(
            Challenge("1", "Run 1km in 4 minutes", "Challenge yourself to complete a 1km run in under 4 minutes", "Fitness", 45, "2d left", "Hard", "🏆 100 coins", "@speedrunner"),
            Challenge("2", "Push a car 50 meters", "Physical strength challenge - push a standard car for 50 meters", "Strength", 23, "5h left", "Extreme", "🎁 Prize pack", "@strongman"),
            Challenge("3", "Solve 100 math problems", "Speed math challenge - solve 100 arithmetic problems in 10 minutes", "Mental", 78, "1d left", "Medium", "🧠 Brain trophy", "@mathwiz"),
            Challenge("4", "Eat 20 hot wings", "Spicy food challenge - finish 20 hot wings in 15 minutes", "Food", 12, "3h left", "Spicy", "🌶️ Fire medal", "@spicylover")
        )
    }

    val categories = listOf("All", "Fitness", "Strength", "Mental", "Food", "Creative")
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B).copy(alpha = 0.3f))
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
                                    text = "🔥 Hot Challenges",
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
                                    Badge(
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
                                    }
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

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                onClick = { selectedCategory.value = category },
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
                    text = "🔥 Trending Challenges",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            items(challenges.chunked(2)) { challengePair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    challengePair.forEach { challenge ->
                        ChallengeCard(
                            challenge = challenge,
                            modifier = Modifier.weight(1f),
                            onClick = { /* Handle challenge click */ }
                        )
                    }
                    // If odd number of challenges, add spacer
                    if (challengePair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
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
    challenge: Challenge,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Difficulty badge
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                        color = Color.White
                    )
                }

                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Challenge title
            Text(
                text = challenge.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Challenge description
            Text(
                text = challenge.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Stats row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Participants",
                        tint = Color(0xFFDFC46B),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${challenge.participants}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        //imageVector = Icons.Default.Timer,
                        imageVector = Icons.Default.Star,
                        contentDescription = "Time",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = challenge.timeLeft,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Prize
            Text(
                text = challenge.prize,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFDFC46B)
            )

            // Author
            Text(
                text = "by ${challenge.author}",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}