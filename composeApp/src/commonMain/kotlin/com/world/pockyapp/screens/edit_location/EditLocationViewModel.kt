package com.world.pockyapp.screens.edit_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.edit_profile.EditProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class EditLocationUiState {
    data object Idle : EditLocationUiState()
    data object Loading : EditLocationUiState()
    data class Success(val data: String = "", val message: String = "") : EditLocationUiState()
    data class Error(val message: String) : EditLocationUiState()
}

class EditLocationViewModel(private val sdk: ApiManager) :
    ViewModel() {

    private val _profileState = MutableStateFlow<ProfileModel?>(null)
    val profileState: StateFlow<ProfileModel?> = _profileState.asStateFlow()

    init {
        getProfile()
    }

    fun getProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _profileState.value = success
            }, { error ->
                _profileState.value = null
            })
        }
    }

    private val _uiState = MutableStateFlow<EditLocationUiState>(EditLocationUiState.Idle)
    val uiState: StateFlow<EditLocationUiState> = _uiState

    var country: String = ""
    var city: String = ""

    fun editLocation() {

        if (country.isEmpty()) {
            _uiState.value = EditLocationUiState.Error("Invalid country")
            return
        }

        if (city.isEmpty() || city == "City") {
            _uiState.value = EditLocationUiState.Error("Invalid city")
            return
        }


        viewModelScope.launch {
            _uiState.value = EditLocationUiState.Loading
            sdk.editLocation(country, city, { success ->
                _uiState.value = EditLocationUiState.Success(data = "EditLocation successful")

            }, { error ->
                _uiState.value = EditLocationUiState.Error(error)
            })
        }
    }

    private val _countriesState = MutableStateFlow<List<DataModel>>(emptyList())
    val countriesState: StateFlow<List<DataModel>> = _countriesState

    init {
        getCountries()
    }

    private fun getCountries() {
        viewModelScope.launch {

            sdk.getCountriesAndCities({ succes ->
                _countriesState.value = succes
            }, {

            })

        }
    }
}

