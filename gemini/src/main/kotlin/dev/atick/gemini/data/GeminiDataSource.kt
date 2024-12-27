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

import dev.atick.gemini.models.AiChatMessage
import dev.atick.gemini.models.AiExpense
import dev.atick.gemini.models.AiSMS

/**
 * Interface representing a data source for the Gemini AI.
 */
interface GeminiDataSource {
    /**
     * Initializes the chat with the given messages and context.
     *
     * @param messages The list of messages to initialize the chat with.
     * @param context The context to initialize the chat with.
     */
    suspend fun initializeChat(messages: List<AiChatMessage>, context: String)

    /**
     * Sends a chat message to the AI.
     *
     * @param message The message to send.
     * @return The response from the AI.
     */
    suspend fun sendChatMessage(message: String): String

    /**
     * Gets an expense from the given SMS.
     *
     * @param aiSMS The SMS to get the expense from.
     * @return The expense from the SMS.
     */
    suspend fun getExpenseFromSMS(aiSMS: AiSMS): AiExpense
}
