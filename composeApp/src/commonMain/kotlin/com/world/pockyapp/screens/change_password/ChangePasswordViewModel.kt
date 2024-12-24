package com.world.pockyapp.screens.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.edit_profile.EditProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class ChangePasswordUiState {
    data object Idle : ChangePasswordUiState()
    data object Loading : ChangePasswordUiState()
    data class Success(val data: String = "", val message: String = "") : ChangePasswordUiState()
    data class Error(val message: String) : ChangePasswordUiState()
}

class ChangePasswordViewModel(private val sdk: ApiManager) :
    ViewModel() {

    private val _profileState = MutableStateFlow<ProfileModel?>(null)
    val profileState: StateFlow<ProfileModel?> = _profileState.asStateFlow()

    init {
        getProfile()
    }

    fun getProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _profileState.value = success
            }, { error ->
                _profileState.value = null
            })
        }
    }

    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    var currentPassword: String = ""
    var newPassword: String = ""
    var confirmPassword: String = ""

    fun changePassword() {

        if (currentPassword.isEmpty()) {
            _uiState.value = ChangePasswordUiState.Error("Invalid current password")
            return
        }

        if (newPassword.isEmpty() || newPassword.length < 6) {
            _uiState.value = ChangePasswordUiState.Error("Invalid new password")
            return
        }

        if (confirmPassword != newPassword) {
            _uiState.value = ChangePasswordUiState.Error("Invalid confirm password")
            return
        }


        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading
            sdk.changePassword(currentPassword, newPassword, { success ->
                _uiState.value = ChangePasswordUiState.Success(data = "ChangePassword successful")

            }, { error ->
                _uiState.value = ChangePasswordUiState.Error(error)
            })
        }
    }

}

