package com.world.pockyapp.screens.auth.login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.icon_visibility
import pockyapp.composeapp.generated.resources.icon_visibilityoff
import pockyapp.composeapp.generated.resources.nearvibe_logo

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

    var passwordVisible by remember { mutableStateOf(false) }

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
                        message = (uiState as LoginUiState.Error).error.message,
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

            Spacer(modifier = Modifier.padding(top = 50.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(Res.drawable.nearvibe_logo),
                    contentDescription = "logo"
                )

                Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = "NearVibe",
                    fontFamily = FontFamily.Default,
                    color = Color(0xFFDFC46B),
                    fontSize = 35.sp,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Welcome to NearVibe",
                fontFamily = FontFamily.Default,
                color = Color.Black,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Sign up to discover the near vibes of people, checking stories, posts, messaging",
                fontFamily = FontFamily.SansSerif,
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth(0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(top = 80.dp))
            

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = { Text(text = "Email", color = Color.Gray) },

                // Left icon
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))


            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = {
                    Text(text = "Password", color = Color.Gray)
                },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Lock, // you can use painterResource if you have custom icon
                        contentDescription = "Password Icon",
                        tint = Color.Gray
                    )
                },

                trailingIcon = {
                    val image =
                        if (passwordVisible) painterResource(Res.drawable.icon_visibility) else painterResource(
                            Res.drawable.icon_visibilityoff
                        )
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, contentDescription = "Toggle password visibility")
                    }
                }
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            Text(
                text = "Forgot Password?",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(0.85f).clickable {
                    navController.navigate(NavRoutes.REGISTER.route)
                },
            )

            Spacer(modifier = Modifier.padding(top = 30.dp))

            Box(
                modifier = Modifier
                    .height(50.dp)
                    .height(50.dp)
                    .fillMaxWidth(0.85f)
                    .background(color = Color(0xFFDFC46B), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center

            ) {
                if (uiState == LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color.Black)
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
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 20.dp))

            Text(
                text = "You don't have an account? Create one",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.clickable {
                    navController.navigate(NavRoutes.REGISTER.route)
                }
            )

            Spacer(modifier = Modifier.padding(top = 20.dp))

            TermsAndPrivacyText({}, {})
        }
    }
}

@Composable
fun TermsAndPrivacyText(
    onTermsClicked: () -> Unit,
    onPrivacyClicked: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        append("By signing up, you agree to our ")

        // Terms of Service
        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(style = SpanStyle(color = Color(0xFFA3A300), fontWeight = FontWeight.Bold)) {
            append("Terms of Service")
        }
        pop()

        append(" and ")

        // Privacy Policy
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(style = SpanStyle(color = Color(0xFFA3A300), fontWeight = FontWeight.Bold)) {
            append("Privacy Policy")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        modifier = Modifier.fillMaxWidth(0.85f),
        style = TextStyle(color = Gray, fontSize = 14.sp),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let { onTermsClicked() }

            annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let { onPrivacyClicked() }
        }
    )
}


