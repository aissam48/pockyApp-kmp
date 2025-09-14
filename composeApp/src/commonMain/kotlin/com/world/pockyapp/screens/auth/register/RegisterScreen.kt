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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
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
import com.world.pockyapp.screens.auth.register.RegisterScreenViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.icon_country
import pockyapp.composeapp.generated.resources.icon_username
import pockyapp.composeapp.generated.resources.icon_visibility
import pockyapp.composeapp.generated.resources.icon_visibilityoff
import pockyapp.composeapp.generated.resources.icons_name

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterScreenViewModel = koinViewModel()
) {

    val countries by viewModel.countriesState.collectAsState()

    val countryModel = remember {
        mutableStateOf<DataModel?>(null)
    }

    val country = remember {
        mutableStateOf("")
    }
    val city = remember {
        mutableStateOf("")
    }
    val shownCountry = remember {
        mutableStateOf(false)
    }
    val shownCity = remember {
        mutableStateOf(false)
    }

    val firstName = remember {
        mutableStateOf("")
    }
    val lastName = remember {
        mutableStateOf("")
    }
    val username = remember {
        mutableStateOf("")
    }
    val phone = remember {
        mutableStateOf("")
    }
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val confirmPassword = remember {
        mutableStateOf("")
    }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }


    LaunchedEffect(uiState) {
        when (uiState) {
            RegisterScreenViewModel.RegisterUiState.Loading -> {

            }

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

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Image(
                    modifier = Modifier.size(23.dp).clickable {
                        navController.popBackStack()
                    },
                    painter = painterResource(Res.drawable.ic_back_black),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(15.dp))

            }
            Spacer(modifier = Modifier.padding(top = 20.dp))

            androidx.compose.material.Text(
                text = "Create your account",
                color = Color.Black,
                fontSize = 25.sp,
                fontFamily = FontFamily.Default,
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            androidx.compose.material.Text(
                text = "Create an account discover a world of near vibes to be in and share yours",
                color = Color(0xFFDFC46B),
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.padding(top = 50.dp))

            OutlinedTextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words // better for names
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = { Text(text = "First Name", color = Color.Gray) },

                // Left icon (optional)
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.icons_name),
                        contentDescription = "First Name Icon"
                    )
                }
            )

            OutlinedTextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words // better for names
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = { Text(text = "Last Name", color = Color.Gray) },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.icons_name),
                        contentDescription = "Last Name Icon"
                    )
                }
            )

            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words // better for names
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = { Text(text = "Username", color = Color.Gray) },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.icon_username),
                        contentDescription = "Username Icon"
                    )
                }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            OutlinedTextField(
                value = phone.value,
                onValueChange = { phone.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = { Text(text = "Phone", color = Color.Gray) },

                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 5.dp)
                    ) {
                        Text(
                            text = "+212",
                            color = Color.Black,
                            modifier = Modifier.padding(end = 4.dp).clickable { }
                        )
                        Spacer(modifier = Modifier.width(5.dp).height(0.dp))
                        Divider(
                            modifier = Modifier.height(30.dp).width(2.dp),
                            color = Color(0xFFDFC46B)
                        )
                    }
                }
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = { Text(text = "Email", color = Color.Gray) },

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
                value = country.value,
                onValueChange = { country.value = it },
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = {
                    Text(text = "Country", color = Color.Gray)
                },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp).clickable {
                            shownCountry.value = true
                        },
                        painter = painterResource(Res.drawable.icon_country),
                        contentDescription = "country Icon"
                    )
                }
            )

            CustomCountryPickerDialog(countries, shownCountry.value, {
                shownCountry.value = false
            }, { cou ->
                countryModel.value = cou
                country.value = cou.country
                shownCountry.value = false
                viewModel.country = cou.country
            })

            OutlinedTextField(
                value = city.value,
                onValueChange = { city.value = it },
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = {
                    Text(text = "City", color = Color.Gray)
                },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp).clickable {
                            shownCity.value = true
                        },
                        painter = painterResource(Res.drawable.icon_country),
                        contentDescription = "city Icon"
                    )
                }
            )
            CustomCityPickerDialog(countryModel.value?.cities ?: emptyList(), shownCity.value, {
                shownCity.value = false
            }, { cit ->
                city.value = cit
                shownCity.value = false
                viewModel.city = cit
            })

            Spacer(modifier = Modifier.padding(top = 15.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
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

            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                label = {
                    Text(text = "Confirm password", color = Color.Gray)
                },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Lock, // you can use painterResource if you have custom icon
                        contentDescription = "confirm password Icon",
                        tint = Color.Gray
                    )
                },

                trailingIcon = {
                    val image =
                        if (confirmPasswordVisible) painterResource(Res.drawable.icon_visibility) else painterResource(
                            Res.drawable.icon_visibilityoff
                        )
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = "Toggle confirm password visibility"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.padding(top = 20.dp))

            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f).align(Alignment.CenterHorizontally)
                    .background(color = Color(0xFFDFC46B), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center

            ) {
                when (uiState) {
                    is RegisterScreenViewModel.RegisterUiState.Loading -> {
                        CircularProgressIndicator(color = Color.Black)
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    viewModel.firstName = firstName.value
                                    viewModel.lastName = lastName.value
                                    viewModel.username = username.value
                                    viewModel.phone = phone.value
                                    viewModel.email = email.value
                                    viewModel.password = password.value
                                    viewModel.confirmPassword = confirmPassword.value
                                    viewModel.register()
                                },
                            contentAlignment = Alignment.Center // Center the Text inside the Box
                        ) {
                            Text(
                                text = "Register",
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 20.dp))

        }
    }
}

@Composable
fun CustomCountryPickerDialog(
    data: List<DataModel>,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCountrySelected: (DataModel) -> Unit
) {

    val searchQuery = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Country",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
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

                    // Country list
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(data.filter {
                            it.country.lowercase().contains(searchQuery.value.lowercase())
                        }) { item ->

                            Text(text = item.country, color = Black, modifier = Modifier.clickable {
                                onCountrySelected(item)
                            })
                            Spacer(modifier = Modifier.size(10.dp))

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

    val searchQuery = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select City",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
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

                    // Country list
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(data.filter {
                            it.lowercase().contains(searchQuery.value.lowercase())
                        }) { item ->

                            Text(text = item, color = Black, modifier = Modifier.clickable {
                                onCountrySelected(item)
                            })
                            Spacer(modifier = Modifier.size(10.dp))

                        }
                    }
                }
            }
        }

    }
}
