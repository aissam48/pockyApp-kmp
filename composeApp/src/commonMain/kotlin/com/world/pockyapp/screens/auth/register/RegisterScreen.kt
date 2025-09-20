import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.screens.auth.register.CountryPhoneCode
import com.world.pockyapp.screens.auth.register.PhoneNumberFieldWithCountryCode
import com.world.pockyapp.screens.auth.register.RegisterScreenViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.icon_visibility
import pockyapp.composeapp.generated.resources.icon_visibilityoff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterScreenViewModel = koinViewModel()
) {

    val countries by viewModel.countriesState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val countryModel = remember { mutableStateOf<DataModel?>(null) }
    val country = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val shownCountry = remember { mutableStateOf(false) }
    val shownCity = remember { mutableStateOf(false) }

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (uiState) {
            RegisterScreenViewModel.RegisterUiState.Loading -> {}
            is RegisterScreenViewModel.RegisterUiState.Success -> {
                navController.navigate(NavRoutes.HOME.route)
            }

            is RegisterScreenViewModel.RegisterUiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = (uiState as RegisterScreenViewModel.RegisterUiState.Error).error.message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Create your account",
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Join NearVibe to discover and share amazing moments with people nearby",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Default
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Information Section
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
                        value = firstName.value,
                        onValueChange = { firstName.value = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        ),
                        label = { Text(text = "First Name", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "First Name",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Last Name
                    OutlinedTextField(
                        value = lastName.value,
                        onValueChange = { lastName.value = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        ),
                        label = { Text(text = "Last Name", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Last Name",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username
                    OutlinedTextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        ),
                        label = { Text(text = "Username", color = Color.Gray) },
                        leadingIcon = {
                            Text(
                                text = "@",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contact Information Section
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
                        text = "Contact Information",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PhoneNumberFieldWithCountryCode{ selectedCountryCode: CountryPhoneCode, phone: String->
                        phoneNumber.value = "${selectedCountryCode.code} $phone"
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        ),
                        label = { Text(text = "Email", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Location Section
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
                        text = "Location",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Country Dropdown
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { shownCountry.value = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (country.value.isEmpty()) "Select Country" else country.value,
                                color = if (country.value.isEmpty()) Color.Gray else Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // City Dropdown
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (countryModel.value != null) {
                                    shownCity.value = true
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (countryModel.value != null) Color(0xFFF8F9FA) else Color(
                                0xFFF8F9FA
                            ).copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (city.value.isEmpty()) "Select City" else city.value,
                                color = when {
                                    countryModel.value == null -> Color.Gray.copy(alpha = 0.5f)
                                    city.value.isEmpty() -> Color.Gray
                                    else -> Color.Black
                                },
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = if (countryModel.value != null) Color.Gray else Color.Gray.copy(
                                    alpha = 0.5f
                                )
                            )
                        }
                    }

                    if (countryModel.value == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please select a country first",
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security Section
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
                        text = "Security",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        ),
                        label = { Text(text = "Password", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = if (passwordVisible) painterResource(Res.drawable.icon_visibilityoff) else painterResource(
                                        Res.drawable.icon_visibility
                                    ),
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword.value,
                        onValueChange = { confirmPassword.value = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        ),
                        label = { Text(text = "Confirm Password", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = if (confirmPasswordVisible) painterResource(Res.drawable.icon_visibilityoff) else painterResource(
                                        Res.drawable.icon_visibility
                                    ),
                                    contentDescription = "Toggle confirm password visibility",
                                    tint = Color.Gray
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color(0xFFDFC46B),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = uiState !is RegisterScreenViewModel.RegisterUiState.Loading) {
                        viewModel.firstName = firstName.value
                        viewModel.lastName = lastName.value
                        viewModel.username = username.value
                        viewModel.phone = phoneNumber.value
                        viewModel.email = email.value
                        viewModel.password = password.value
                        viewModel.confirmPassword = confirmPassword.value
                        viewModel.register()
                    },
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is RegisterScreenViewModel.RegisterUiState.Loading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    else -> {
                        Text(
                            text = "Create Account",
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

    // Dialogs
    CustomCountryPickerDialog(
        data = countries,
        showDialog = shownCountry.value,
        onDismiss = { shownCountry.value = false },
        onCountrySelected = { selectedCountry ->
            countryModel.value = selectedCountry
            country.value = selectedCountry.country
            city.value = "" // Reset city when country changes
            shownCountry.value = false
            viewModel.country = selectedCountry.country
        }
    )

    CustomCityPickerDialog(
        data = countryModel.value?.cities ?: emptyList(),
        showDialog = shownCity.value,
        onDismiss = { shownCity.value = false },
        onCountrySelected = { selectedCity ->
            city.value = selectedCity
            shownCity.value = false
            viewModel.city = selectedCity
        }
    )
}

@Composable
fun CustomCountryPickerDialog(
    data: List<DataModel>,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCountrySelected: (DataModel) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Country",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { onDismiss() }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search countries...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countries List
                    LazyColumn {
                        items(data.filter {
                            it.country.lowercase().contains(searchQuery.value.lowercase())
                        }) { item ->
                            Text(
                                text = item.country,
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCountrySelected(item) }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCityPickerDialog(
    data: List<String>,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCountrySelected: (String) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select City",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { onDismiss() }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search cities...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cities List
                    LazyColumn {
                        items(data.filter {
                            it.lowercase().contains(searchQuery.value.lowercase())
                        }) { item ->
                            Text(
                                text = item,
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCountrySelected(item) }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}