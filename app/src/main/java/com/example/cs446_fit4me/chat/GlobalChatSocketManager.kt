package com.example.cs446_fit4me.chat

import android.content.Context
import com.example.cs446_fit4me.model.ChatMessage
import com.example.cs446_fit4me.ui.components.ChatNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GlobalChatSocketManager {
    private var socketManager: ChatSocketManager? = null
    private var currentUserId: String? = null

    private var openChatPeerId: String? = null

    fun init(userId: String) {
        if (socketManager != null) return // Only init once
        this.currentUserId = userId
        socketManager = ChatSocketManager(com.example.cs446_fit4me.network.ApiClient.SOCKET_URL, userId)
        socketManager?.connect()
    }

    fun setOnGlobalMessageReceived(callback: (ChatMessage) -> Unit) {
        socketManager?.setOnMessageReceived { msg ->
            callback(msg)
        }
    }

    fun setOpenChatPeerId(peerId: String?) {
        openChatPeerId = peerId
    }

    fun isChatOpenForPeer(peerId: String): Boolean {
        return openChatPeerId == peerId
    }

    fun disconnect() {
        socketManager?.disconnect()
        socketManager = null
        currentUserId = null
        openChatPeerId = null
    }
}
