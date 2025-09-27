package com.world.pockyapp.screens.challenges.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.screens.components.ModernHeader
import org.jetbrains.compose.resources.painterResource
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder

data class ChallengeCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

data class DifficultyLevel(
    val id: String,
    val name: String,
    val color: Color,
    val description: String
)

@Composable
fun CreateChallengeScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }
    var timeLimit by remember { mutableStateOf("") }
    var prize by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ChallengeCategory?>(null) }
    var selectedDifficulty by remember { mutableStateOf<DifficultyLevel?>(null) }
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val categories = remember {
        listOf(
            ChallengeCategory("fitness", "Fitness", Icons.Default.MoreVert, Color(0xFF4CAF50)),
            ChallengeCategory("strength", "Strength", Icons.Default.MoreVert, Color(0xFFFF5722)),
            ChallengeCategory("mental", "Mental", Icons.Default.MoreVert, Color(0xFF9C27B0)),
            ChallengeCategory("food", "Food", Icons.Default.MoreVert, Color(0xFFFF9800)),
            ChallengeCategory("creative", "Creative", Icons.Default.MoreVert, Color(0xFF2196F3)),
            ChallengeCategory("dance", "Dance", Icons.Default.MoreVert, Color(0xFFE91E63)),
            ChallengeCategory("skill", "Skill", Icons.Default.MoreVert, Color(0xFF607D8B))
        )
    }

    val difficulties = remember {
        listOf(
            DifficultyLevel("easy", "Easy", Color(0xFF4CAF50), "Perfect for beginners"),
            DifficultyLevel("medium", "Medium", Color(0xFFFF9800), "Moderate challenge level"),
            DifficultyLevel("hard", "Hard", Color(0xFFFF5722), "Requires experience"),
            DifficultyLevel("extreme", "Extreme", Color(0xFF9C27B0), "For experts only"),
            DifficultyLevel("spicy", "Spicy", Color(0xFFFFC107), "Hot challenge!")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header

        item{
            ModernHeader(""){
                navController.popBackStack()
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFDFC46B),
                                    Color(0xFFF4D03F)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Create Challenge",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Share your challenge with the world",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Video Upload Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Demo Video",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedVideoUri != null) {
                        // Video Preview
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Video Selected",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )

                                // Change video button
                                Button(
                                    onClick = { selectedVideoUri = null },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDFC46B)
                                    )
                                ) {
                                    Text("Change", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        // Upload Area
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { selectedVideoUri = "demo_video" },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFDFC46B).copy(alpha = 0.1f)
                            ),
                            border = BorderStroke(2.dp, Color(0xFFDFC46B).copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                    tint = Color(0xFFDFC46B),
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Upload Demo Video",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFDFC46B)
                                )

                                Text(
                                    text = "Show others how it's done",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Challenge Details
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Challenge Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Title Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Challenge Title") },
                        placeholder = { Text("e.g., Run 1km in 4 minutes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFDFC46B),
                            focusedLabelColor = Color(0xFFDFC46B)
                        )
                    )

                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe your challenge in detail...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFDFC46B),
                            focusedLabelColor = Color(0xFFDFC46B)
                        )
                    )

                    // Rules Input
                    OutlinedTextField(
                        value = rules,
                        onValueChange = { rules = it },
                        label = { Text("Rules & Requirements") },
                        placeholder = { Text("What are the rules? Any specific requirements?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFDFC46B),
                            focusedLabelColor = Color(0xFFDFC46B)
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Time Limit
                        OutlinedTextField(
                            value = timeLimit,
                            onValueChange = { timeLimit = it },
                            label = { Text("Time Limit") },
                            placeholder = { Text("30 seconds") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDFC46B),
                                focusedLabelColor = Color(0xFFDFC46B)
                            )
                        )

                        // Prize
                        OutlinedTextField(
                            value = prize,
                            onValueChange = { prize = it },
                            label = { Text("Prize") },
                            placeholder = { Text("100 coins") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDFC46B),
                                focusedLabelColor = Color(0xFFDFC46B)
                            )
                        )
                    }
                }
            }
        }

        // Category Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Category",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = selectedCategory?.id == category.id,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }
        }

        // Difficulty Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Difficulty Level",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        difficulties.forEach { difficulty ->
                            DifficultyOption(
                                difficulty = difficulty,
                                isSelected = selectedDifficulty?.id == difficulty.id,
                                onClick = { selectedDifficulty = difficulty }
                            )
                        }
                    }
                }
            }
        }

        // Create Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable {
                        if (validateForm(
                                title,
                                description,
                                selectedCategory,
                                selectedDifficulty
                            )
                        ) {
                            isLoading = true
                            // Handle challenge creation
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFDFC46B),
                                    Color(0xFFF4D03F)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Create Challenge",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun CategoryChip(
    category: ChallengeCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) category.color else Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) category.color else category.color.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else category.color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else category.color
            )
        }
    }
}

@Composable
fun DifficultyOption(
    difficulty: DifficultyLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) difficulty.color.copy(alpha = 0.1f) else Color.Gray.copy(
                alpha = 0.05f
            )
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) difficulty.color else Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = difficulty.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) difficulty.color else Color.Black
                )
                Text(
                    text = difficulty.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = difficulty.color
                )
            )
        }
    }
}

fun validateForm(
    title: String,
    description: String,
    category: ChallengeCategory?,
    difficulty: DifficultyLevel?
): Boolean {
    return title.isNotBlank() &&
            description.isNotBlank() &&
            category != null &&
            difficulty != null
}