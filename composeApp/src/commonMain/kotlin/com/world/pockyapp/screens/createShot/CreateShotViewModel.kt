package com.world.pockyapp.screens.createShot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.GeoLocationModel
import com.world.pockyapp.network.models.model.ResponseMessageModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateShotViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _createShotState = MutableStateFlow<ResponseState<ResponseMessageModel>>(ResponseState.Idle)
    val createShotState = _createShotState.asStateFlow()

    fun shareShot(bytes: ByteArray, isChecked: Boolean, extension: String, geoLocationModel: GeoLocationModel){
        viewModelScope.launch {
            _createShotState.value = ResponseState.Loading
            sdk.shareShot(bytes, isChecked, geoLocationModel,extension,{
                _createShotState.value = ResponseState.Success(it)
            },{
                _createShotState.value = ResponseState.Error(it)
            })


        }
    }
}