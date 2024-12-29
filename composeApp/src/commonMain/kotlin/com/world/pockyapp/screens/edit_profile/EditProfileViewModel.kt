package com.world.pockyapp.screens.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class EditProfileUiState {
    data object Idle : EditProfileUiState()
    data object Loading : EditProfileUiState()
    data class Success(val data: String = "", val message: String = "") : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

class EditProfileViewModel(private val sdk: ApiManager) :
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

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState

    var firstName: String = ""
    var lastName: String = ""
    var phone: String = ""
    var email: String = ""
    var description: String = ""


    fun editProfile(byteArray: ByteArray?) {

        if (firstName.isEmpty()) {
            _uiState.value = EditProfileUiState.Error("Invalid First Name")
            return
        }

        if (lastName.isEmpty()) {
            _uiState.value = EditProfileUiState.Error("Invalid Last Name")
            return
        }

        if (phone.isEmpty()) {
            _uiState.value = EditProfileUiState.Error("Invalid Phone")
            return
        }

        if (email.isEmpty()) {
            _uiState.value = EditProfileUiState.Error("Invalid Email")
            return
        }

        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            sdk.editProfile(firstName, lastName, phone, email, description, byteArray, { success ->
                _uiState.value = EditProfileUiState.Success(data = "EditProfile successful")

            }, { error ->
                _uiState.value = EditProfileUiState.Error(error)
            })
        }
    }
}

