package com.world.pockyapp.screens.home.navigations.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ChatRequestModel
import com.world.pockyapp.network.models.model.ConversationModel
import com.world.pockyapp.network.models.model.ErrorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    data class Success<out T>(val data: T) : UIState<T>()
    data class Error(val error: ErrorModel) : UIState<Nothing>()
}

class ConversationsViewModel(val sdk: ApiManager) : ViewModel() {

    private var isChatRequestLoadingFirstTime = true
    private var isConversationsLoadingFirstTime = true

    private val _chatRequestsMoments =
        MutableStateFlow<UIState<List<ChatRequestModel>>>(UIState.Loading)
    val chatRequestsMoments: StateFlow<UIState<List<ChatRequestModel>>> =
        _chatRequestsMoments.asStateFlow()

    private val _conversationsState =
        MutableStateFlow<UIState<List<ConversationModel>>>(UIState.Loading)
    val conversationsState: StateFlow<UIState<List<ConversationModel>>> =
        _conversationsState.asStateFlow()

    fun loadChatRequests() {
        viewModelScope.launch {
            if (isChatRequestLoadingFirstTime) {
                _chatRequestsMoments.value = UIState.Loading
            }

            sdk.getChatRequests({ success ->
                isChatRequestLoadingFirstTime = false
                _chatRequestsMoments.value = UIState.Success(success)
            }, { error ->
                isChatRequestLoadingFirstTime = true
                _chatRequestsMoments.value = UIState.Error(error)
            })
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            if (isConversationsLoadingFirstTime) {
                _conversationsState.value = UIState.Loading
            }
            sdk.getConversations({ success ->
                isConversationsLoadingFirstTime = false
                _conversationsState.value = UIState.Success(success)
            }, { error ->
                isConversationsLoadingFirstTime = true
                _conversationsState.value = UIState.Error(error)
            })
        }
    }
}
