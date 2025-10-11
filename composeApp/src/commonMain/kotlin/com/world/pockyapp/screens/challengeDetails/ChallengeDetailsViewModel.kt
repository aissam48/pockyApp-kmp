package com.world.pockyapp.screens.challengeDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ChallengeModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChallengeDetailsViewModel(private val sdk: ApiManager): ViewModel() {


    private val _challengeDetailsState = MutableStateFlow<ResponseState<ChallengeModel>>(
        ResponseState.Idle)
    val challengeDetailsState = _challengeDetailsState.asStateFlow()

    fun getChallengeDetails(challengeId: String) {
        viewModelScope.launch {
            println("rerererere 2")

            _challengeDetailsState.value = ResponseState.Loading

            sdk.getChallengeDetails(challengeId,{
                _challengeDetailsState.value = ResponseState.Success(it)
            },{
                _challengeDetailsState.value = ResponseState.Error(it)
            })
        }
    }
}