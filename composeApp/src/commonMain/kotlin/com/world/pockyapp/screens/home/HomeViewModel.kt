package com.world.pockyapp.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.localDB.ProfileDB
import com.world.pockyapp.network.localDB.ProfileDoa
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.home.navigations.discover.UiState
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HomeViewModel(private val sdk: ApiManager, private val localDB: ProfileDoa):ViewModel() {
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


    private val _profileState = MutableStateFlow<ResponseState<ProfileModel>>(ResponseState.Loading)
    val profileState: StateFlow<ResponseState<ProfileModel>> = _profileState.asStateFlow()
    var isProfileLoadingFirstTime = true
    fun getProfile() {
        viewModelScope.launch {
            if (isProfileLoadingFirstTime) {
                _profileState.value = ResponseState.Loading
            }

            try {
                sdk.getMyProfile(
                    onSuccess = { profile ->
                        isProfileLoadingFirstTime = false
                        _profileState.value = ResponseState.Success(profile)
                        this.launch(Dispatchers.IO){
                            localDB.saveProfile(profile)
                        }


                    },
                    onFailure = { error ->
                        isProfileLoadingFirstTime = true
                        _profileState.value = ResponseState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _profileState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    fun saveProfileLocalDB(profile: ProfileModel) {

    }
}