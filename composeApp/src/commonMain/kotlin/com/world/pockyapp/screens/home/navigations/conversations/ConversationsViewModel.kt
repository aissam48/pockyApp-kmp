package com.world.pockyapp.screens.home.navigations.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ChatRequestModel
import com.world.pockyapp.network.models.model.ConversationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationsViewModel(val sdk: ApiManager) : ViewModel() {

    private val _chatRequestsMoments = MutableStateFlow<List<ChatRequestModel>>(emptyList())
    val chatRequestsMoments: StateFlow<List<ChatRequestModel>> = _chatRequestsMoments.asStateFlow()

    private val _conversationsState = MutableStateFlow<List<ConversationModel>>(emptyList())
    val conversationsState: StateFlow<List<ConversationModel>> = _conversationsState.asStateFlow()

    fun loadChatRequests() {
        viewModelScope.launch {
            sdk.getChatRequests({ succes ->
                _chatRequestsMoments.value = succes
            }, { error ->
                _chatRequestsMoments.value = emptyList()
            })

        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            sdk.getConversations({ succes ->
                _conversationsState.value = succes
            }, { error ->
                _conversationsState.value = emptyList()
            })

        }
    }

}