package com.world.pockyapp.screens.home.navigations.hot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ChatRequestModel
import com.world.pockyapp.network.models.model.ConversationModel
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.StreetModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HotState<out T> {
    data object Loading : HotState<Nothing>()
    data class Success<out T>(val data: T) : HotState<T>()
    data class Error(val error: ErrorModel) : HotState<Nothing>()
}

class HotViewModel(val sdk: ApiManager) : ViewModel() {

    private var isStreetLoadingFirstTime = true

    private val _streetState =
        MutableStateFlow<HotState<List<StreetModel>>>(HotState.Loading)
    val streetState: StateFlow<HotState<List<StreetModel>>> = _streetState.asStateFlow()

    fun loadStreets() {
        viewModelScope.launch {
            if (isStreetLoadingFirstTime) {
                _streetState.value = HotState.Loading
            }

            sdk.getStreets({ success ->
                isStreetLoadingFirstTime = false
                _streetState.value = HotState.Success(success)
            }, { error ->
                isStreetLoadingFirstTime = true
                _streetState.value = HotState.Error(error)
            })
        }
    }

}
