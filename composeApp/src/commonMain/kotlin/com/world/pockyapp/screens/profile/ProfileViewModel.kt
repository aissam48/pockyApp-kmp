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

class ProfileViewModel(private val sdk: ApiManager):ViewModel() {
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<List<PostModel>>(emptyList())
    val postsState: StateFlow<List<PostModel>> = _postsState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _profileState.value = ProfileUiState.Success(success)
            }, { error ->
                _profileState.value = ProfileUiState.Error(error ?: "Unknown error")
            })
        }
    }

    fun getMyPosts() {
        viewModelScope.launch {
            sdk.getMyPosts({ success ->
                _postsState.value = success
            }, { error ->
                _postsState.value = listOf()
            })
        }
    }

}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: ProfileModel) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}