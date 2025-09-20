package com.world.pockyapp.screens.change_password

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
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.screens.components.CustomDialogSuccess
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.icon_visibility
import pockyapp.composeapp.generated.resources.icon_visibilityoff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavHostController,
    viewModel: ChangePasswordViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    val currentPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

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
            ChangePasswordUiState.Loading -> {}
            is ChangePasswordUiState.Success -> {
                title.value = "Your password has been updated successfully"
                showDialog = true
            }

            is ChangePasswordUiState.Error -> {
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
                .padding(horizontal = 16.dp)
        ) {
            // Header
            item {
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
                        text = "Change Password",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Security Info Card
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFDFC46B).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Security",
                                    tint = Color(0xFFDFC46B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Update Your Password",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Keep your account secure",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Current Password
                        Text(
                            text = "Current Password",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )

                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = currentPassword.value,
                            onValueChange = { currentPassword.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (showCurrentPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            placeholder = {
                                Text(
                                    text = "Enter your current password",
                                    color = Color.Gray
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showCurrentPassword = !showCurrentPassword }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = if (showNewPassword)
                                            painterResource(Res.drawable.icon_visibilityoff)
                                        else
                                            painterResource(Res.drawable.icon_visibility),
                                        contentDescription = if (showCurrentPassword) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // New Password
                        Text(
                            text = "New Password",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )

                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = newPassword.value,
                            onValueChange = { newPassword.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (showNewPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            placeholder = {
                                Text(
                                    text = "Enter your new password",
                                    color = Color.Gray
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showNewPassword = !showNewPassword }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = if (showNewPassword)
                                            painterResource(Res.drawable.icon_visibilityoff)
                                        else
                                            painterResource(Res.drawable.icon_visibility),
                                        contentDescription = if (showNewPassword) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm Password
                        Text(
                            text = "Confirm New Password",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )

                        OutlinedTextField(
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            value = confirmPassword.value,
                            onValueChange = { confirmPassword.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (showConfirmPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color(0xFFDFC46B),
                                focusedBorderColor = Color(0xFFDFC46B),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                backgroundColor = Color(0xFFF8F9FA)
                            ),
                            placeholder = {
                                Text(
                                    text = "Confirm your new password",
                                    color = Color.Gray
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showConfirmPassword = !showConfirmPassword }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = if (showConfirmPassword)
                                            painterResource(Res.drawable.icon_visibilityoff)
                                        else
                                            painterResource(Res.drawable.icon_visibility),
                                        contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )

                        // Password requirements hint
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Password should be at least 8 characters long",
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
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
                        .clickable(enabled = uiState !is ChangePasswordUiState.Loading) {
                            viewModel.currentPassword = currentPassword.value
                            viewModel.newPassword = newPassword.value
                            viewModel.confirmPassword = confirmPassword.value
                            viewModel.changePassword()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState) {
                        is ChangePasswordUiState.Loading -> {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        else -> {
                            Text(
                                text = "Update Password",
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