package com.kevinroditi.cochiwawa.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinroditi.cochiwawa.domain.model.ChatRoom
import com.kevinroditi.cochiwawa.domain.model.Message
import com.kevinroditi.cochiwawa.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val chatRooms: List<ChatRoom> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasConfirmedBookings: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        checkConfirmedBookings()
        loadChatRooms()
    }

    private fun checkConfirmedBookings() {
        chatRepository.hasConfirmedBookings()
            .onEach { hasConfirmed ->
                _uiState.update { it.copy(hasConfirmedBookings = hasConfirmed) }
            }
            .launchIn(viewModelScope)
    }

    fun loadChatRooms() {
        _uiState.update { it.copy(isLoading = true) }
        chatRepository.getChatRooms()
            .onEach { rooms ->
                _uiState.update { it.copy(chatRooms = rooms, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun loadMessages(chatRoomId: String) {
        _uiState.update { it.copy(isLoading = true) }
        chatRepository.getMessages(chatRoomId)
            .onEach { messages ->
                _uiState.update { it.copy(messages = messages, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(chatRoomId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val result = chatRepository.sendMessage(chatRoomId, text)
            if (result.isFailure) {
                _uiState.update { it.copy(error = "Failed to send message") }
            }
        }
    }
}
