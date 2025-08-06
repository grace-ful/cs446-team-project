package com.example.cs446_fit4me.chat

import android.content.Context
import android.util.Log
import com.example.cs446_fit4me.datastore.SessionManager
import com.example.cs446_fit4me.model.ChatMessage
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.*

object GlobalChatSocketManager {
    private var socketManager: ChatSocketManager? = null
    private var currentUserId: String? = null
    private var openChatPeerId: String? = null

    /**
     * Initializes the socket connection using the current user's ID from SessionManager.
     */
    fun initWithSession(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = SessionManager(context).getUserId()

            withContext(Dispatchers.Main) {
                if (!userId.isNullOrBlank()) {
                    if (socketManager == null || currentUserId != userId) {
                        Log.d("CHAT_DEBUG", "Initializing socket manager with userId: $userId")
                        currentUserId = userId
                        socketManager = ChatSocketManager(ApiClient.SOCKET_URL, userId)
                        socketManager?.connect()
                    } else {
                        Log.d("CHAT_DEBUG", "Socket already initialized with correct userId.")
                    }
                } else {
                    Log.e("CHAT_DEBUG", "Failed to initialize socket: No user ID found in session.")
                }
            }
        }
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

                callback(msg)
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
