package dev.atick.compose.data.chat

import dev.atick.core.ui.utils.TextFiledData

data class ChatScreenData(
    val newMessage: TextFiledData = TextFiledData(""),
    val messages: List<UiMessage> = emptyList(),
)

data class UiMessage(
    val text: String,
    val isFromUser: Boolean,
)
