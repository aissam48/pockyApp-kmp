package com.world.pockyapp.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val sdk: ApiManager):ViewModel() {
    var selectedScreen by mutableStateOf(0)

    private val _informInHomeSharedFlow = MutableSharedFlow<Boolean>()
    val informInHomeSharedFlow = _informInHomeSharedFlow.asSharedFlow()

    /*init {
        viewModelScope.launch {
            delay(1000)
            _informInHomeSharedFlow.emit(true)
            println("HomeViewModel Received informInHomeShare")

        }
    }*/

    private val _LaunchScreenState = MutableSharedFlow<Int>()
    val launchScreenState = _LaunchScreenState.asSharedFlow()
    fun launchScreen(index: Int){
        viewModelScope.launch {
            _LaunchScreenState.emit(index)
        }
    }

    fun updateFcmToken(token: String) {

        viewModelScope.launch {
            sdk.updateFcmToken(tokenFcm = token,{},{})
        }

    }
}