package com.world.pockyapp.screens.profile_preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilePreviewViewModel(private val sdk: ApiManager) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfilePreviewUiState>(ProfilePreviewUiState.Loading)
    val profileState: StateFlow<ProfilePreviewUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<PostsState>(PostsState.Loading)
    val postsState: StateFlow<PostsState> = _postsState.asStateFlow()

    private val _sendChatRequestState = MutableStateFlow<ChatRequestState>(ChatRequestState.Loading)
    val sendChatRequestState: StateFlow<ChatRequestState> = _sendChatRequestState.asStateFlow()

    private val _responseChatRequestState = MutableStateFlow<ChatRequestState>(ChatRequestState.Loading)
    val responseChatRequestState: StateFlow<ChatRequestState> = _responseChatRequestState.asStateFlow()

    private val _myProfileState = MutableStateFlow<MyProfileState>(MyProfileState.Loading)
    val myProfileState: StateFlow<MyProfileState> = _myProfileState.asStateFlow()

    private val _beFriendState = MutableStateFlow<FriendState>(FriendState.Loading)
    val beFriendState: StateFlow<FriendState> = _beFriendState.asStateFlow()

    private val _unFriendState = MutableStateFlow<FriendState>(FriendState.Loading)
    val unFriendState: StateFlow<FriendState> = _unFriendState.asStateFlow()

    fun getProfile(id: String) {
        _profileState.value = ProfilePreviewUiState.Loading
        viewModelScope.launch {
            sdk.getProfile(id, { success ->
                _profileState.value = ProfilePreviewUiState.Success(success)
            }, { error ->
                _profileState.value = ProfilePreviewUiState.Error(error ?: "Unknown error")
            })
        }
    }

    fun beFriend(id: String) {
        _beFriendState.value = FriendState.Loading
        viewModelScope.launch {
            sdk.beFriend(id, { success ->
                _beFriendState.value = FriendState.Success(success)
            }, { error ->
                _beFriendState.value = FriendState.Error(error ?: "Unknown error")
            })
        }
    }

    fun getMyProfile() {
        _myProfileState.value = MyProfileState.Loading
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _myProfileState.value = MyProfileState.Success(success)
            }, { error ->
                _myProfileState.value = MyProfileState.Error(error ?: "Unknown error")
            })
        }
    }

    fun getPosts(id: String) {
        _postsState.value = PostsState.Loading
        viewModelScope.launch {
            sdk.getPosts(id, { success ->
                _postsState.value = PostsState.Success(success)
            }, { error ->
                _postsState.value = PostsState.Error(error ?: "Unknown error")
            })
        }
    }

    fun sendRequestChat(otherUserID: String) {
        _sendChatRequestState.value = ChatRequestState.Loading
        viewModelScope.launch {
            sdk.sendRequestChat(otherUserID, { success ->
                _sendChatRequestState.value = ChatRequestState.Success(success)
            }, { error ->
                _sendChatRequestState.value = ChatRequestState.Error(error ?: "Unknown error")
            })
        }
    }

    fun responseRequestChat(id: String, status: Boolean, senderID: String) {
        _responseChatRequestState.value = ChatRequestState.Loading
        viewModelScope.launch {
            sdk.responseRequestChat(id, status, senderID, { success ->
                _responseChatRequestState.value = ChatRequestState.Success(success)
            }, { error ->
                _responseChatRequestState.value = ChatRequestState.Error(error ?: "Unknown error")
            })
        }
    }

    fun removeFriend(id: String) {
        _unFriendState.value = FriendState.Loading
        viewModelScope.launch {
            sdk.unFriend(id, { success ->
                _unFriendState.value = FriendState.Success(success)
            }, { error ->
                _unFriendState.value = FriendState.Error(error ?: "Unknown error")
            })
        }
    }
}

sealed class ProfilePreviewUiState {
    data object Loading : ProfilePreviewUiState()
    data class Success(val profile: ProfileModel) : ProfilePreviewUiState()
    data class Error(val message: String) : ProfilePreviewUiState()
}

sealed class PostsState {
    data object Loading : PostsState()
    data class Success(val posts: List<PostModel>) : PostsState()
    data class Error(val message: String) : PostsState()
}

sealed class ChatRequestState {
    data object Loading : ChatRequestState()
    data class Success(val message: String) : ChatRequestState()
    data class Error(val message: String) : ChatRequestState()
}

sealed class MyProfileState {
    data object Loading : MyProfileState()
    data class Success(val profile: ProfileModel) : MyProfileState()
    data class Error(val message: String) : MyProfileState()
}

sealed class FriendState {
    data object Loading : FriendState()
    data class Success(val message: String) : FriendState()
    data class Error(val message: String) : FriendState()
}
