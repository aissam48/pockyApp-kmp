package com.world.pockyapp.screens.blocked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BlockedUiState {
    data object Loading : BlockedUiState()
    data class Success(val data: List<ProfileModel> = listOf(), val message: String = "") :
        BlockedUiState()

    data class Error(val error: ErrorModel) : BlockedUiState()
}

sealed class UnBlockUiState {
    data object Loading : UnBlockUiState()
    data class Success(val data: String = "", val message: String = "") : UnBlockUiState()
    data class Error(val error: ErrorModel) : UnBlockUiState()
}

class BlockedViewModel(val sdk: ApiManager) : ViewModel() {

    private val _blockedState =
        MutableStateFlow<BlockedUiState>(BlockedUiState.Loading)
    val blockedState: StateFlow<BlockedUiState> = _blockedState.asStateFlow()

    private val _unBlockState =
        MutableStateFlow<UnBlockUiState>(UnBlockUiState.Loading)
    val unBlockState: StateFlow<UnBlockUiState> = _unBlockState.asStateFlow()

    fun getBlockedUsers() {
        _blockedState.value = BlockedUiState.Loading
        viewModelScope.launch {
            sdk.getBlockedUsers({ success ->
                _blockedState.value = BlockedUiState.Success(success)
            }, { error ->
                _blockedState.value =
                    BlockedUiState.Error(error)
            })
        }
    }

    fun unBlock(id: String) {
        _unBlockState.value = UnBlockUiState.Loading
        viewModelScope.launch {
            sdk.unBlock(id, { success ->
                _unBlockState.value = UnBlockUiState.Success(success)
            }, { error ->
                _unBlockState.value =
                    UnBlockUiState.Error(error)
            })
        }
    }

}