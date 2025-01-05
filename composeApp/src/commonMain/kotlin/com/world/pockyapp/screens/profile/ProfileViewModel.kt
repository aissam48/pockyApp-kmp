package com.world.pockyapp.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val sdk: ApiManager) : ViewModel() {

    private var isProfileLoadingFirstTime = true
    private var isPostsLoadingFirstTime = true

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val postsState: StateFlow<PostsUiState> = _postsState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            if (isProfileLoadingFirstTime){
                _profileState.value = ProfileUiState.Loading
            }
            try {
                sdk.getMyProfile({ success ->
                    isProfileLoadingFirstTime = false
                    _profileState.value = ProfileUiState.Success(success)

                }, { error ->
                    isProfileLoadingFirstTime = true
                    _profileState.value = ProfileUiState.Error(error)

                })
            } catch (e: Exception) {
                isProfileLoadingFirstTime = true
                _profileState.value = ProfileUiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }
        }
    }

    fun getMyPosts() {
        viewModelScope.launch {
            if (isPostsLoadingFirstTime){
                _postsState.value = PostsUiState.Loading
            }
            try {
                sdk.getMyPosts({ success ->
                    isPostsLoadingFirstTime = false
                    _postsState.value = PostsUiState.Success(success)

                }, { error ->
                    isPostsLoadingFirstTime = true
                    _postsState.value = PostsUiState.Error(error)

                })
            } catch (e: Exception) {
                isPostsLoadingFirstTime = true
                _postsState.value = PostsUiState.Error(
                    error = ErrorModel(
                        message = e.message ?: "Unknown error",
                        code = 500
                    )
                )
            }

        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: ProfileModel) : ProfileUiState()
    data class Error(val error: ErrorModel) : ProfileUiState()
}

sealed class PostsUiState {
    object Loading : PostsUiState()
    data class Success(val posts: List<PostModel>) : PostsUiState()
    data class Error(val error: ErrorModel) : PostsUiState()
}
