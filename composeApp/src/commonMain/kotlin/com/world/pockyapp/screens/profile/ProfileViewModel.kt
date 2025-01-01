package com.world.pockyapp.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val postsState: StateFlow<PostsUiState> = _postsState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            try {
                sdk.getMyProfile({ success ->
                    _profileState.value = ProfileUiState.Success(success)

                }, { error ->
                    _profileState.value = ProfileUiState.Error(error ?: "Unknown error")

                })
            } catch (e: Exception) {
                _profileState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getMyPosts() {
        viewModelScope.launch {

            try {
                sdk.getMyPosts({ success ->
                    _postsState.value = PostsUiState.Success(success)

                }, { error ->
                    _postsState.value = PostsUiState.Error(error ?: "Unknown error")

                })
            } catch (e: Exception) {
                _postsState.value = PostsUiState.Error(e.message ?: "Unknown error")
            }

        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: ProfileModel) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class PostsUiState {
    object Loading : PostsUiState()
    data class Success(val posts: List<PostModel>) : PostsUiState()
    data class Error(val message: String) : PostsUiState()
}
