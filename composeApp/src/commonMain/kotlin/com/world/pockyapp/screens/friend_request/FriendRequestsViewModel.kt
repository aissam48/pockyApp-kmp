package com.world.pockyapp.screens.friend_request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.FriendRequestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendRequestsViewModel(val sdk: ApiManager) : ViewModel() {

    private val _friendRequestsState = MutableStateFlow<MutableList<FriendRequestModel>>(
        mutableListOf()
    )
    val friendRequestsState: StateFlow<MutableList<FriendRequestModel>> =
        _friendRequestsState.asStateFlow()

    private val _acceptRequestState = MutableStateFlow<String>("")
    val acceptRequestState: StateFlow<String> = _acceptRequestState.asStateFlow()

    private val _rejectRequestState = MutableStateFlow<String>("")
    val rejectRequestState: StateFlow<String> = _rejectRequestState.asStateFlow()


    fun getFriendRequests() {
        viewModelScope.launch {
            sdk.getFriendRequests({ success ->
                _friendRequestsState.value = success.toMutableList()
            }, {
                _friendRequestsState.value = mutableListOf()
            })
        }
    }

    fun acceptFriendRequest(requestID:String) {
        viewModelScope.launch {
            sdk.acceptFriendRequest(requestID,{ success ->
                _acceptRequestState.value = success
            }, {
                _acceptRequestState.value = ""
            })
        }
    }

    fun rejectFriendRequest(requestID:String) {
        viewModelScope.launch {
            sdk.rejectFriendRequest(requestID,{ success ->
                _rejectRequestState.value = success
            }, {
                _rejectRequestState.value = ""
            })
        }
    }

}