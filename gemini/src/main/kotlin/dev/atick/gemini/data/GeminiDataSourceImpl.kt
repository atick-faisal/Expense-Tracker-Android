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

package dev.atick.gemini.data

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import dev.atick.gemini.di.ChatModel
import dev.atick.gemini.di.ExpensesModel
import dev.atick.gemini.models.AiChatMessage
import dev.atick.gemini.models.toGeminiContents
import javax.inject.Inject

class GeminiDataSourceImpl @Inject constructor(
    @ChatModel private val chatModel: GenerativeModel,
    @ExpensesModel private val expensesModel: GenerativeModel,
) : GeminiDataSource {

    private lateinit var chat: Chat

    override suspend fun initializeChat(messages: List<AiChatMessage>) {
        val chatHistory = messages.toGeminiContents()
        chat = chatModel.startChat(chatHistory)
        chat.sendMessage(
            "For the rest of the conversation, keep the response concise" +
                " and reply in plain text. Do not format the output.",
        )
    }

    override suspend fun sendChatMessage(message: String): String {
        return chat.sendMessage(message).text?.trim()
            ?: throw IllegalStateException("Something went wrong with the chat AI.")
    }
}
