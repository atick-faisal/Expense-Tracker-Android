/*
 * Copyright 2024 Atick Faisal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.atick.compose.data.chat

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import dev.atick.core.ui.utils.TextFieldData
import dev.atick.storage.room.models.ChatEntity

data class ChatScreenData(
    val newMessage: TextFieldData = TextFieldData(""),
    val messages: List<UiMessage> = emptyList(),
)

data class UiMessage(
    val text: String,
    val isFromUser: Boolean = true,
)

fun ChatEntity.asUiMessage(): UiMessage {
    return UiMessage(
        text = text,
        isFromUser = isFromUser,
    )
}

fun ChatEntity.asContent(): Content {
    return content(
        role = if (isFromUser) "user" else "model",
    ) { text(text) }
}

fun List<ChatEntity>.asUiMessages(): List<UiMessage> {
    return map(ChatEntity::asUiMessage)
}

fun List<ChatEntity>.asContents(): List<Content> {
    return map(ChatEntity::asContent)
}
