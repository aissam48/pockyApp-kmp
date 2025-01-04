package com.world.pockyapp.screens.moment_screen

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MomentsViewModel(private val sdk: ApiManager) :
    ViewModel() {

    private val _viewMomentState = MutableStateFlow<String>("")
    val viewMomentState: StateFlow<String> = _viewMomentState

    private val _likeState = MutableStateFlow<String>("")
    val likeState = _likeState.asStateFlow()

    private val _unLikeState = MutableStateFlow<String>("")
    val unLikeState = _unLikeState.asStateFlow()

    private val _deleteState = MutableStateFlow<String>("")
    val deleteState = _deleteState.asStateFlow()

    fun viewMoment(momentID: String, ownerID: String) {
        viewModelScope.launch {
            sdk.viewMoment(momentID, ownerID, { success ->
                _viewMomentState.value = success.message

            }, { error ->
                _viewMomentState.value = error.message
            })
        }
    }


    fun deleteMoment(momentID: String) {
        viewModelScope.launch {
            sdk.deleteMoment(momentID, { success ->
                _deleteState.value = success
            }, { error ->
                _deleteState.value = error.message
            })
        }
    }

    fun like(momentID: String) {
        viewModelScope.launch {
            sdk.likeMoment(momentID, { success ->
                _likeState.value = success
            }, { error ->
                _likeState.value = error.message
            })
        }
    }

    fun unLike(momentID: String) {
        viewModelScope.launch {
            sdk.unLikeMoment(momentID, { success ->
                _unLikeState.value = success
            }, { error ->
                _unLikeState.value = error.message
            })
        }
    }
}

