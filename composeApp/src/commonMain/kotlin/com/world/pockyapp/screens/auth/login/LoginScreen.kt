package com.world.pockyapp.screens.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val scrollState = rememberScrollState()

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
            LoginUiState.Loading -> {}
            else -> Unit
        }
    }

    // Modern color scheme
    val primaryGold = Color(0xFFDFC46B)
    val darkGold = Color(0xFFB8A055)
    val lightGray = Color(0xFFF8F9FA)
    val mediumGray = Color(0xFF6C757D)
    val darkText = Color(0xFF212529)
    val cardBackground = Color(0xFFFFFFFF)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFFFFFFF)
                )
                .padding(paddingValues)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                // Modern Logo Section with glassmorphism effect
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            spotColor = primaryGold.copy(alpha = 0.3f)
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground.copy(alpha = 0.95f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(50.dp),
                            painter = painterResource(Res.drawable.nearvibe_logo),
                            contentDescription = "NearVibe Logo"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // App Name with modern typography
                Text(
                    text = "NearVibe",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkText,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Connect with your community",
                    fontSize = 16.sp,
                    color = mediumGray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Modern Card Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Welcome",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Sign in to your account to share your vibe with others",
                            fontSize = 14.sp,
                            color = mediumGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Modern Email Field
                        OutlinedTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = darkText,
                                unfocusedTextColor = darkText,
                                cursorColor = primaryGold,
                                focusedBorderColor = primaryGold,
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedLabelColor = primaryGold,
                                unfocusedLabelColor = mediumGray,
                                focusedLeadingIconColor = primaryGold,
                                unfocusedLeadingIconColor = mediumGray
                            ),
                            label = {
                                Text(
                                    text = "Email address",
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        // Modern Password Field
                        OutlinedTextField(
                            value = password.value,
                            onValueChange = { password.value = it },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = darkText,
                                unfocusedTextColor = darkText,
                                cursorColor = primaryGold,
                                focusedBorderColor = primaryGold,
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedLabelColor = primaryGold,
                                unfocusedLabelColor = mediumGray,
                                focusedLeadingIconColor = primaryGold,
                                unfocusedLeadingIconColor = mediumGray,
                                focusedTrailingIconColor = primaryGold,
                                unfocusedTrailingIconColor = mediumGray
                            ),
                            label = {
                                Text(
                                    text = "Password",
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    painterResource(Res.drawable.icon_visibility)
                                else
                                    painterResource(Res.drawable.icon_visibilityoff)

                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        painter = image,
                                        contentDescription = "Toggle password visibility",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        // Forgot Password Link
                        Text(
                            text = "Forgot password?",
                            fontSize = 14.sp,
                            color = primaryGold,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable {
                                    navController.navigate(NavRoutes.REGISTER.route)
                                }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Modern Login Button
                        Button(
                            onClick = {
                                viewModel.email = email.value
                                viewModel.password = password.value
                                viewModel.login()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryGold,
                                contentColor = Color.White
                            ),
                            enabled = uiState != LoginUiState.Loading
                        ) {
                            if (uiState == LoginUiState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Sign In",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sign Up Link
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Don't have an account? ",
                                fontSize = 14.sp,
                                color = mediumGray
                            )
                            Text(
                                text = "Sign up",
                                fontSize = 14.sp,
                                color = primaryGold,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    navController.navigate(NavRoutes.REGISTER.route)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Modern Terms and Privacy
                ModernTermsAndPrivacyText(
                    onTermsClicked = { /* Handle terms click */ },
                    onPrivacyClicked = { /* Handle privacy click */ }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ModernTermsAndPrivacyText(
    onTermsClicked: () -> Unit,
    onPrivacyClicked: () -> Unit
) {
    val mediumGray = Color(0xFF6C757D)
    val primaryGold = Color(0xFFDFC46B)

    val annotatedText = buildAnnotatedString {
        append("By signing in, you agree to our ")

        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(style = SpanStyle(color = primaryGold, fontWeight = FontWeight.Medium)) {
            append("Terms of Service")
        }
        pop()

        append(" and ")

        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(style = SpanStyle(color = primaryGold, fontWeight = FontWeight.Medium)) {
            append("Privacy Policy")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        style = TextStyle(
            color = mediumGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let { onTermsClicked() }

            annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let { onPrivacyClicked() }
        }
    )
}