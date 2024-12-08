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

package dev.atick.compose.repository.chat

import dev.atick.compose.data.chat.UiMessage
import dev.atick.compose.data.chat.asUiMessages
import dev.atick.core.utils.suspendRunCatching
import dev.atick.gemini.data.GeminiDataSource
import dev.atick.gemini.models.AiChatMessage
import dev.atick.gemini.models.AiChatSender
import dev.atick.storage.room.data.ChatDataSource
import dev.atick.storage.room.models.ChatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDataSource: ChatDataSource,
    private val geminiDataSource: GeminiDataSource,
) : ChatRepository {

    override fun getAllMessages(): Flow<List<UiMessage>> {
        return chatDataSource.getAllMessages().map(List<ChatEntity>::asUiMessages)
    }

    override suspend fun initializeChat(historyDepth: Int): Result<Unit> {
        return suspendRunCatching {
            val chatHistory = chatDataSource.getRecentMessages(historyDepth)
            val aiChatHistory = chatHistory.map { chat ->
                AiChatMessage(
                    text = chat.text,
                    sender = if (chat.isFromUser) AiChatSender.USER else AiChatSender.MODEL,
                )
            }
            geminiDataSource.initializeChat(aiChatHistory)
        }
    }

    override suspend fun sendMessage(message: String): Result<Unit> {
        return suspendRunCatching {
            chatDataSource.insertMessage(
                ChatEntity(
                    text = message,
                    isFromUser = true,
                ),
            )
            val response = geminiDataSource.sendChatMessage(message)
            chatDataSource.insertMessage(ChatEntity(text = response, isFromUser = false))
        }
    }
}
