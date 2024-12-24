package com.world.pockyapp.screens.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
) {
    val viewModel = koinViewModel<LoginScreenViewModel>()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    /*val notifier = NotifierManager.getLocalNotifier()
    notifier.notify (
        title = "Title from KMPNotifier",
        body = "Body message from KMPNotifier",
        payloadData = mapOf(
            Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
            "extraKey" to "randomValue"
        )
    )*/

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Logging -> {
                navController.navigate(NavRoutes.HOME.route)
            }

            is LoginUiState.Success -> {
                navController.navigate(NavRoutes.HOME.route)
            }

            is LoginUiState.Error -> {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        message = (uiState as LoginUiState.Error).message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            LoginUiState.Loading -> {

            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.padding(top = 100.dp))

            Text(
                text = "E-Shri",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 50.sp,
            )

            Spacer(modifier = Modifier.padding(top = 80.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                value = email.value,
                onValueChange = { email.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text(text = "Email", color = MaterialTheme.colorScheme.onPrimary) }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                value = password.value,
                onValueChange = { password.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text(text = "Password", color = MaterialTheme.colorScheme.onPrimary) }
            )

            Spacer(modifier = Modifier.padding(top = 30.dp))

            Box(
                modifier = Modifier
                    .height(50.dp)
                    .height(50.dp)
                    .fillMaxWidth(0.85f)
                    .background(color = Gray, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center

            ) {
                if (uiState == LoginUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                viewModel.email = email.value
                                viewModel.password = password.value
                                viewModel.login()
                            },
                        contentAlignment = Alignment.Center // Center the Text inside the Box
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }


                }
            }

            Spacer(modifier = Modifier.padding(top = 20.dp))

            Text(
                text = "You don't have an account? Create one",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate(NavRoutes.REGISTER.route)
                }
            )
        }
    }
}

