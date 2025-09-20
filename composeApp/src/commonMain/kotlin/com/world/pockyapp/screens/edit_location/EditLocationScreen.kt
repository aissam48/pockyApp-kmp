package com.world.pockyapp.screens.edit_location

import CustomCityPickerDialog
import CustomCountryPickerDialog
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.screens.components.CustomDialogSuccess
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_back_black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLocationScreen(
    navController: NavHostController,
    viewModel: EditLocationViewModel = koinViewModel()
) {

    val countries by viewModel.countriesState.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val countryModel = remember { mutableStateOf<DataModel?>(null) }
    val country = remember { mutableStateOf("Select Country") }
    val city = remember { mutableStateOf("Select City") }
    val shownCountry = remember { mutableStateOf(false) }
    val shownCity = remember { mutableStateOf(false) }

    country.value = profile?.country ?: "Select Country"
    city.value = profile?.city ?: "Select City"

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
            is EditLocationUiState.Loading -> {}
            is EditLocationUiState.Success -> {
                title.value = "Your location has been updated successfully"
                showDialog = true
            }

            is EditLocationUiState.Error -> {
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
                        text = "Edit Location",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Location Info Card
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
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFFDFC46B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Update Your Location",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Choose your country and city",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Country Selector
                        Text(
                            text = "Country",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )

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
                                    text = country.value,
                                    color = if (country.value == "Select Country") Color.Gray else Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = if (country.value == "Select Country") FontWeight.Normal else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // City Selector
                        Text(
                            text = "City",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (countryModel.value != null || country.value != "Select Country") {
                                        shownCity.value = true
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (countryModel.value != null || country.value != "Select Country")
                                    Color(0xFFF8F9FA)
                                else
                                    Color(0xFFF8F9FA).copy(alpha = 0.5f)
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
                                    text = city.value,
                                    color = when {
                                        countryModel.value == null && country.value == "Select Country" -> Color.Gray.copy(
                                            alpha = 0.5f
                                        )

                                        city.value == "Select City" -> Color.Gray
                                        else -> Color.Black
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = if (city.value == "Select City") FontWeight.Normal else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                    tint = if (countryModel.value != null || country.value != "Select Country")
                                        Color.Gray
                                    else
                                        Color.Gray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (countryModel.value == null && country.value == "Select Country") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Please select a country first",
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
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
                        .clickable(enabled = uiState !is EditLocationUiState.Loading) {
                            viewModel.country = country.value
                            viewModel.city = city.value
                            viewModel.editLocation()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState) {
                        is EditLocationUiState.Loading -> {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        else -> {
                            Text(
                                text = "Update Location",
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

    // Dialogs
    CustomCountryPickerDialog(
        data = countries,
        showDialog = shownCountry.value,
        onDismiss = { shownCountry.value = false },
        onCountrySelected = { selectedCountry ->
            countryModel.value = selectedCountry
            country.value = selectedCountry.country
            city.value = "Select City"
            viewModel.city = ""
            profile?.city = "Select City"
            profile?.country = selectedCountry.country
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
            profile?.city = selectedCity
        }
    )
}