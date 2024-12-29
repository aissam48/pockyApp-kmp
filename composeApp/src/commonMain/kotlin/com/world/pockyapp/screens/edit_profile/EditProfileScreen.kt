package com.world.pockyapp.screens.edit_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Gray
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
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.compose_multiplatform
import pockyapp.composeapp.generated.resources.ic_close_black
import pockyapp.composeapp.generated.resources.ic_edit_black

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = koinViewModel()
) {

    val scope = rememberCoroutineScope()
    val photo = remember {
        mutableStateOf<ByteArray?>(null)
    }

    val profile by viewModel.profileState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val firstName = remember {
        mutableStateOf("")
    }
    val lastName = remember {
        mutableStateOf(profile?.lastName ?: "")
    }
    val phone = remember {
        mutableStateOf(profile?.phone ?: "")
    }
    val email = remember {
        mutableStateOf(profile?.email ?: "")
    }
    val description = remember {
        mutableStateOf(profile?.description ?: "")
    }

    firstName.value = profile?.firstName ?: ""
    lastName.value = profile?.lastName ?: ""
    phone.value = profile?.phone ?: ""
    email.value = profile?.email ?: ""
    description.value = profile?.description ?: ""

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                photo.value = it
            }
        }
    )

    LaunchedEffect(uiState) {
        when (uiState) {
            EditProfileUiState.Loading -> {

            }

            is EditProfileUiState.Success -> {

            }

            is EditProfileUiState.Error -> {

            }

            else -> Unit
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 15.dp)
        ) {

            item {
                Box(modifier = Modifier.fillMaxWidth()) {

                    Image(
                        painter = painterResource(Res.drawable.ic_close_black),
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp).size(40.dp).clickable {
                            navController.popBackStack()
                        }.align(Alignment.TopStart)
                    )

                    Box(modifier = Modifier.size(150.dp).align(Alignment.Center)) {
                        if (photo.value == null && profile?.photoID == null) {
                            Image(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(150.dp).clip(CircleShape),
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = null
                            )
                        } else {
                            if (photo.value == null && profile?.photoID != null) {
                                AsyncImage(
                                    model = getUrl(profile?.photoID),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(150.dp).clip(CircleShape),
                                    contentDescription = null
                                )

                            } else {
                                AsyncImage(
                                    model = photo.value,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(150.dp).clip(CircleShape),
                                    contentDescription = null
                                )
                            }

                        }

                        Icon(
                            painterResource(Res.drawable.ic_edit_black),
                            null,
                            modifier = Modifier.align(Alignment.BottomEnd).clickable {
                                singleImagePicker.launch()
                            }
                        )
                    }

                }
            }

            item {

                Spacer(modifier = Modifier.padding(top = 40.dp))

                OutlinedTextField(
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(),
                    value = firstName.value,
                    onValueChange = {
                        firstName.value = it
                        profile?.firstName = it
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = {
                        Text(
                            text = "FirstName",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(top = 15.dp))

                OutlinedTextField(
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(),
                    value = lastName.value,
                    onValueChange = {
                        lastName.value = it
                        profile?.lastName = it
                    },
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
                    modifier = Modifier.fillMaxWidth(),
                    value = phone.value,
                    onValueChange = {
                        phone.value = it
                        profile?.phone = it
                    },
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
                    modifier = Modifier.fillMaxWidth(),
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        profile?.email = it
                    },
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
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    value = description.value,
                    textStyle = TextStyle(
                        textAlign = TextAlign.Start  // Aligns text to the start
                    ),
                    onValueChange = {
                        description.value = it
                        profile?.description = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = {
                        Text(
                            text = "Description",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                )

                Spacer(modifier = Modifier.padding(top = 30.dp))

                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(color = Gray, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center

                ) {
                    when (uiState) {
                        is EditProfileUiState.Loading -> {
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
                                        viewModel.description = description.value
                                        viewModel.editProfile(photo.value)
                                    },
                                contentAlignment = Alignment.Center // Center the Text inside the Box
                            ) {
                                Text(
                                    text = "Update",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
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