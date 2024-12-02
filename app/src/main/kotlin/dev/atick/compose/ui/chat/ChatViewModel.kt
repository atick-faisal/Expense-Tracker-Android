package dev.atick.compose.ui.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.chat.ChatScreenData
import dev.atick.compose.repository.chat.ChatRepository
import dev.atick.core.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _chatUiState = MutableStateFlow(UiState(ChatScreenData()))
    val chatUiState = _chatUiState.asStateFlow()
}