package com.world.pockyapp.screens.post_preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.GeoLocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PostUiState {
    data object Idle : PostUiState()
    data object Loading : PostUiState()
    data class Success(val data: String = "", val message: String = "") : PostUiState()
    data class Error(val error: ErrorModel) : PostUiState()
}

class PostViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _uiState = MutableStateFlow<PostUiState>(PostUiState.Idle)
    val uiState: StateFlow<PostUiState> = _uiState

    fun setPost(byteArray: ByteArray, isNearby: Boolean, geoLocationModel: GeoLocationModel) {
        _uiState.value = PostUiState.Loading
        viewModelScope.launch {
            sdk.setPost(byteArray,isNearby,geoLocationModel, {
                _uiState.value = PostUiState.Success()
            }, { error ->
                _uiState.value = PostUiState.Error(error)
            })
        }
    }
}
