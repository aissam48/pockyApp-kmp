package com.world.pockyapp.screens.report_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.screens.profile_preview.ProfilePreviewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportProfileViewModel(private val sdk: ApiManager) : ViewModel() {


    private val _profileState =
        MutableStateFlow<ProfilePreviewUiState>(ProfilePreviewUiState.Loading)
    val profileState: StateFlow<ProfilePreviewUiState> = _profileState.asStateFlow()

    private val _reportProfileState =
        MutableStateFlow<ReportProfileState>(ReportProfileState.Idle)
    val reportProfileState: StateFlow<ReportProfileState> = _reportProfileState.asStateFlow()


    fun getProfile(id: String) {
        _profileState.value = ProfilePreviewUiState.Loading
        viewModelScope.launch {
            sdk.getProfile(id, { success ->
                _profileState.value = ProfilePreviewUiState.Success(success)
            }, { error ->
                _profileState.value = ProfilePreviewUiState.Error(error)
            })
        }
    }

    fun reportProfile(id: String, content: String) {
        _reportProfileState.value = ReportProfileState.Loading
        viewModelScope.launch {
            sdk.reportProfile(id, content, { success ->
                _reportProfileState.value = ReportProfileState.Success(success.message)
            }, { error ->
                _reportProfileState.value = ReportProfileState.Error(error)
            })
        }
    }
}

sealed class ReportProfileState {
    data object Loading : ReportProfileState()
    data object Idle : ReportProfileState()
    data class Success(val message: String) : ReportProfileState()
    data class Error(val error: ErrorModel) : ReportProfileState()
}