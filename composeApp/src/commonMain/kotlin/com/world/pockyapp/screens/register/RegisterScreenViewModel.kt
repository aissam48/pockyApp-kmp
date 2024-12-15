package com.world.pockyapp.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.world.pockyapp.network.ApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay


class RegisterScreenViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    var firstName: String = ""
    var lastName: String = ""
    var phone: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    fun register() {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            if (!isValidName(firstName)) {
                _uiState.value = RegisterUiState.Error("Invalid First Name")
                return@launch
            }

            if (!isValidName(lastName)) {
                _uiState.value = RegisterUiState.Error("Invalid Last Name")
                return@launch
            }

            if (!isValidPhone(phone)) {
                _uiState.value = RegisterUiState.Error("Invalid Phone")
                return@launch
            }

            if (!isValidEmail(email)) {
                _uiState.value = RegisterUiState.Error("Invalid Email")
                return@launch
            }

            if (!isValidPassword(password)) {
                _uiState.value = RegisterUiState.Error("Invalid Password")
                return@launch
            }

            if (password != confirmPassword) {
                _uiState.value = RegisterUiState.Error("Passwords do not match")
                return@launch
            }

            sdk.register(firstName, lastName, phone, email, password, {
                _uiState.value = RegisterUiState.Success("Registration successful")
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    _uiState.value = RegisterUiState.Idle
                }

            }, {
                _uiState.value = RegisterUiState.Error(it)
            })

        }
    }

    private fun isValidName(name: String): Boolean {
        return name.isNotEmpty()
    }

    private fun isValidEmail(email: String): Boolean {
        //return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return true
    }

    private fun isValidPhone(phone: String): Boolean {
        //return Patterns.PHONE.matcher(phone).matches()
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }


    sealed class RegisterUiState() {
        data object Idle : RegisterUiState()
        data object Loading : RegisterUiState()
        data class Success(val message: String) : RegisterUiState()
        data class Error(val message: String) : RegisterUiState()
    }
}
