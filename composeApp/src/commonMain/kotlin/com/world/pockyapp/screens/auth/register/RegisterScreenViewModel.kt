package com.world.pockyapp.screens.auth.register

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.utils.Utils.isValidEmail
import com.world.pockyapp.utils.Utils.isValidPhoneNumber
import com.world.pockyapp.utils.Utils.isValidUsername
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay


class RegisterScreenViewModel(
    private val sdk: ApiManager,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _countriesState = MutableStateFlow<List<DataModel>>(emptyList())
    val countriesState: StateFlow<List<DataModel>> = _countriesState

    var firstName: String = ""
    var lastName: String = ""
    var username: String = ""
    var phone: String = ""
    var email: String = ""
    var country: String = ""
    var city: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    fun register() {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            if (!isValidName(firstName)) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid First Name",
                        code = 400
                    )
                )
                return@launch
            }

            if (!isValidName(lastName)) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid Last Name",
                        code = 400
                    )
                )
                return@launch
            }

            if (!isValidUsername(username)) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Username should has just chars, numbers, _",
                        code = 400
                    )
                )
                return@launch
            }

            if (!isValidPhoneNumber(phone)) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid Phone",
                        code = 400
                    )
                )
                return@launch
            }

            if (!isValidEmail(email)) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid Email",
                        code = 400
                    )
                )
                return@launch
            }

            if (country.isEmpty()) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid Country",
                        code = 400
                    )
                )
                return@launch
            }

            if (city.isEmpty()) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid City",
                        code = 400
                    )
                )
                return@launch
            }

            if (!isValidPassword(password)) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Invalid Password",
                        code = 400
                    )
                )
                return@launch
            }

            if (password != confirmPassword) {
                _uiState.value = RegisterUiState.Error(
                    error = ErrorModel(
                        message = "Passwords do not match",
                        code = 400
                    )
                )
                return@launch
            }

            sdk.register(firstName, lastName, phone, email, password, country, city, { success ->
                _uiState.value = RegisterUiState.Success("Registration successful")
                CoroutineScope(Dispatchers.Main).launch {
                    dataStore.edit {
                        it[stringPreferencesKey("token")] = success.data.accessToken
                    }
                    delay(500)
                    _uiState.value = RegisterUiState.Idle
                }

            }, {
                _uiState.value = RegisterUiState.Error(it)
            })

        }
    }

    init {
        getCountries()
    }

    private fun getCountries() {
        viewModelScope.launch {

            sdk.getCountriesAndCities({ succes ->
                _countriesState.value = succes
            }, {

            })

        }
    }


    private fun isValidName(name: String): Boolean {
        return name.isNotEmpty()
    }


    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }


    sealed class RegisterUiState() {
        data object Idle : RegisterUiState()
        data object Loading : RegisterUiState()
        data class Success(val message: String) : RegisterUiState()
        data class Error(val error: ErrorModel) : RegisterUiState()
    }
}
