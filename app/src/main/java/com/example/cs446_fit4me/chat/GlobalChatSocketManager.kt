package com.example.cs446_fit4me.chat

import android.content.Context
import android.util.Log
import com.example.cs446_fit4me.model.ChatMessage
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.*

object GlobalChatSocketManager {
    private var socketManager: ChatSocketManager? = null
    private var currentUserId: String? = null
    private var openChatPeerId: String? = null

    fun init(userId: String) {
        if (socketManager != null) return
        Log.d("CHAT_DEBUG", "Initializing socket manager for user: $userId")

        this.currentUserId = userId
        socketManager = ChatSocketManager(com.example.cs446_fit4me.network.ApiClient.SOCKET_URL, userId)
        socketManager?.connect()
    }

    fun setOnGlobalMessageReceived(context: Context, callback: (ChatMessage) -> Unit) {
        socketManager?.setOnMessageReceived { msg ->
            Log.d("CHAT_DEBUG", "Received message: ${msg.content} from ${msg.senderId}")

            fetchUserNameFromBackend(context, msg.senderId) { senderName ->
            if (!isChatOpenForPeer(msg.senderId)) {
                    ChatNotificationHelper.showChatNotification(
                        context = context,
                        senderName = senderName,
                        message = msg.content,
                        peerUserId = msg.senderId
                    )
                } else {
                    Log.d("CHAT_DEBUG", "Chat with ${msg.senderId} is currently open. No notification shown.")
                }

                callback(msg) // continue to UI
            }
        }
    }


    fun setOpenChatPeerId(peerId: String?) {
        openChatPeerId = peerId
        Log.d("CHAT_DEBUG", "Set open chat peer ID to: $peerId")
    }

    fun isChatOpenForPeer(peerId: String): Boolean {
        val isOpen = openChatPeerId == peerId
        Log.d("CHAT_DEBUG", "isChatOpenForPeer($peerId) = $isOpen")
        return isOpen
    }

    fun disconnect() {
        Log.d("CHAT_DEBUG", "Disconnecting socket manager")
        socketManager?.disconnect()
        socketManager = null
        currentUserId = null
        openChatPeerId = null
    }

    fun fetchUserNameFromBackend(context: Context, userId: String, onResult: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.getUserApi(context).getUserByIdByPath(userId)


                val name = response.name
                Log.d("CHAT_DEBUG", "Fetched name for $userId: $name")

                withContext(Dispatchers.Main) {
                    onResult(name)
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Failed to fetch user name for $userId: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult("New Message")
                }
            }
        }
    }
}
