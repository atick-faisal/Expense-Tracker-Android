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
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import dev.atick.core.di.IoDispatcher
import dev.atick.gemini.di.ChatModel
import dev.atick.gemini.di.ExpensesModel
import dev.atick.gemini.models.AiChatMessage
import dev.atick.gemini.models.AiExpense
import dev.atick.gemini.models.AiSMS
import dev.atick.gemini.models.toGeminiContents
import dev.atick.gemini.utils.ExpenseParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiDataSourceImpl @Inject constructor(
    @ChatModel private val chatModel: GenerativeModel,
    @ExpensesModel private val expensesModel: GenerativeModel,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GeminiDataSource {

    private lateinit var chat: Chat

    override suspend fun initializeChat(messages: List<AiChatMessage>, context: String) {
        withContext(ioDispatcher) {
            val chatHistory = messages.toGeminiContents()
            chat = chatModel.startChat(chatHistory)
            chat.sendMessage(
                "For the rest of the conversation, try use the provided context below to answer." +
                    " queries. If not enough information is available in the context, try to" +
                    " provide helpful suggestion if possible. Keep the response concise" +
                    " and reply in plain text. Do not format the output." +
                    "\n\nContext: \n $context",
            )
        }
    }

    override suspend fun sendChatMessage(message: String): String {
        return withContext(ioDispatcher) {
            chat.sendMessage(message).text?.trim()
                ?: throw IllegalStateException("Something went wrong with the chat AI.")
        }
    }

    override suspend fun getExpenseFromSMS(aiSMS: AiSMS): AiExpense {
        return withContext(ioDispatcher) {
            val response = try {
                expensesModel.generateContent(aiSMS.getTextSMS()).text?.trim()
            } catch (e: GoogleGenerativeAIException) {
                throw e.toGeminiException()
            } ?: throw IllegalStateException("Something went wrong with the expenses AI.")
            ExpenseParser.parseExpense(response)
        }
    }
}
