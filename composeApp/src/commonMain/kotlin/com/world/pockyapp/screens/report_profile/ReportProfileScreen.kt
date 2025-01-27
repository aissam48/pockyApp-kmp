package com.world.pockyapp.screens.report_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.components.CustomDialog
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.screens.profile_preview.ProfilePreviewUiState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_placeholder

@Composable
fun ReportProfileScreen(
    navController: NavHostController,
    id: String,
    viewModel: ReportProfileViewModel = koinViewModel()
) {

    val profileState by viewModel.profileState.collectAsState()
    val reportProfileState by viewModel.reportProfileState.collectAsState()

    val result = remember {
        mutableStateOf("")
    }
    val showDialog = remember {
        mutableStateOf(false)
    }

    val profile = remember {
        mutableStateOf(ProfileModel())
    }

    val content = remember {
        mutableStateOf("")
    }

    if (showDialog.value) {
        CustomDialogSuccess(
            title = result.value,
            action = "Cancel",
            onCancel = {
                showDialog.value = false
                content.value = ""
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getProfile(id = id)
    }

    LaunchedEffect(reportProfileState) {
        when (val state = reportProfileState) {
            is ReportProfileState.Loading -> {

            }

            is ReportProfileState.Success -> {
                showDialog.value = true
                result.value = state.message
            }

            is ReportProfileState.Error -> {

            }

            is ReportProfileState.Idle -> {

            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->

        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

            Spacer(modifier = Modifier.size(20.dp))
            Image(
                modifier = Modifier.size(23.dp).clickable {
                    navController.popBackStack()
                },
                painter = painterResource(Res.drawable.ic_back_black),
                contentDescription = null
            )

            when (val state = profileState) {
                is ProfilePreviewUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }


                }

                is ProfilePreviewUiState.Success -> {
                    profile.value = state.profile

                    AsyncImage(
                        model = getUrl(profile.value.photoID),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(Res.drawable.ic_placeholder),
                        error = painterResource(Res.drawable.ic_placeholder),
                    )

                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = profile.value.firstName,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        maxLines = 2
                    )
                    Text(
                        text = profile.value.username,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = "Tell us why you want to report ${profile.value.firstName} ${profile.value.lastName}",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.size(10.dp))

                    OutlinedTextField(
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        value = content.value,
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start  // Aligns text to the start
                        ),
                        onValueChange = {
                            content.value = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        ),
                        label = {
                            Text(
                                text = "Report",
                                color = Color.Black
                            )
                        },
                    )

                    Spacer(modifier = Modifier.padding(top = 30.dp))

                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .background(color = Gray, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center

                    ) {
                        when (reportProfileState) {
                            is ReportProfileState.Loading -> {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }

                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            viewModel.reportProfile(profile.value.id, content.value)
                                        },
                                    contentAlignment = Alignment.Center // Center the Text inside the Box
                                ) {
                                    androidx.compose.material3.Text(
                                        text = "Send",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(top = 30.dp))
                }

                is ProfilePreviewUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.error.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                }
            }

        }

    }
}