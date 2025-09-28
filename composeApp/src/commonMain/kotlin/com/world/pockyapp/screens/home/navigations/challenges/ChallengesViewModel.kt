package com.world.pockyapp.screens.home.navigations.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ChallengeModel
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.network.models.model.StreetModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChallengesViewModel(val sdk: ApiManager) : ViewModel() {

    private var isChallengesLoadedFirstTime = true

    private val _challengestState =
        MutableStateFlow<ResponseState<List<ChallengeModel>>>(ResponseState.Loading)
    val challengesState: StateFlow<ResponseState<List<ChallengeModel>>> = _challengestState.asStateFlow()

    private val _profileState = MutableStateFlow<ResponseState<ProfileModel>>(ResponseState.Loading)
    val profileState: StateFlow<ResponseState<ProfileModel>> = _profileState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            try {
                sdk.getMyProfile(
                    onSuccess = { profile ->
                        _profileState.value = ResponseState.Success(profile)
                    },
                    onFailure = { error ->
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

    fun loadChallenges() {
        try {
            viewModelScope.launch {
                if (isChallengesLoadedFirstTime) {
                    _challengestState.value = ResponseState.Loading
                }

                sdk.getChallenges({ success ->
                    isChallengesLoadedFirstTime = false
                    _challengestState.value = ResponseState.Success(success)
                }, { error ->
                    isChallengesLoadedFirstTime = true
                    _challengestState.value = ResponseState.Error(error)
                })
            }
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
