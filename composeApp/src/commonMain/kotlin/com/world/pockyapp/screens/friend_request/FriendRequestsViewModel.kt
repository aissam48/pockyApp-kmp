package com.world.pockyapp.screens.friend_request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.FriendRequestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class FriendRequestsViewModel(val sdk: ApiManager) : ViewModel() {

    private val _friendRequestsState = MutableStateFlow<UiState<List<FriendRequestModel>>>(UiState.Loading)
    val friendRequestsState: StateFlow<UiState<List<FriendRequestModel>>> = _friendRequestsState.asStateFlow()

    private val _acceptRequestState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val acceptRequestState: StateFlow<UiState<String>> = _acceptRequestState.asStateFlow()

    private val _rejectRequestState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val rejectRequestState: StateFlow<UiState<String>> = _rejectRequestState.asStateFlow()

    fun getFriendRequests() {
        _friendRequestsState.value = UiState.Loading
        viewModelScope.launch {
            sdk.getFriendRequests({ success ->
                _friendRequestsState.value = UiState.Success(success)
            }, { error ->
                _friendRequestsState.value = UiState.Error(error ?: "An error occurred")
            })
        }
    }

    fun acceptFriendRequest(requestID: String) {
        _acceptRequestState.value = UiState.Loading
        viewModelScope.launch {
            sdk.acceptFriendRequest(requestID, { success ->
                _acceptRequestState.value = UiState.Success(success)
            }, { error ->
                _acceptRequestState.value = UiState.Error(error ?: "An error occurred")
            })
        }
    }

    fun rejectFriendRequest(requestID: String) {
        _rejectRequestState.value = UiState.Loading
        viewModelScope.launch {
            sdk.rejectFriendRequest(requestID, { success ->
                _rejectRequestState.value = UiState.Success(success)
            }, { error ->
                _rejectRequestState.value = UiState.Error(error ?: "An error occurred")
            })
        }
    }
}