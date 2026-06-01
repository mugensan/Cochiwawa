package com.kevinroditi.cochiwawa.data.repository

import com.kevinroditi.cochiwawa.data.remote.ChatApi
import com.kevinroditi.cochiwawa.data.remote.SendChatRequest
import com.kevinroditi.cochiwawa.domain.model.ChatRoom
import com.kevinroditi.cochiwawa.domain.model.Message
import com.kevinroditi.cochiwawa.domain.repository.ChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {

    override fun getChatRooms(): Flow<List<ChatRoom>> = flow {
        while (true) {
            try {
                val rooms = chatApi.getChatRooms()
                emit(rooms)
            } catch (e: Exception) {
                // Log error
            }
            delay(10000) // Poll every 10 seconds
        }
    }

    override fun getMessages(chatRoomId: String): Flow<List<Message>> = flow {
        while (true) {
            try {
                val messages = chatApi.getMessages(chatRoomId)
                emit(messages)
            } catch (e: Exception) {
                // Log error
            }
            delay(3000) // Poll every 3 seconds for messages
        }
    }

    override suspend fun sendMessage(chatRoomId: String, message: String): Result<Unit> {
        return try {
            val response = chatApi.sendMessage(SendChatRequest(chatRoomId, message))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun hasConfirmedBookings(): Flow<Boolean> = flow {
        while (true) {
            try {
                val rooms = chatApi.getChatRooms()
                emit(rooms.isNotEmpty())
            } catch (e: Exception) {
                emit(false)
            }
            delay(30000) // Check every 30 seconds
        }
    }
}
