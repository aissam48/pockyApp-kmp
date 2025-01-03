package com.world.pockyapp.screens.splash_screen

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel (private val dataStore: DataStore<Preferences>):ViewModel(){

    private val _splashState = MutableStateFlow<String>("")
    val splashState = _splashState.asStateFlow()

    init {
        checkToken()
    }
    private fun checkToken(){
        viewModelScope.launch {
            val preferences = dataStore.edit { }
            val token = preferences[stringPreferencesKey("token")]
            println("SplashViewModel $token")
            if (token?.isEmpty() == true || token == null){
                _splashState.value = "login"
            }else{
                _splashState.value = "home"
            }
        }

    }

}