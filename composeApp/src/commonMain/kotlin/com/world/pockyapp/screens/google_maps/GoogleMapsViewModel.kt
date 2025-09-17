package com.world.pockyapp.screens.google_maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.home.navigations.discover.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoogleMapsViewModel(val sdk: ApiManager): ViewModel() {

    private var isGlobalMomentsLoadingFirstTime = true

    private val _globalMomentsState = MutableStateFlow<UiState<List<MomentModel>>>(UiState.Loading)

    val globalMomentsState: StateFlow<UiState<List<MomentModel>>> =
        _globalMomentsState.asStateFlow()

    fun loadGlobalMoments() {
        viewModelScope.launch {
            if (isGlobalMomentsLoadingFirstTime) {
                _globalMomentsState.value = UiState.Loading
            }
            try {
                sdk.getGlobalMoments(
                    onSuccess = { moments ->
                        isGlobalMomentsLoadingFirstTime = false
                        _globalMomentsState.value = UiState.Success(moments)
                    },
                    onFailure = { error ->
                        isGlobalMomentsLoadingFirstTime = true
                        _globalMomentsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _globalMomentsState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }


}