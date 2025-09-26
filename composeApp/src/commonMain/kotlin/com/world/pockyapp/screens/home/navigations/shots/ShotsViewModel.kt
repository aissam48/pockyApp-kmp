package com.world.pockyapp.screens.home.navigations.shots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ResponseMessageModel
import com.world.pockyapp.network.models.model.ShotModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShotsViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _getShotsState = MutableStateFlow<ResponseState<List<ShotModel>>>(ResponseState.Idle)
    val getShotsState = _getShotsState.asStateFlow()

    fun getShots(){
        viewModelScope.launch {
            _getShotsState.value = ResponseState.Loading
            sdk.getShots({
                _getShotsState.value = ResponseState.Success(it)
            },{
                _getShotsState.value = ResponseState.Error(it)
            })
        }
    }


}