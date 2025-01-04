package com.world.pockyapp.screens.home.navigations.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: ErrorModel) : UiState<Nothing>()
}

data class LikeAction(
    val postId: String,
    val isLiked: Boolean
)

class DiscoverViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<ProfileModel>>(UiState.Loading)
    val profileState: StateFlow<UiState<ProfileModel>> = _profileState.asStateFlow()

    private val _friendsMomentsState =
        MutableStateFlow<UiState<List<ProfileModel>>>(UiState.Loading)
    val friendsMomentsState: StateFlow<UiState<List<ProfileModel>>> =
        _friendsMomentsState.asStateFlow()

    private val _nearbyMomentsState = MutableStateFlow<UiState<List<ProfileModel>>>(UiState.Loading)
    val nearbyMomentsState: StateFlow<UiState<List<ProfileModel>>> =
        _nearbyMomentsState.asStateFlow()

    private val _nearbyPostsState = MutableStateFlow<UiState<List<PostModel>>>(UiState.Loading)
    val nearbyPostsState: StateFlow<UiState<List<PostModel>>> = _nearbyPostsState.asStateFlow()

    private val _likeActionState = MutableStateFlow<UiState<LikeAction>?>(null)
    val likeActionState: StateFlow<UiState<LikeAction>?> = _likeActionState.asStateFlow()


    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            try {
                sdk.getMyProfile(
                    onSuccess = { profile ->
                        _profileState.value = UiState.Success(profile)
                    },
                    onFailure = { error ->
                        _profileState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _profileState.value = UiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }
        }
    }

    fun loadFriendsMoments() {
        viewModelScope.launch {
            _friendsMomentsState.value = UiState.Loading
            try {
                sdk.getFriendsMoments(
                    onSuccess = { moments ->
                        _friendsMomentsState.value = UiState.Success(moments)
                    },
                    onFailure = { error ->
                        _friendsMomentsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _friendsMomentsState.value = UiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }
        }
    }

    fun loadNearbyMoments() {
        viewModelScope.launch {
            _nearbyMomentsState.value = UiState.Loading
            try {
                sdk.getNearbyMoments(
                    onSuccess = { moments ->
                        _nearbyMomentsState.value = UiState.Success(moments)
                    },
                    onFailure = { error ->
                        _nearbyMomentsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _nearbyMomentsState.value = UiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }
        }
    }

    fun loadNearbyPosts() {
        viewModelScope.launch {
            _nearbyPostsState.value = UiState.Loading
            try {
                sdk.getNearbyPosts(
                    onSuccess = { posts ->
                        _nearbyPostsState.value = UiState.Success(posts)
                    },
                    onFailure = { error ->
                        _nearbyPostsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _nearbyPostsState.value = UiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            _likeActionState.value = UiState.Loading
            try {
                val currentPosts =
                    (_nearbyPostsState.value as? UiState.Success)?.data ?: return@launch
                val updatedPosts = currentPosts.map { post ->
                    if (post.postID == postId) {
                        val updatedLikes = post.likes.toMutableList()
                        val isLiked = if (updatedLikes.contains(userId)) {
                            updatedLikes.remove(userId)
                            sdk.unLike(postId, {}, {})
                            false
                        } else {
                            updatedLikes.add(userId)
                            sdk.like(postId, {}, {})
                            true
                        }
                        _likeActionState.value = UiState.Success(LikeAction(postId, isLiked))
                        post.copy(likes = updatedLikes)
                    } else {
                        post
                    }
                }
                _nearbyPostsState.value = UiState.Success(updatedPosts)
            } catch (e: Exception) {
                _likeActionState.value = UiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }
        }
    }
}