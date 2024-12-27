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
import dev.atick.core.utils.MonthInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching chat data.
 */
interface ChatRepository {
    companion object {
        /**
         * The maximum depth of the chat history to fetch.
         */
        const val MAX_HISTORY_DEPTH = 100

        /**
         * The number of top categories to load.
         */
        const val N_TOP_CATEGORY_TO_LOAD = 10

        /**
         * The number of top merchants to load.
         */
        const val N_TOP_MERCHANT_TO_LOAD = 10
    }

    /**
     * Gets all the messages.
     *
     * @return A [Flow] of [List] of [UiMessage] representing the messages.
     */
    fun getAllMessages(): Flow<List<UiMessage>>

    /**
     * Initializes the chat.
     *
     * @param monthInfo The month information for which the chat is to be initialized.
     * @param historyDepth The depth of the chat history to fetch.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun initializeChat(monthInfo: MonthInfo, historyDepth: Int): Result<Unit>

    /**
     * Sends a message.
     *
     * @param message The message to be sent.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun sendMessage(message: String): Result<Unit>
}
