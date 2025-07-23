package com.example.cs446_fit4me.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.ChatMessage
import com.example.cs446_fit4me.network.ChatApiService
import com.example.cs446_fit4me.chat.ChatSocketManager
import com.example.cs446_fit4me.network.SendMessageRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class ChatViewModel(
    private val api: ChatApiService,
    private val socketManager: ChatSocketManager,
    private val currentUserId: String,
    private val peerUserId: String
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadHistory()
        socketManager.connect()
        socketManager.setOnMessageReceived { msg ->
            // Only add messages relevant to this chat
            if (
                (msg.senderId == currentUserId && msg.receiverId == peerUserId) ||
                (msg.senderId == peerUserId && msg.receiverId == currentUserId)
            ) {
                _messages.value = _messages.value + msg
            }
        }
    }

    fun loadHistory() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val history = api.getChatHistory( peerUserId)
                _messages.value = history
            } finally {
                _loading.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        val msg = ChatMessage(
            id = null,
            senderId = currentUserId,
            receiverId = peerUserId,
            content = text,
            createdAt = ZonedDateTime.now().toString()
        )
        socketManager.sendMessage(msg)
//        viewModelScope.launch {
//            try {
//                api.sendMessage(
//                    SendMessageRequest(
//                        senderId = currentUserId,
//                        receiverId = peerUserId,
//                        content = text
//                    )
//                )
//                // Optionally add to messages list immediately
//            } catch (e: Exception) {
//                // handle/send error
//            }
//        }
        // Optionally, add the message optimistically
        //_messages.value = _messages.value + msg
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }
}
