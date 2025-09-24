package com.world.pockyapp.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.Constant
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.MessageModel
import com.world.pockyapp.network.models.model.ProfileModel
import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject

import kotlinx.serialization.json.put

sealed class ChatUiState {
    data object Idle : ChatUiState()
    data object Loading : ChatUiState()
    data class Success(val data: String = "", val message: String = "") : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

sealed class CancelChatUiState {
    data object Idle : CancelChatUiState()
    data object Loading : CancelChatUiState()
    data class Success(val data: String = "", val message: String = "") : CancelChatUiState()
    data class Error(val error: ErrorModel) : CancelChatUiState()
}

class ChatViewModel(private val sdk: ApiManager) : ViewModel() {


    var conversationID = ""

    private val _profileState = MutableStateFlow<ProfileModel?>(ProfileModel())
    val profileState: StateFlow<ProfileModel?> = _profileState.asStateFlow()

    private val _myProfileState = MutableStateFlow<ProfileModel?>(ProfileModel())
    val myProfileState: StateFlow<ProfileModel?> = _myProfileState.asStateFlow()

    private val _messagesState = MutableStateFlow<List<MessageModel>>(emptyList())
    val messagesState: StateFlow<List<MessageModel>> = _messagesState

    private val _newMessageState = MutableStateFlow<MessageModel?>(null)
    val newMessageState: StateFlow<MessageModel?> = _newMessageState

    private val _cancelConversationState =
        MutableStateFlow<CancelChatUiState>(CancelChatUiState.Idle)
    val cancelConversationState: StateFlow<CancelChatUiState> =
        _cancelConversationState.asStateFlow()

    private val _subscribeConversationSharedFlow = MutableSharedFlow<String>()
    val subscribeConversationSharedFlow = _subscribeConversationSharedFlow.asSharedFlow()

    private val _unSubscribeConversationSharedFlow = MutableSharedFlow<String>()
    val unSubscribeConversationSharedFlow = _unSubscribeConversationSharedFlow.asSharedFlow()

    fun cancelConversation(
        conversationID: String,
        chatRequestID: String,
    ) {
        viewModelScope.launch {
            sdk.cancelConversation(conversationID, chatRequestID, { succes ->
                _cancelConversationState.value = CancelChatUiState.Success()
            }, { error ->
                _cancelConversationState.value = CancelChatUiState.Error(error)
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

    lateinit var socket: Socket


    fun unSubscribeToConversation(conversationID: String) {
        viewModelScope.launch {
            delay(1000)
            _unSubscribeConversationSharedFlow.emit(conversationID)
            println("ChatViewModel unSubscribe $conversationID")
        }
    }

    fun subscribeToConversation(conversationID: String) {
        viewModelScope.launch {
            delay(1000)
            _subscribeConversationSharedFlow.emit(conversationID)
            println("ChatViewModel subscribe $conversationID")
        }
    }


    fun sendMessage(data: MessageModel) {

        print("sendddddd 0")

        viewModelScope.launch {
            print("sendddddd 1")
            try {
                // Build JsonObject manually
                val messageJsonObject = buildJsonObject {
                    put("senderID", data.senderID)
                    put("content", data.content)
                    put("conversationID", data.conversationID)
                    put("id", data.id)
                }

                socket.emit("send_message", messageJsonObject)

                println("Sent message: ${Json.encodeToString(data)}")
            } catch (e: Exception) {
                println("Failed to send message: $e")
            }
        }
    }

    var sent = false
    fun connectS() {
        if (!sent){
            socket = Socket(
                endpoint = Constant.ws,
                config = SocketOptions(
                    queryParams = mapOf("token" to ""),
                    transport = SocketOptions.Transport.WEBSOCKET
                )
            )
            {
                on(SocketEvent.Connect) {
                    println("connect")
                    socket.emit("joinRoom", conversationID)

                }

                on(SocketEvent.Disconnect) {
                    println("disconnect")
                }

                on(SocketEvent.Error) {
                    println("error $it")
                }

                on("receive_message") { data ->

                    println("receive_message $data")
                    _newMessageState.value = Json.decodeFromString<MessageModel>(data.toString())
                }
            }
            socket.connect()

            sent = true
        }

    }


}

