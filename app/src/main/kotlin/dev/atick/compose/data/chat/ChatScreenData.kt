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

import dev.atick.compose.R
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

val demoQuestions = listOf<Int>(
    R.string.demo_question_1,
    R.string.demo_question_2,
    R.string.demo_question_3,
    R.string.demo_question_4,
    R.string.demo_question_5,
    R.string.demo_question_6,
    R.string.demo_question_7,
    R.string.demo_question_8,
    R.string.demo_question_9,
)
