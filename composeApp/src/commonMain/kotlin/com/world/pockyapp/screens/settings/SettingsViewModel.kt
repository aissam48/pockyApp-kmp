package com.world.pockyapp.screens.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    private val _logoutState = MutableStateFlow("")
    val logoutState = _logoutState.asStateFlow()


    fun logout(){
        viewModelScope.launch {
            dataStore.edit {
                it.clear()
            }
            _logoutState.value = "logout"
        }
    }
}