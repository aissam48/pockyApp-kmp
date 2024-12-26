package com.world.pockyapp.screens.profile_preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.home.navigations.profile.ProfileUiState
import com.world.pockyapp.screens.post_preview.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilePreviewViewModel(private val sdk: ApiManager) : ViewModel() {
    private val _profileState =
        MutableStateFlow<ProfilePreviewUiState>(ProfilePreviewUiState.Loading)
    val profileState: StateFlow<ProfilePreviewUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<List<PostModel>>(emptyList())
    val postsState: StateFlow<List<PostModel>> = _postsState.asStateFlow()

    private val _sendChatRequestState = MutableStateFlow<String>("")
    val sendChatRequestState: StateFlow<String> = _sendChatRequestState.asStateFlow()

    private val _responseChatRequestState = MutableStateFlow<String>("")
    val responseChatRequestState: StateFlow<String> = _responseChatRequestState.asStateFlow()

    private val _myProfileState = MutableStateFlow<ProfileModel?>(ProfileModel())
    val myProfileState: StateFlow<ProfileModel?> = _myProfileState.asStateFlow()

    private val _beFriendState = MutableStateFlow<String>("")
    val beFriendState: StateFlow<String> = _beFriendState.asStateFlow()

    private val _unFriendState = MutableStateFlow<String>("")
    val unFriendState: StateFlow<String> = _unFriendState.asStateFlow()


    fun getProfile(id: String) {
        viewModelScope.launch {
            sdk.getProfile(id, { success ->
                _profileState.value = ProfilePreviewUiState.Success(success)
            }, { error ->
                _profileState.value = ProfilePreviewUiState.Error(error ?: "Unknown error")
            })
        }
    }

    fun beFriend(id: String) {
        viewModelScope.launch {
            sdk.beFriend(id, { success ->
                _beFriendState.value = success
            }, { error ->
                _beFriendState.value = ""
            })
        }
    }

    fun getMyProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _myProfileState.value = success
            }, { error ->
                _myProfileState.value = null
            })
        }
    }

    fun getPosts(id: String) {
        viewModelScope.launch {
            sdk.getPosts(id, { success ->
                _postsState.value = success
            }, { error ->
                _postsState.value = listOf()
            })
        }
    }

    fun sendRequestChat(otherUserID: String) {
        viewModelScope.launch {
            sdk.sendRequestChat(otherUserID, { success ->
                _sendChatRequestState.value = success
            }, { error ->
                _sendChatRequestState.value = error
            })
        }
    }

    fun responseRequestChat(
        id: String,
        status: Boolean
    ) {
        viewModelScope.launch {
            sdk.responseRequestChat(id, status, { success ->
                _responseChatRequestState.value = success
            }, { error ->
                _responseChatRequestState.value = error
            })
        }
    }

    fun removeFriend(id: String) {
        viewModelScope.launch {
            sdk.unFriend(id, { success ->
                _unFriendState.value = success
            }, { error ->
                _unFriendState.value = ""
            })
        }
    }
}

sealed class ProfilePreviewUiState {
    object Loading : ProfilePreviewUiState()
    data class Success(val profile: ProfileModel) : ProfilePreviewUiState()
    data class Error(val message: String) : ProfilePreviewUiState()
}