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
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
        mutableStateOf("Country")
    }
    val city = remember {
        mutableStateOf("City")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.Start)) {
                Image(
                    modifier = Modifier.size(23.dp).clickable {
                        navController.popBackStack()
                    },
                    painter = painterResource(Res.drawable.ic_back_black),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(15.dp))
                androidx.compose.material.Text(
                    text = "Register",
                    color = Color.Black,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.padding(top = 50.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                value = firstName.value,
                onValueChange = { firstName.value = it },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text(text = "FirstName", color = MaterialTheme.colorScheme.onPrimary) }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                value = lastName.value,
                onValueChange = { lastName.value = it },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text(text = "LastName", color = MaterialTheme.colorScheme.onPrimary) }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                value = phone.value,
                onValueChange = { phone.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text(text = "Phone", color = MaterialTheme.colorScheme.onPrimary) }
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

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

            Box(
                modifier = Modifier.height(55.dp).fillMaxWidth(0.85f)
                    .background(color = LightGray, shape = RoundedCornerShape(15.dp)).clickable {
                        shownCountry.value = true
                    }.padding(start = 15.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = country.value,
                    color = if (country.value == "Country") Color.White else Color.Black
                )
            }


            CustomCountryPickerDialog(countries, shownCountry.value, {
                shownCountry.value = false
            }, { cou ->
                countryModel.value = cou
                country.value = cou.country
                shownCountry.value = false
                viewModel.country = cou.country
            })

            Spacer(modifier = Modifier.padding(top = 15.dp))

            Box(
                modifier = Modifier.height(55.dp).fillMaxWidth(0.85f)
                    .background(color = LightGray, shape = RoundedCornerShape(15.dp)).clickable {
                        shownCity.value = true
                    }.padding(start = 15.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = city.value,
                    color = if (city.value == "City") Color.White else Color.Black
                )
            }


            CustomCityPickerDialog(countryModel.value?.cities ?: emptyList(), shownCity.value, {
                shownCity.value = false
            }, { cit ->
                city.value = cit
                shownCity.value = false
                viewModel.city = cit
            })

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

            Spacer(modifier = Modifier.padding(top = 15.dp))

            OutlinedTextField(
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(0.85f),
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                label = {
                    Text(
                        text = "Confirm Password",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )

            Spacer(modifier = Modifier.padding(top = 20.dp))

            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f)
                    .background(color = Gray, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center

            ) {
                when (uiState) {
                    is RegisterScreenViewModel.RegisterUiState.Loading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    viewModel.firstName = firstName.value
                                    viewModel.lastName = lastName.value
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
                                color = MaterialTheme.colorScheme.primary
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
