package com.example.cs446_fit4me.chat

import android.util.Log
import com.example.cs446_fit4me.model.ChatMessage
import io.socket.client.IO
import org.json.JSONObject

class ChatSocketManager(serverUrl: String, val userId: String) {
    private val TAG = "CHAT_DEBUG"
    val socket: io.socket.client.Socket? = IO.socket(serverUrl)

    fun connect() {
        socket?.connect()
        socket?.emit("join", userId)
        Log.d(TAG, "Connected to socket and joined with userId: $userId")
    }

    fun sendMessage(message: ChatMessage) {
        val json = JSONObject().apply {
            put("senderId", message.senderId)
            put("receiverId", message.receiverId)
            put("content", message.content)
            put("createdAt", message.createdAt)
        }
        socket?.emit("send_message", json)
        Log.d(TAG, "Sent message: ${message.content} to ${message.receiverId}")
    }

    fun setOnMessageReceived(onReceived: (ChatMessage) -> Unit) {
        socket?.on("receive_message") { args ->
            try {
                val data = args[0] as JSONObject
                val msg = ChatMessage(
                    id = data.optString("id"),
                    senderId = data.optString("senderId"),
                    receiverId = data.optString("receiverId"),
                    content = data.optString("content"),
                    createdAt = data.optString("createdAt")
                )
                Log.d(TAG, "Received socket message: ${msg.content} from ${msg.senderId}")
                onReceived(msg)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing received message: ${e.message}")
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
        Log.d(TAG, "Socket disconnected")
    }
}
