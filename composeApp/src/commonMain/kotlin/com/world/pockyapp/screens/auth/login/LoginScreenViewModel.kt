package com.world.pockyapp.screens.auth.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class LoginUiState {
    data object Logging : LoginUiState()
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val data: String = "", val message: String = "") : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginScreenViewModel(private val sdk: ApiManager, private val dataStore: DataStore<Preferences>) :
    ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    var email: String = ""
    var password: String = ""

    init {
        viewModelScope.launch {
            dataStore.edit {
                val token = it[stringPreferencesKey("token")]
                if (token != null && token != "") {
                    _uiState.value = LoginUiState.Logging
                }
            }
        }

    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            if (!isValidEmail(email)) {
                _uiState.value = LoginUiState.Error("Invalid Email")
                return@launch
            }

            if (!isValidPassword(password)) {
                _uiState.value = LoginUiState.Error("Invalid Password")
                return@launch
            }

            sdk.login(email, password, { success ->

                _uiState.value = LoginUiState.Success(data = "Login successful")
                CoroutineScope(Dispatchers.Main).launch {
                    dataStore.edit {
                        it[stringPreferencesKey("token")] = success.data.accessToken
                    }
                    delay(500)
                    _uiState.value = LoginUiState.Idle
                }

            }, { error ->
                _uiState.value = LoginUiState.Error(error)
            })
        }
    }

    private fun isValidEmail(email: String): Boolean {
        //return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

}

