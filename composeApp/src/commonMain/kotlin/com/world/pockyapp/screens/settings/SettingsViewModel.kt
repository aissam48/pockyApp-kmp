package com.world.pockyapp.screens.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val sdk:ApiManager,private val dataStore: DataStore<Preferences>) : ViewModel() {

    private val _logoutState = MutableStateFlow("")
    val logoutState = _logoutState.asStateFlow()

    private val _deleteAccountState = MutableStateFlow("")
    val deleteAccountState = _deleteAccountState.asStateFlow()


    fun logout(){
        viewModelScope.launch {
            dataStore.edit {
                it.clear()
            }
            _logoutState.value = "logout"
        }
    }

    fun deleteAccount(){
        viewModelScope.launch {
            sdk.deleteAccount({success->
                viewModelScope.launch {
                    dataStore.edit {
                        it.clear()
                    }
                }
                _deleteAccountState.value = "deleteAccount"
            },{error->
                _deleteAccountState.value = ""
            })
        }
    }
}