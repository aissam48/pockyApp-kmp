package com.world.pockyapp.screens.profile_preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.profile.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilePreviewViewModel(private val sdk: ApiManager) : ViewModel() {

    private var isProfileLoadingFirstTime = true
    private var isPostsLoadingFirstTime = true

    private val _profileState =
        MutableStateFlow<ProfilePreviewUiState>(ProfilePreviewUiState.Loading)
    val profileState: StateFlow<ProfilePreviewUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<PostsState>(PostsState.Loading)
    val postsState: StateFlow<PostsState> = _postsState.asStateFlow()

    private val _sendChatRequestState = MutableStateFlow<ChatRequestState>(ChatRequestState.Loading)
    val sendChatRequestState: StateFlow<ChatRequestState> = _sendChatRequestState.asStateFlow()

    private val _responseChatRequestState =
        MutableStateFlow<ChatRequestState>(ChatRequestState.Loading)
    val responseChatRequestState: StateFlow<ChatRequestState> =
        _responseChatRequestState.asStateFlow()

    private val _myProfileState = MutableStateFlow<MyProfileState>(MyProfileState.Loading)
    val myProfileState: StateFlow<MyProfileState> = _myProfileState.asStateFlow()

    private val _beFriendState = MutableStateFlow<FriendState>(FriendState.Idil)
    val beFriendState: StateFlow<FriendState> = _beFriendState.asStateFlow()

    private val _unFriendState = MutableStateFlow<FriendState>(FriendState.Idil)
    val unFriendState: StateFlow<FriendState> = _unFriendState.asStateFlow()

    private val _blockState = MutableStateFlow<BlockState>(BlockState.Idle)
    val blockState: StateFlow<BlockState> = _blockState.asStateFlow()

    private val _unBlockState = MutableStateFlow<UnBlockState>(UnBlockState.Idle)
    val unBlockState: StateFlow<UnBlockState> = _unBlockState.asStateFlow()

    private val _unFollowState = MutableStateFlow<UnFollowState>(UnFollowState.Idle)
    val unFollowState: StateFlow<UnFollowState> = _unFollowState.asStateFlow()

    private val _followState = MutableStateFlow<FollowState>(FollowState.Idle)
    val followState: StateFlow<FollowState> = _followState.asStateFlow()



    fun getProfile(id: String) {
        if (isProfileLoadingFirstTime){
            _profileState.value = ProfilePreviewUiState.Loading
        }
        viewModelScope.launch {
            sdk.getProfile(id, { success ->
                isProfileLoadingFirstTime = false
                _profileState.value = ProfilePreviewUiState.Success(success)
            }, { error ->
                isProfileLoadingFirstTime = true
                _profileState.value = ProfilePreviewUiState.Error(error)
            })
        }
    }

    fun beFriend(id: String) {
        _beFriendState.value = FriendState.Loading
        viewModelScope.launch {
            sdk.beFriend(id, { success ->
                _beFriendState.value = FriendState.Success(success)
            }, { error ->
                _beFriendState.value = FriendState.Error(error)
            })
        }
    }

    fun getMyProfile() {
        _myProfileState.value = MyProfileState.Loading
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _myProfileState.value = MyProfileState.Success(success)
            }, { error ->
                _myProfileState.value = MyProfileState.Error(error)
            })
        }
    }

    fun getPosts(id: String) {
        if (isPostsLoadingFirstTime){
            _postsState.value = PostsState.Loading
        }
        viewModelScope.launch {
            sdk.getPosts(id, { success ->
                isPostsLoadingFirstTime = false
                _postsState.value = PostsState.Success(success)
            }, { error ->
                isPostsLoadingFirstTime = true
                _postsState.value = PostsState.Error(error)
            })
        }
    }

    fun sendRequestChat(otherUserID: String) {
        _sendChatRequestState.value = ChatRequestState.Loading
        viewModelScope.launch {
            sdk.sendRequestChat(otherUserID, { success ->
                _sendChatRequestState.value = ChatRequestState.Success(success)
            }, { error ->
                _sendChatRequestState.value = ChatRequestState.Error(error)
            })
        }
    }

    fun responseRequestChat(id: String, status: Boolean, senderID: String) {
        _responseChatRequestState.value = ChatRequestState.Loading
        viewModelScope.launch {
            sdk.responseRequestChat(id, status, senderID, { success ->
                _responseChatRequestState.value = ChatRequestState.Success(success)
            }, { error ->
                _responseChatRequestState.value = ChatRequestState.Error(error)
            })
        }
    }

    fun removeFriend(id: String) {
        _unFriendState.value = FriendState.Loading
        viewModelScope.launch {
            sdk.unFriend(id, { success ->
                _unFriendState.value = FriendState.Success(success)
            }, { error ->
                _unFriendState.value = FriendState.Error(error)
            })
        }
    }

    fun block(id: String) {
        _blockState.value = BlockState.Loading
        viewModelScope.launch {
            sdk.block(id, { success ->
                _blockState.value = BlockState.Success(success)
            }, { error ->
                _blockState.value = BlockState.Error(error)
            })
        }
    }

    fun unBlock(id: String) {
        _unBlockState.value = UnBlockState.Loading
        viewModelScope.launch {
            sdk.unBlock(id, { success ->
                _unBlockState.value = UnBlockState.Success(success)
            }, { error ->
                _unBlockState.value = UnBlockState.Error(error)
            })
        }
    }

    fun report(id: String) {


    }

    fun follow(id: String) {
        _followState.value = FollowState.Loading
        viewModelScope.launch {
            sdk.followProfile(id, { success ->
                _followState.value = FollowState.Success(success.message)
            }, { error ->
                _followState.value = FollowState.Error(error)
            })
        }
    }

    fun unFollow(id: String) {
        _unFollowState.value = UnFollowState.Loading
        viewModelScope.launch {
            sdk.unFollowProfile(id, { success ->
                _unFollowState.value = UnFollowState.Success(success.message)
            }, { error ->
                _unFollowState.value = UnFollowState.Error(error)
            })
        }
    }
}

sealed class ProfilePreviewUiState {
    data object Loading : ProfilePreviewUiState()
    data class Success(val profile: ProfileModel) : ProfilePreviewUiState()
    data class Error(val error: ErrorModel) : ProfilePreviewUiState()
}

sealed class PostsState {
    data object Loading : PostsState()
    data class Success(val posts: List<PostModel>) : PostsState()
    data class Error(val error: ErrorModel) : PostsState()
}

sealed class ChatRequestState {
    data object Loading : ChatRequestState()
    data class Success(val message: String) : ChatRequestState()
    data class Error(val error: ErrorModel) : ChatRequestState()
}

sealed class MyProfileState {
    data object Loading : MyProfileState()
    data class Success(val profile: ProfileModel) : MyProfileState()
    data class Error(val error: ErrorModel) : MyProfileState()
}

sealed class FriendState {
    data object Idil : FriendState()
    data object Loading : FriendState()
    data class Success(val message: String) : FriendState()
    data class Error(val error: ErrorModel) : FriendState()
}

sealed class BlockState {
    data object Loading : BlockState()
    data object Idle : BlockState()
    data class Success(val message: String) : BlockState()
    data class Error(val error: ErrorModel) : BlockState()
}

sealed class UnBlockState {
    data object Loading : UnBlockState()
    data object Idle : UnBlockState()
    data class Success(val message: String) : UnBlockState()
    data class Error(val error: ErrorModel) : UnBlockState()
}
sealed class FollowState {
    data object Loading : FollowState()
    data object Idle : FollowState()
    data class Success(val message: String) : FollowState()
    data class Error(val error: ErrorModel) : FollowState()
}
sealed class UnFollowState {
    data object Loading : UnFollowState()
    data object Idle : UnFollowState()
    data class Success(val message: String) : UnFollowState()
    data class Error(val error: ErrorModel) : UnFollowState()
}
