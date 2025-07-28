package com.example.cs446_fit4me.chat

import android.content.Context
import com.example.cs446_fit4me.model.ChatMessage
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

    /**
     * Updated to show a notification if the user is not actively chatting with the sender.
     */
    fun setOnGlobalMessageReceived(context: Context, callback: (ChatMessage) -> Unit) {
        socketManager?.setOnMessageReceived { msg ->
            // Show notification if this chat is not currently open
            if (!isChatOpenForPeer(msg.senderId)) {
                ChatNotificationHelper.showChatNotification(
                    context = context,
                    senderName = "New Message", // Replace with a proper name if available
                    message = msg.content,
                    peerUserId = msg.senderId
                )
            }

            // Still pass to UI layer
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
