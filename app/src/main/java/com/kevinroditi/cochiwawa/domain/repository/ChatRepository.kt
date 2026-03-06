package com.kevinroditi.cochiwawa.domain.repository

import com.kevinroditi.cochiwawa.domain.model.ChatRoom
import com.kevinroditi.cochiwawa.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatRooms(): Flow<List<ChatRoom>>
    fun getMessages(chatRoomId: String): Flow<List<Message>>
    suspend fun sendMessage(chatRoomId: String, message: String): Result<Unit>
    fun hasConfirmedBookings(): Flow<Boolean>
}
