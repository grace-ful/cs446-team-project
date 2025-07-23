// com.example.cs446_fit4me.chat.ChatSocketManager
package com.example.cs446_fit4me.chat

import com.example.cs446_fit4me.model.ChatMessage
import io.socket.client.IO
import org.json.JSONObject

class ChatSocketManager(serverUrl: String, val userId: String) {

    val socket: io.socket.client.Socket? = IO.socket(serverUrl)

    fun connect() {
        socket?.connect()
        socket?.emit("join", userId)
    }

    fun sendMessage(message: ChatMessage) {
        val json = JSONObject().apply {
            put("senderId", message.senderId)
            put("receiverId", message.receiverId)
            put("content", message.content)
            put("createdAt", message.createdAt) // Use 'createdAt'
            // Don't send id; backend generates it
        }
        socket?.emit("send_message", json)
    }

    fun setOnMessageReceived(onReceived: (ChatMessage) -> Unit) {
        socket?.on("receive_message") { args ->
            val data = args[0] as JSONObject
            val msg = ChatMessage(
                id = data.optString("id"), // Will be null if not sent by backend
                senderId = data.optString("senderId"),
                receiverId = data.optString("receiverId"),
                content = data.optString("content"),
                createdAt = data.optString("createdAt") // Use 'createdAt'
            )
            onReceived(msg)
        }
    }

    fun disconnect() {
        socket?.disconnect()
    }
}
