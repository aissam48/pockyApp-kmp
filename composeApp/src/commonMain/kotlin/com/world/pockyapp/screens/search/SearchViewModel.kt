package com.world.pockyapp.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val sdk: ApiManager) : ViewModel() {

    private val _profilesState = MutableStateFlow<List<ProfileModel>?>(null)
    val profilesState: StateFlow<List<ProfileModel>?> = _profilesState.asStateFlow()


    fun search(keyword: String) {
        viewModelScope.launch {
            sdk.search(keyword, { success ->
                _profilesState.value = success
            }, { error ->
                _profilesState.value = null
            })
        }
    }

}