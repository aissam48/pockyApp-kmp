package com.world.pockyapp.screens.edit_location

import CustomCityPickerDialog
import CustomCountryPickerDialog
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
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
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.screens.change_password.ChangePasswordUiState
import com.world.pockyapp.screens.components.CustomDialogSuccess
import com.world.pockyapp.screens.edit_profile.EditProfileUiState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_back_black
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_edit_black

@Composable
fun EditLocationScreen(
    navController: NavHostController,
    viewModel: EditLocationViewModel = koinViewModel()
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

    val scope = rememberCoroutineScope()

    val profile by viewModel.profileState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()


    country.value = profile?.country ?: ""
    city.value = profile?.city ?: ""

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
            is EditLocationUiState.Loading -> {

            }

            is EditLocationUiState.Success -> {
                title.value = "Your location has updated successfully"
                showDialog = true
            }

            is EditLocationUiState.Error -> {
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
                    androidx.compose.material.Text(
                        text = "Location",
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(50.dp))

                Box(
                    modifier = Modifier.height(55.dp).fillMaxWidth()
                        .background(color = LightGray, shape = RoundedCornerShape(15.dp))
                        .clickable {
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

                    city.value = "City"
                    viewModel.city = ""
                    profile?.city = "City"

                    profile?.country = cou.country
                    shownCountry.value = false
                    viewModel.country = cou.country
                })

                Spacer(modifier = Modifier.padding(top = 15.dp))

                Box(
                    modifier = Modifier.height(55.dp).fillMaxWidth()
                        .background(color = LightGray, shape = RoundedCornerShape(15.dp))
                        .clickable {
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
                    profile?.city = cit
                })

                Spacer(modifier = Modifier.size(30.dp))

                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(color = Gray, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center

                ) {
                    when (uiState) {
                        is EditLocationUiState.Loading -> {
                            CircularProgressIndicator(color = Color.Black)
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        viewModel.country = country.value
                                        viewModel.city = city.value
                                        viewModel.editLocation()
                                    },
                                contentAlignment = Alignment.Center // Center the Text inside the Box
                            ) {
                                Text(
                                    text = "Update",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 30.dp))

            }
        }
    }


}