package com.world.pockyapp.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.MessageModel
import com.world.pockyapp.network.models.model.ProfileModel
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


sealed class ChatUiState {
    data object Idle : ChatUiState()
    data object Loading : ChatUiState()
    data class Success(val data: String = "", val message: String = "") : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(private val sdk: ApiManager) :
    ViewModel() {


    var conversationID = ""

    private val _profileState = MutableStateFlow<ProfileModel?>(ProfileModel())
    val profileState: StateFlow<ProfileModel?> = _profileState.asStateFlow()

    private val _myProfileState = MutableStateFlow<ProfileModel?>(ProfileModel())
    val myProfileState: StateFlow<ProfileModel?> = _myProfileState.asStateFlow()

    private val _messagesState = MutableStateFlow<List<MessageModel>>(emptyList())
    val messagesState: StateFlow<List<MessageModel>> = _messagesState

    private val _newMessageState = MutableStateFlow<MessageModel?>(null)
    val newMessageState: StateFlow<MessageModel?> = _newMessageState

    private val _cancelConversationState = MutableStateFlow<String>("")
    val cancelConversationState: StateFlow<String> = _cancelConversationState.asStateFlow()

    fun cancelConversation(
        conversationID: String,
        chatRequestID: String,
    ) {
        viewModelScope.launch {
            sdk.cancelConversation(conversationID, chatRequestID, { succes ->
                _cancelConversationState.value = succes
            }, { error ->
                _cancelConversationState.value = ""
            })

        }
    }

    fun getMessages(conversationID: String) {
        viewModelScope.launch {
            sdk.getMessages(conversationID, { success ->
                _messagesState.value = success
            }, { error ->
                _messagesState.value = emptyList()
            })
        }
    }

    fun getProfile(id: String) {
        viewModelScope.launch {
            sdk.getProfile(id, { success ->
                _profileState.value = success
            }, { error ->
                _profileState.value = null
            })
        }
    }

    fun getMyProfile() {
        viewModelScope.launch {
            sdk.getMyProfile({ success ->
                _myProfileState.value = success
            }, { error ->
                _myProfileState.value = null
            })
        }
    }

    init {

        viewModelScope.launch {
            delay(1000)
            for (frame in sdk.ws.incoming) {
                frame as Frame.Text ?: continue
                println("check data send -> " + frame.readText())
                try {
                    val textContent = frame.readText()
                    val message = Json.decodeFromString<MessageModel>(textContent)
                    if (message.conversationID == conversationID){
                        _newMessageState.value = message
                    }
                } catch (e: SerializationException) {
                    println("Failed to parse message: ${e.message}")
                }
            }
        }
    }

    fun sendMessage(data: MessageModel) {
        viewModelScope.launch {
            sdk.sendMessage(data, {

            }, {

            })
        }
    }

}

