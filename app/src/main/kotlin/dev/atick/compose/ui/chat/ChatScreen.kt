package dev.atick.compose.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.chat.ChatScreenData
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun ChatRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    chatViewModel: ChatViewModel = hiltViewModel(),
) {
    val chatUiState by chatViewModel.chatUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = chatUiState,
        onShowSnackbar = onShowSnackbar,
    ) { chatScreenData ->
        ChatScreen(chatScreenData)
    }
}

@Composable
private fun ChatScreen(
    chatScreenData: ChatScreenData,
) {

}