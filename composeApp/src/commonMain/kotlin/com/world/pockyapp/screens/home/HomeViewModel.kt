package com.world.pockyapp.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.world.pockyapp.network.ApiManager

class HomeViewModel(private val sdk: ApiManager):ViewModel() {
    var selectedScreen by mutableStateOf(0)
}