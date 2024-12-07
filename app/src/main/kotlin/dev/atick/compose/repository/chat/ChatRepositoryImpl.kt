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

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import dev.atick.compose.data.chat.UiMessage
import dev.atick.compose.data.chat.asContents
import dev.atick.compose.data.chat.asUiMessages
import dev.atick.core.utils.suspendRunCatching
import dev.atick.storage.room.data.ChatDataSource
import dev.atick.storage.room.models.ChatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDataSource: ChatDataSource,
    private val generativeModel: GenerativeModel,
) : ChatRepository {

    private lateinit var chat: Chat

    override fun getAllMessages(): Flow<List<UiMessage>> {
        return chatDataSource.getAllMessages().map(List<ChatEntity>::asUiMessages)
    }

    override suspend fun initializeChat(historyDepth: Int): Result<Unit> {
        return suspendRunCatching {
            val chatHistory = chatDataSource.getRecentMessages(historyDepth)
            chat = generativeModel.startChat(chatHistory.asContents())
            chat.sendMessage(
                "For the rest of the conversation, keep the response concise" +
                    " and reply in plain text. Do not format the output.",
            )
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
            val response = chat.sendMessage(message).text?.trim()
                ?: throw IllegalStateException("Sorry, I didn't understand that.")
            chatDataSource.insertMessage(ChatEntity(text = response, isFromUser = false))
        }
    }
}
