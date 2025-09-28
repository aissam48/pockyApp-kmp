package com.world.pockyapp.screens.create_challenge

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.world.pockyapp.screens.components.ModernHeader
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.icon_play

@Composable
actual fun CreateChallengeScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ChallengeCategory?>(null) }
    var selectedDifficulty by remember { mutableStateOf<DifficultyLevel?>(null) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType>(DialogType.Success) }
    var dialogMessage by remember { mutableStateOf("") }

    val viewModel: CreateChallengeViewModel = koinViewModel()
    val context = LocalContext.current
    val createChallengeState = viewModel.createChallengeState.collectAsState()

    // ExoPlayer setup
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    val categories = remember {
        listOf(
            ChallengeCategory("fitness", "Fitness", Color(0xFF4CAF50)),
            ChallengeCategory("strength", "Strength", Color(0xFF4CAF50)),
            ChallengeCategory("mental", "Mental", Color(0xFF4CAF50)),
            ChallengeCategory("food", "Food", Color(0xFF4CAF50)),
            ChallengeCategory("creative", "Creative", Color(0xFF4CAF50)),
            ChallengeCategory("dance", "Dance", Color(0xFF4CAF50)),
            ChallengeCategory("skill", "Skill", Color(0xFF4CAF50)),
            ChallengeCategory("other", "Other", Color(0xFF4CAF50))
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

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedVideoUri = it
            print("url of video -> ${it.path}")

            // Set up ExoPlayer with the selected video
            val mediaItem = MediaItem.fromUri(it)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()

            // Convert URI to ByteArray
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                viewModel.video = inputStream?.readBytes()
                print("size of video -> ${viewModel.video?.size}")
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                dialogType = DialogType.Error
                dialogMessage = "Failed to process video: ${e.message}"
                showDialog = true
            }
        }
    }

    // Handle create challenge state changes
    LaunchedEffect(createChallengeState.value) {
        when (createChallengeState.value) {
            is ResponseState.Success -> {
                dialogType = DialogType.Success
                dialogMessage = "Challenge created successfully!"
                showDialog = true
            }
            is ResponseState.Error -> {
                dialogType = DialogType.Error
                dialogMessage = "Failed to create challenge. Please try again."
                showDialog = true
            }
            else -> {}
        }
    }

    // Dispose ExoPlayer when composable is removed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            ModernHeader("") {
                navController.popBackStack()
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
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
                    }
                }
            }
        }

        // Video Upload Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Video",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        if (selectedVideoUri != null) {
                            IconButton(
                                onClick = {
                                    selectedVideoUri = null
                                    exoPlayer.stop()
                                    exoPlayer.clearMediaItems()
                                    viewModel.video = null
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove video",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedVideoUri != null) {
                        // Video Preview with ExoPlayer
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            AndroidView(
                                factory = { context ->
                                    PlayerView(context).apply {
                                        player = exoPlayer
                                        useController = true
                                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { videoPickerLauncher.launch("video/*") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDFC46B).copy(alpha = 0.2f),
                                contentColor = Color(0xFFDFC46B)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Change Video")
                        }
                    } else {
                        // Upload Area
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    videoPickerLauncher.launch("video/*")
                                },
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
                                    painter = painterResource(Res.drawable.icon_play),
                                    contentDescription = null,
                                    tint = Color(0xFFDFC46B),
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Upload Video",
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
                    .padding(horizontal = 10.dp),
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
                        onValueChange = {
                            title = it
                            viewModel.title = title
                        },
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
                        onValueChange = {
                            description = it
                            viewModel.description = description
                        },
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
                        onValueChange = {
                            rules = it
                            viewModel.rules = rules
                        },
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
                }
            }
        }

        // Category Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
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
                                onClick = {
                                    selectedCategory = category
                                    viewModel.category = category.name
                                }
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
                    .padding(horizontal = 10.dp),
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
                                onClick = {
                                    selectedDifficulty = difficulty
                                    viewModel.difficulty = difficulty.name
                                }
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
                    .padding(horizontal = 10.dp)
                    .clickable {
                        if (validateForm(title, description, selectedCategory, selectedDifficulty, selectedVideoUri)) {
                            viewModel.createChallenge()
                        } else {
                            dialogType = DialogType.Error
                            dialogMessage = "Please fill in all required fields and upload a video"
                            showDialog = true
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
                    if (createChallengeState.value == ResponseState.Loading) {
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
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = when (dialogType) {
                        DialogType.Success -> Icons.Default.Check
                        DialogType.Error -> Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = when (dialogType) {
                        DialogType.Success -> Color(0xFF4CAF50)
                        DialogType.Error -> Color(0xFFFF5722)
                    },
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = when (dialogType) {
                        DialogType.Success -> "Success!"
                        DialogType.Error -> "Error"
                    },
                    fontWeight = FontWeight.Bold,
                    color = when (dialogType) {
                        DialogType.Success -> Color(0xFF4CAF50)
                        DialogType.Error -> Color(0xFFFF5722)
                    }
                )
            },
            text = {
                Text(
                    text = dialogMessage,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        if (dialogType == DialogType.Success) {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(
                        text = if (dialogType == DialogType.Success) "Continue" else "OK",
                        color = Color(0xFFDFC46B),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

private fun validateForm(
    title: String,
    description: String,
    category: ChallengeCategory?,
    difficulty: DifficultyLevel?,
    videoUri: Uri?
): Boolean {
    return title.isNotBlank() &&
            description.isNotBlank() &&
            category != null &&
            difficulty != null &&
            videoUri != null
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

enum class DialogType {
    Success,
    Error
}

data class ChallengeCategory(
    val id: String,
    val name: String,
    val color: Color
)

data class DifficultyLevel(
    val id: String,
    val name: String,
    val color: Color,
    val description: String
)