package com.world.pockyapp.screens.followers.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendsViewModel(private val sdk: ApiManager) : ViewModel() {

    val allFriends = mutableSetOf<ProfileModel>()
    private val _friendsState =
        MutableStateFlow<ResponseState<List<ProfileModel>>>(ResponseState.Idle)
    val friendsState = _friendsState.asStateFlow()
    fun getFriends(id: String) {

        viewModelScope.launch {
            _friendsState.value = ResponseState.Loading
            sdk.getFriends(id, {
                allFriends.addAll(it)
                _friendsState.value = ResponseState.Success(allFriends.toList())
            }, {
                _friendsState.value = ResponseState.Error(it)
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