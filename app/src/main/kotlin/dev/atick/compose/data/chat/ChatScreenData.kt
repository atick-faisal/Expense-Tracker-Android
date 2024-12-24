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

import dev.atick.core.ui.utils.TextFieldData
import dev.atick.storage.room.models.ChatEntity

data class ChatScreenData(
    val newMessage: TextFieldData = TextFieldData(""),
    val messages: List<UiMessage> = emptyList(),
)

data class UiMessage(
    val id: Long,
    val text: String,
    val isFromUser: Boolean = true,
)

fun ChatEntity.asUiMessage(): UiMessage {
    return UiMessage(
        id = id,
        text = text,
        isFromUser = isFromUser,
    )
}

fun List<ChatEntity>.asUiMessages(): List<UiMessage> {
    return map(ChatEntity::asUiMessage)
}

val demoQuestions = listOf<String>(
    "What's my total spending this month?",
    "Which merchant did I spend most on?",
    "How much did I spend at Netflix last month?",
    "Am I over budget this month?",
    "How much of my monthly budget is left?",
    "List all my active subscriptions",
    "How much do I spend on subscriptions monthly?",
    "Where can I cut expenses?",
    "Show my biggest one-time purchases",
)
