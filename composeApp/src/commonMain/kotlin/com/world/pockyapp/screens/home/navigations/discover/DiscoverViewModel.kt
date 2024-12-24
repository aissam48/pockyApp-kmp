package com.world.pockyapp.screens.home.navigations.discover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.post_preview.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private val _nearbyPosts = MutableStateFlow<List<PostModel>>(emptyList())
    val nearbyPosts: StateFlow<List<PostModel>> = _nearbyPosts.asStateFlow()


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
                _nearbyPosts.value = succes
            }, { error ->
                _nearbyPosts.value = emptyList()
            })


        }
    }

}

sealed class DiscoverUiState {
    object Loading : DiscoverUiState()
    data class Success(val profile: ProfileModel) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}