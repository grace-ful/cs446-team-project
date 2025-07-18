// com.example.cs446_fit4me.network.ChatApiService
package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.ChatMessage
import com.example.cs446_fit4me.model.UserResponse
import retrofit2.http.*

data class SendMessageRequest(
    val senderId: String,
    val receiverId: String,
    val content: String
)

data class SimpleUser(
    val id: String,
    val name: String
)

data class ConversationsResponse(
    val users: List<SimpleUser>
)


interface ChatApiService {
    @GET("chat/history/{userB}")
    suspend fun getChatHistory(
        @Path("userB") userB: String
    ): List<ChatMessage>

    @POST("chat/send")
    suspend fun sendMessage(
        @Body message: SendMessageRequest
    ): ChatMessage

    @GET("chat/conversations")
    suspend fun getConversations(): ConversationsResponse
}
