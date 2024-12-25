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
import kotlinx.coroutines.launch


class MomentsViewModel(private val sdk: ApiManager) :
    ViewModel() {

    private val _viewMomentState = MutableStateFlow<String>("")
    val viewMomentState: StateFlow<String> = _viewMomentState

    fun viewMoment(momentID: String, ownerID: String) {
        viewModelScope.launch {
            sdk.viewMoment(momentID, ownerID, { success ->
                _viewMomentState.value = success.message

            }, { error ->
                _viewMomentState.value = error
            })
        }
    }
}

