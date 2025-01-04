package com.world.pockyapp.screens.friend_request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.FriendRequestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FriendRequestsUiState {
    data object Loading : FriendRequestsUiState()
    data class Success(val data: List<FriendRequestModel> = listOf(), val message: String = "") :
        FriendRequestsUiState()

    data class Error(val error: ErrorModel) : FriendRequestsUiState()
}

sealed class AcceptRequestsUiState {
    data object Loading : AcceptRequestsUiState()
    data class Success(val data: String = "", val message: String = "") : AcceptRequestsUiState()
    data class Error(val error: ErrorModel) : AcceptRequestsUiState()
}

sealed class RejectRequestsUiState {
    data object Loading : RejectRequestsUiState()
    data class Success(val data: String = "", val message: String = "") : RejectRequestsUiState()
    data class Error(val error: ErrorModel) : RejectRequestsUiState()
}

class FriendRequestsViewModel(val sdk: ApiManager) : ViewModel() {

    private val _friendRequestsState =
        MutableStateFlow<FriendRequestsUiState>(FriendRequestsUiState.Loading)
    val friendRequestsState: StateFlow<FriendRequestsUiState> = _friendRequestsState.asStateFlow()

    private val _acceptRequestState =
        MutableStateFlow<AcceptRequestsUiState>(AcceptRequestsUiState.Loading)
    val acceptRequestState: StateFlow<AcceptRequestsUiState> = _acceptRequestState.asStateFlow()

    private val _rejectRequestState =
        MutableStateFlow<RejectRequestsUiState>(RejectRequestsUiState.Loading)
    val rejectRequestState: StateFlow<RejectRequestsUiState> = _rejectRequestState.asStateFlow()

    fun getFriendRequests() {
        _friendRequestsState.value = FriendRequestsUiState.Loading
        viewModelScope.launch {
            sdk.getFriendRequests({ success ->
                _friendRequestsState.value = FriendRequestsUiState.Success(success)
            }, { error ->
                _friendRequestsState.value =
                    FriendRequestsUiState.Error(error)
            })
        }
    }

    fun acceptFriendRequest(requestID: String) {
        _acceptRequestState.value = AcceptRequestsUiState.Loading
        viewModelScope.launch {
            sdk.acceptFriendRequest(requestID, { success ->
                _acceptRequestState.value = AcceptRequestsUiState.Success(success)
            }, { error ->
                _acceptRequestState.value =
                    AcceptRequestsUiState.Error(error)
            })
        }
    }

    fun rejectFriendRequest(requestID: String) {
        _rejectRequestState.value = RejectRequestsUiState.Loading
        viewModelScope.launch {
            sdk.rejectFriendRequest(requestID, { success ->
                _rejectRequestState.value = RejectRequestsUiState.Success(success)
            }, { error ->
                _rejectRequestState.value =
                    RejectRequestsUiState.Error(error)
            })
        }
    }
}