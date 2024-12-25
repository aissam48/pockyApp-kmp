package com.world.pockyapp.screens.home.navigations.discover

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class DiscoverViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _friendsMoments = MutableStateFlow<List<ProfileModel>>(emptyList())
    val friendsMoments: StateFlow<List<ProfileModel>> = _friendsMoments.asStateFlow()

    private val _nearbyMoments = MutableStateFlow<List<ProfileModel>>(emptyList())
    val nearbyMoments: StateFlow<List<ProfileModel>> = _nearbyMoments.asStateFlow()

    private val _nearbyPosts = MutableStateFlow<MutableList<PostModel>>(mutableListOf())
    val nearbyPosts: StateFlow<MutableList<PostModel>> = _nearbyPosts.asStateFlow()

    private val _likePost = MutableStateFlow<String>("")
    val likePost: StateFlow<String> = _likePost.asStateFlow()

    private val _unLikePost = MutableStateFlow<String>("")
    val unLikePost: StateFlow<String> = _unLikePost.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileModel?>(ProfileModel())
    val profileState: StateFlow<ProfileModel?> = _profileState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _profileState.value = success
            }, { error ->
                _profileState.value = null
            })
        }
    }

    fun likePost(postID:String) {
        viewModelScope.launch {
            sdk.like(postID,{ succes ->
                _likePost.value = succes
            }, { error ->
                _likePost.value = error
            })

        }
    }

    fun unLikePost(postID:String) {
        viewModelScope.launch {
            sdk.unLike(postID,{ succes ->
                _unLikePost.value = succes
            }, { error ->
                _unLikePost.value = error
            })

        }
    }

    fun loadFriendsMoments() {
        viewModelScope.launch {
            sdk.getFriendsMoments({ succes ->
                _friendsMoments.value = succes
            }, { error ->
                _friendsMoments.value = emptyList()
            })

        }
    }

    fun loadNearbyMoments() {
        viewModelScope.launch {
            sdk.getNearbyMoments({ succes ->
                _nearbyMoments.value = succes
            }, { error ->
                _nearbyMoments.value = emptyList()
            })

        }
    }

    fun loadNearbyPosts() {
        viewModelScope.launch {
            sdk.getNearbyPosts({ succes ->
                _nearbyPosts.value = succes.toMutableList()
            }, { error ->
                _nearbyPosts.value = mutableListOf()
            })


        }
    }

    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            _nearbyPosts.update { posts ->
                posts.map { post ->
                    if (post.postID == postId) {
                        val updatedLikes = post.likes.toMutableList()
                        if (updatedLikes.contains(userId)) {
                            updatedLikes.remove(userId)
                            this@DiscoverViewModel.sdk.unLike(postId,{},{})
                        } else {
                            updatedLikes.add(userId)
                            this@DiscoverViewModel.sdk.like(postId,{},{})
                        }
                        post.copy(likes = updatedLikes)
                    } else {
                        post
                    }
                }.toMutableList()
            }
        }

    }
}

sealed class DiscoverUiState {
    object Loading : DiscoverUiState()
    data class Success(val profile: ProfileModel) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}