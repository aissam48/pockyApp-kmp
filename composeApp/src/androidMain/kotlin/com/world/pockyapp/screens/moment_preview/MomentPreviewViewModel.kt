package com.world.pockyapp.screens.moment_preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.GeoLocationModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class MomentPreviewUiState {
    data object Idle : MomentPreviewUiState()
    data object Loading : MomentPreviewUiState()
    data class Success(val data: String = "", val message: String = "") : MomentPreviewUiState()
    data class Error(val error: ErrorModel) : MomentPreviewUiState()
}

class MomentPreviewViewModel(private val sdk: ApiManager) :
    ViewModel() {

    private val _profileState = MutableStateFlow<ProfileModel?>(null)
    val profileState: StateFlow<ProfileModel?> = _profileState.asStateFlow()

    private val _uiState = MutableStateFlow<MomentPreviewUiState>(MomentPreviewUiState.Idle)
    val uiState: StateFlow<MomentPreviewUiState> = _uiState


    fun shareMoment(byteArray: ByteArray?, isNearby: Boolean, geoLocationModel: GeoLocationModel) {

        viewModelScope.launch {
            _uiState.value = MomentPreviewUiState.Loading
            sdk.shareMoment(byteArray,isNearby,geoLocationModel, { success ->
                _uiState.value = MomentPreviewUiState.Success()

            }, { error ->
                _uiState.value = MomentPreviewUiState.Error(error)
            })
        }
    }
}

