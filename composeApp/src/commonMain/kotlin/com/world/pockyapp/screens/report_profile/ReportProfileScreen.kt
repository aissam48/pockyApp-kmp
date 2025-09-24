package com.world.pockyapp.screens.report_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.screens.profile_preview.ProfilePreviewUiState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_placeholder
import pockyapp.composeapp.generated.resources.ic_report_black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProfileScreen(
    navController: NavHostController,
    id: String,
    viewModel: ReportProfileViewModel = koinViewModel()
) {

    val profileState by viewModel.profileState.collectAsState()
    val reportProfileState by viewModel.reportProfileState.collectAsState()

    val result = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val profile = remember { mutableStateOf(ProfileModel()) }
    val content = remember { mutableStateOf("") }

    if (showDialog.value) {
        CustomDialogSuccess(
            title = result.value,
            action = "OK",
            onCancel = {
                showDialog.value = false
                content.value = ""
                navController.popBackStack()
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getProfile(id = id)
    }

    LaunchedEffect(reportProfileState) {
        when (val state = reportProfileState) {
            is ReportProfileState.Loading -> {}
            is ReportProfileState.Success -> {
                showDialog.value = true
                result.value = state.message
            }

            is ReportProfileState.Error -> {
                showDialog.value = true
                result.value = "Failed to send report. Please try again."
            }

            is ReportProfileState.Idle -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFFFFFFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFF8F9FA),
                            CircleShape
                        )
                        .clickable {
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_back_black),
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Report User",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (val state = profileState) {
                is ProfilePreviewUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFDFC46B),
                            strokeWidth = 3.dp
                        )
                    }
                }

                is ProfilePreviewUiState.Success -> {
                    profile.value = state.profile

                    // User Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Photo
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        Color.Gray.copy(alpha = 0.1f),
                                        CircleShape
                                    )
                            ) {
                                AsyncImage(
                                    model = getUrl(profile.value.photoID),
                                    contentDescription = "Profile Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    placeholder = painterResource(Res.drawable.ic_placeholder),
                                    error = painterResource(Res.drawable.ic_placeholder),
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // User Details
                            Text(
                                text = "${profile.value.firstName} ${profile.value.lastName}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Text(
                                text = "@${profile.value.username}",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Report Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Color(0xFFEF4444).copy(alpha = 0.1f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_report_black),
                                        contentDescription = "Report",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Report This User",
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Help us maintain a safe community",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Reason for Report",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )

                            OutlinedTextField(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                value = content.value,
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Start
                                ),
                                onValueChange = { content.value = it },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences
                                ),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color.Black,
                                    cursorColor = Color(0xFFDFC46B),
                                    focusedBorderColor = Color(0xFFDFC46B),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                    backgroundColor = Color(0xFFF8F9FA)
                                ),
                                placeholder = {
                                    Text(
                                        text = "Please describe why you're reporting this user...",
                                        color = Color.Gray
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Your report is anonymous and helps keep our community safe.",
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Submit Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                color = if (content.value.isNotBlank()) Color(0xFFEF4444) else Color.Gray.copy(
                                    alpha = 0.3f
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                enabled = content.value.isNotBlank() && reportProfileState !is ReportProfileState.Loading
                            ) {
                                if (content.value.isNotBlank()) {
                                    viewModel.reportProfile(profile.value.id, content.value)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (reportProfileState) {
                            is ReportProfileState.Loading -> {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            else -> {
                                Text(
                                    text = "Submit Report",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (content.value.isNotBlank()) Color.White else Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }

                is ProfilePreviewUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Failed to load user profile",
                                color = Color(0xFFE53E3E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = state.error.message,
                                color = Color(0xFFE53E3E),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}