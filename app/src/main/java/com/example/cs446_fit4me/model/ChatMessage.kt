// com.example.cs446_fit4me.model.ChatMessage
package com.example.cs446_fit4me.model

data class ChatMessage(
    val id: String?,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val createdAt: String
)
