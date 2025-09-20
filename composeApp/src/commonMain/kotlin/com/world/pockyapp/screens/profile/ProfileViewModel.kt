package com.world.pockyapp.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val sdk: ApiManager) : ViewModel() {

    private var isProfileLoadingFirstTime = true
    private var isPostsLoadingFirstTime = true
    private var isMomentsLoadingFirstTime = true

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val postsState: StateFlow<PostsUiState> = _postsState.asStateFlow()

    private val _myMomentsState =
        MutableStateFlow<MyMomentsState>(MyMomentsState.Idle)
    val myMomentsState: StateFlow<MyMomentsState> = _myMomentsState.asStateFlow()

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
                        message = "Network error. Please try again later.",
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
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }

        }
    }

    fun getMyMoments() {
        if (isMomentsLoadingFirstTime){
            _myMomentsState.value = MyMomentsState.Loading
        }
        viewModelScope.launch {
            sdk.getMyMoments( { success ->
                isMomentsLoadingFirstTime = false
                _myMomentsState.value = MyMomentsState.Success(success)
            }, { error ->
                isMomentsLoadingFirstTime = true
                _myMomentsState.value = MyMomentsState.Error(error)
            })
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

sealed class MyMomentsState {
    data object Loading : MyMomentsState()
    data object Idle : MyMomentsState()
    data class Success(val moments: List<MomentModel>) : MyMomentsState()
    data class Error(val error: ErrorModel) : MyMomentsState()
}