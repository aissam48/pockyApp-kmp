package com.world.pockyapp.screens.followers.followings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FollowingsViewModel(private val sdk: ApiManager) : ViewModel() {

    val allFollowings = mutableSetOf<ProfileModel>()
    private val _followingsState =
        MutableStateFlow<ResponseState<List<ProfileModel>>>(ResponseState.Idle)
    val followingsState = _followingsState.asStateFlow()
    fun getFollowings(id: String) {

        viewModelScope.launch {
            _followingsState.value = ResponseState.Loading
            sdk.getFollowings(id, {
                allFollowings.addAll(it)
                _followingsState.value = ResponseState.Success(allFollowings.toList())
            }, {
                _followingsState.value = ResponseState.Error(it)
            })
        }
    }


    private val _myProfileState = MutableStateFlow<ResponseState<ProfileModel>>(ResponseState.Idle)
    val myProfileState: StateFlow<ResponseState<ProfileModel>> = _myProfileState.asStateFlow()
    fun getMyProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _myProfileState.value = ResponseState.Success(success)
            }, { error ->
                _myProfileState.value = ResponseState.Error(error)
            })
        }
    }

}