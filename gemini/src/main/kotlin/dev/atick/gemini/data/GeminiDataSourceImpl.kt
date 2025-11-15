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

import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.FirebaseAIException
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

/**
 * Implementation of [GeminiDataSource] that uses the provided [GenerativeModel]s to interact with
 * the Gemini AI.
 *
 * @param chatModel The chat model to use.
 * @param expensesModel The expenses model to use.
 * @param ioDispatcher The [CoroutineDispatcher] to use for IO operations.
 */
class GeminiDataSourceImpl @Inject constructor(
    @ChatModel private val chatModel: GenerativeModel,
    @ExpensesModel private val expensesModel: GenerativeModel,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GeminiDataSource {

    /**
     * The chat instance to use for the chat AI.
     */
    private lateinit var chat: Chat

    /**
     * Initializes the chat with the given messages and context.
     *
     * @param messages The list of messages to initialize the chat with.
     * @param context The context to initialize the chat with.
     */
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

    /**
     * Sends a chat message to the AI.
     *
     * @param message The message to send.
     * @return The response from the AI.
     */
    override suspend fun sendChatMessage(message: String): String {
        return withContext(ioDispatcher) {
            chat.sendMessage(message).text?.trim()
                ?: throw IllegalStateException("Something went wrong with the chat AI.")
        }
    }

    /**
     * Gets an expense from the given SMS.
     *
     * @param aiSMS The SMS to get the expense from.
     * @return The expense from the SMS.
     */
    override suspend fun getExpenseFromSMS(aiSMS: AiSMS): AiExpense {
        return withContext(ioDispatcher) {
            val response = try {
                expensesModel.generateContent(aiSMS.getTextSMS()).text?.trim()
            } catch (e: FirebaseAIException) {
                throw e.toGeminiException()
            } ?: throw IllegalStateException("Something went wrong with the expenses AI.")
            ExpenseParser.parseExpense(response)
        }
    }
}
