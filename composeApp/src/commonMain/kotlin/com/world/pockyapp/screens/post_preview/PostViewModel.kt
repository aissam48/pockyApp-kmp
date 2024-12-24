package com.world.pockyapp.screens.post_preview

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class PostUiState {
    data object Idle : PostUiState()
    data object Loading : PostUiState()
    data class Success(val data: String = "", val message: String = "") : PostUiState()
    data class Error(val message: String) : PostUiState()
}

class PostViewModel(private val sdk: ApiManager) :
    ViewModel() {

    private val _uiState = MutableStateFlow<PostUiState>(PostUiState.Idle)
    val uiState: StateFlow<PostUiState> = _uiState

    fun setPost(byteArray: ByteArray) {
        viewModelScope.launch {
            sdk.setPost(byteArray, { success ->
                _uiState.value = PostUiState.Success(data = "Post successful")

            }, { error ->
                _uiState.value = PostUiState.Error(error)
            })
        }
    }
}

