package com.world.pockyapp.screens.change_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.screens.edit_location.EditLocationUiState
import com.world.pockyapp.screens.edit_profile.EditProfileUiState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black

@Composable
fun ChangePasswordScreen(navController: NavHostController, viewModel: ChangePasswordViewModel = koinViewModel()) {

    val uiState by viewModel.uiState.collectAsState()

    val currentPassword = remember {
        mutableStateOf("")
    }
    val newPassword = remember {
        mutableStateOf("")
    }
    val confirmPassword = remember {
        mutableStateOf("")
    }

    var showDialog by remember { mutableStateOf(false) }

    val title = remember {
        mutableStateOf("")
    }

    if (showDialog) {
        CustomDialogSuccess(
            title = title.value,
            action = "Cancel",
            onCancel = { showDialog = false }
        )
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            ChangePasswordUiState.Loading -> {

            }

            is ChangePasswordUiState.Success -> {
                title.value = "Your password has updated successfully"
                showDialog = true
            }

            is ChangePasswordUiState.Error -> {
                title.value = state.error.message
                showDialog = true
            }

            else -> Unit
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp, top = 15.dp)
        ) {

            item {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier.size(23.dp).clickable {
                            navController.popBackStack()
                        },
                        painter = painterResource(Res.drawable.ic_back_black),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = "Password",
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.size(50.dp))

                OutlinedTextField(
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(),
                    value = currentPassword.value,
                    onValueChange = { currentPassword.value = it },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = { androidx.compose.material3.Text(text = "Current password", color = MaterialTheme.colorScheme.onPrimary) }
                )

                Spacer(modifier = Modifier.padding(top = 15.dp))

                OutlinedTextField(
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(),
                    value = newPassword.value,
                    onValueChange = { newPassword.value = it },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = { androidx.compose.material3.Text(text = "New password", color = MaterialTheme.colorScheme.onPrimary) }
                )

                Spacer(modifier = Modifier.padding(top = 15.dp))

                OutlinedTextField(
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = { androidx.compose.material3.Text(text = "Confirm password", color = MaterialTheme.colorScheme.onPrimary) }
                )

                Spacer(modifier = Modifier.size(30.dp))

                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(color = Gray, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center

                ) {
                    when (uiState) {
                        is ChangePasswordUiState.Loading -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        viewModel.currentPassword = currentPassword.value
                                        viewModel.newPassword = newPassword.value
                                        viewModel.confirmPassword = confirmPassword.value
                                        viewModel.changePassword()
                                    },
                                contentAlignment = Alignment.Center // Center the Text inside the Box
                            ) {
                                androidx.compose.material3.Text(
                                    text = "Update",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}