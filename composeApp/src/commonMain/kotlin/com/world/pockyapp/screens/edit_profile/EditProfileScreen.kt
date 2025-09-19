package com.world.pockyapp.screens.edit_profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.world.pockyapp.Constant
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.screens.components.CustomDialog
import com.world.pockyapp.screens.components.CustomDialogSuccess
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_edit_black
import pockyapp.composeapp.generated.resources.ic_placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = koinViewModel()
) {

    val scope = rememberCoroutineScope()
    val photo = remember { mutableStateOf<ByteArray?>(null) }

    val profile by viewModel.profileState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf(profile?.lastName ?: "") }
    val phone = remember { mutableStateOf(profile?.phone ?: "") }
    val email = remember { mutableStateOf(profile?.email ?: "") }
    val description = remember { mutableStateOf(profile?.description ?: "") }

    firstName.value = profile?.firstName ?: ""
    lastName.value = profile?.lastName ?: ""
    phone.value = profile?.phone ?: ""
    email.value = profile?.email ?: ""
    description.value = profile?.description ?: ""

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                photo.value = it
            }
        }
    )

    var showDialog by remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }

    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "OK",
            onCancel = { showDialog = false }
        )
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            EditProfileUiState.Loading -> {}
            is EditProfileUiState.Success -> {
                title.value = "Your profile has been updated successfully"
                showDialog = true
            }

            is EditProfileUiState.Error -> {
                title.value = state.error.message
                showDialog = true
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.popBackStack()
                            },
                        painter = painterResource(Res.drawable.ic_back_black),
                        contentDescription = "Back"
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Edit Profile",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Profile Photo Section
            item {
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
                        Text(
                            text = "Profile Photo",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.size(140.dp)
                        ) {
                            // Profile Image
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(
                                        Color.Gray.copy(alpha = 0.1f),
                                        CircleShape
                                    )
                            ) {
                                when {
                                    photo.value != null -> {
                                        AsyncImage(
                                            model = photo.value,
                                            contentDescription = "Selected Photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )
                                    }

                                    profile?.photoID != null -> {
                                        AsyncImage(
                                            model = getUrl(profile?.photoID),
                                            contentDescription = "Current Photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            placeholder = painterResource(Res.drawable.ic_placeholder),
                                            error = painterResource(Res.drawable.ic_placeholder),
                                        )
                                    }

                                    else -> {
                                        Image(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            painter = painterResource(Res.drawable.ic_placeholder),
                                            contentDescription = "Placeholder"
                                        )
                                    }
                                }
                            }

                            // Edit Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFDFC46B),
                                        CircleShape
                                    )
                                    .align(Alignment.BottomEnd)
                                    .clickable {
                                        singleImagePicker.launch()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_edit_black),
                                    contentDescription = "Edit Photo",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap the icon to change your photo",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Personal Information Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // First Name
                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = firstName.value,
                            onValueChange = {
                                firstName.value = it
                                profile?.firstName = it
                            },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            label = {
                                Text(
                                    text = "First Name",
                                    color = Color.Gray
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Last Name
                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = lastName.value,
                            onValueChange = {
                                lastName.value = it
                                profile?.lastName = it
                            },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            label = {
                                Text(
                                    text = "Last Name",
                                    color = Color.Gray
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Phone
                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = phone.value,
                            onValueChange = {
                                phone.value = it
                                profile?.phone = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            label = {
                                Text(
                                    text = "Phone",
                                    color = Color.Gray
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = email.value,
                            onValueChange = {
                                email.value = it
                                profile?.email = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            label = {
                                Text(
                                    text = "Email",
                                    color = Color.Gray
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bio Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "About You",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            value = description.value,
                            textStyle = TextStyle(
                                textAlign = TextAlign.Start
                            ),
                            onValueChange = {
                                description.value = it
                                profile?.description = it
                            },
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
                            label = {
                                Text(
                                    text = "Tell us about yourself",
                                    color = Color.Gray
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Write a short bio...",
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Update Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            color = Color(0xFFDFC46B),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = uiState !is EditProfileUiState.Loading) {
                            viewModel.firstName = firstName.value
                            viewModel.lastName = lastName.value
                            viewModel.phone = phone.value
                            viewModel.email = email.value
                            viewModel.description = description.value
                            viewModel.editProfile(photo.value)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState) {
                        is EditProfileUiState.Loading -> {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        else -> {
                            Text(
                                text = "Update Profile",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}