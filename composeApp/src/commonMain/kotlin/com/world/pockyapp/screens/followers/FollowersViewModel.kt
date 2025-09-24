package com.world.pockyapp.screens.followers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FollowersViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _followersState =
        MutableStateFlow<ResponseState<List<ProfileModel>>>(ResponseState.Idle)
    val followersState = _followersState.asStateFlow()
    fun getFollowers(id: String) {

        viewModelScope.launch {
            _followersState.value = ResponseState.Loading
            sdk.getFollowers(id, {
                _followersState.value = ResponseState.Success(it)
            }, {
                _followersState.value = ResponseState.Error(it)
            })
        }
    }

}