package com.kevinroditi.cochiwawa.data.remote

import com.kevinroditi.cochiwawa.domain.model.ChatRoom
import com.kevinroditi.cochiwawa.domain.model.Message
import retrofit2.http.*

interface ChatApi {
    @GET("chat/rooms")
    suspend fun getChatRooms(): List<ChatRoom>

    @GET("chat/messages/{chatRoomId}")
    suspend fun getMessages(@Path("chatRoomId") chatRoomId: String): List<Message>

    @POST("chat/send")
    suspend fun sendMessage(@Body sendChatRequest: SendChatRequest): retrofit2.Response<Unit>
}

data class SendChatRequest(
    val chatRoomId: String,
    val message: String
)
