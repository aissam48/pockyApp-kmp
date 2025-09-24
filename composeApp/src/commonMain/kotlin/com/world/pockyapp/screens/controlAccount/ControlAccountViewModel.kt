package com.world.pockyapp.screens.controlAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.network.models.model.ResponseMessageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ControlAccountViewModel(private val sdk: ApiManager) : ViewModel() {


    private val _followersVisibilityStateFlow = MutableStateFlow<ResponseState<ResponseMessageModel>>(ResponseState.Idle)
    val followersVisibilityStateFlow = _followersVisibilityStateFlow.asStateFlow()
    fun followersVisibility(visibility: String) {
        viewModelScope.launch {
            _followersVisibilityStateFlow.value = ResponseState.Loading
            sdk.updateFollowersVisibility(visibility, {
                _followersVisibilityStateFlow.value = ResponseState.Success(it)
            }, {
                _followersVisibilityStateFlow.value = ResponseState.Error(it)
            })
        }
    }

    private val _followingsVisibilityStateFlow = MutableStateFlow<ResponseState<ResponseMessageModel>>(ResponseState.Idle)
    val followingsVisibilityStateFlow = _followingsVisibilityStateFlow.asStateFlow()
    fun followingsVisibility(visibility: String) {
        viewModelScope.launch {
            _followingsVisibilityStateFlow.value = ResponseState.Loading
            sdk.updateFollowingsVisibility(visibility, {
                _followingsVisibilityStateFlow.value = ResponseState.Success(it)
            }, {
                _followingsVisibilityStateFlow.value = ResponseState.Error(it)
            })
        }
    }

    private val _friendsVisibilityStateFlow = MutableStateFlow<ResponseState<ResponseMessageModel>>(ResponseState.Idle)
    val friendsVisibilityStateFlow = _friendsVisibilityStateFlow.asStateFlow()
    fun friendsVisibility(visibility: String) {
        viewModelScope.launch {
            _friendsVisibilityStateFlow.value = ResponseState.Loading
            sdk.updateFriendsVisibility(visibility, {
                _friendsVisibilityStateFlow.value = ResponseState.Success(it)
            }, {
                _friendsVisibilityStateFlow.value = ResponseState.Error(it)
            })
        }
    }


    private val _profileState = MutableStateFlow<ResponseState<ProfileModel>>(ResponseState.Loading)
    val profileState: StateFlow<ResponseState<ProfileModel>> = _profileState.asStateFlow()
    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = ResponseState.Loading

            try {
                sdk.getMyProfile(
                    onSuccess = { profile ->
                        _profileState.value = ResponseState.Success(profile)
                    },
                    onFailure = { error ->
                        _profileState.value = ResponseState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _profileState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }


}

sealed class ResponseState<out T> {
    object Idle : ResponseState<Nothing>()
    object Loading : ResponseState<Nothing>()
    data class Success<T>(val data: T) : ResponseState<T>()
    data class Error(val error: ErrorModel) : ResponseState<Nothing>()
}
