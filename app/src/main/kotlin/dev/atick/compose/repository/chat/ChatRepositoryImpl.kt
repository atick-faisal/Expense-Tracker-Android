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
import dev.atick.core.utils.MonthInfo
import dev.atick.core.utils.suspendRunCatching
import dev.atick.gemini.data.GeminiDataSource
import dev.atick.gemini.models.AiChatMessage
import dev.atick.gemini.models.AiChatSender
import dev.atick.storage.room.data.AnalysisDataSource
import dev.atick.storage.room.data.BudgetDataSource
import dev.atick.storage.room.data.ChatDataSource
import dev.atick.storage.room.data.ExpenseDataSource
import dev.atick.storage.room.models.ChatEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [ChatRepository] that fetches data from the [ChatDataSource], [AnalysisDataSource], [BudgetDataSource], and [GeminiDataSource].
 *
 * @param expenseDataSource The data source for expense data.
 * @param analysisDataSource The data source for analysis data.
 * @param budgetDataSource The data source for budget data.
 * @param chatDataSource The data source for chat data.
 * @param geminiDataSource The data source for Gemini chat data.
 */
class ChatRepositoryImpl @Inject constructor(
    private val expenseDataSource: ExpenseDataSource,
    private val analysisDataSource: AnalysisDataSource,
    private val budgetDataSource: BudgetDataSource,
    private val chatDataSource: ChatDataSource,
    private val geminiDataSource: GeminiDataSource,
) : ChatRepository {

    /**
     * Gets all the messages.
     *
     * @return A [Flow] of [List] of [UiMessage] representing the messages.
     */
    override fun getAllMessages(): Flow<List<UiMessage>> {
        return chatDataSource.getAllMessages().map(List<ChatEntity>::asUiMessages)
    }

    /**
     * Initializes the chat.
     *
     * @param monthInfo The month information for which the chat is to be initialized.
     * @param historyDepth The depth of the chat history to fetch.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun initializeChat(monthInfo: MonthInfo, historyDepth: Int): Result<Unit> {
        return suspendRunCatching {
            val chatHistory = chatDataSource.getRecentMessages(historyDepth)
            val chatContext = createChatContext(monthInfo)
            val aiChatHistory = chatHistory.map { chat ->
                AiChatMessage(
                    text = chat.text,
                    sender = if (chat.isFromUser) AiChatSender.USER else AiChatSender.MODEL,
                )
            }
            geminiDataSource.initializeChat(aiChatHistory, chatContext)
        }
    }

    /**
     * Sends a message.
     *
     * @param message The message to be sent.
     * @return A [Result] indicating the success or failure of the operation.
     */
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

    /**
     * Creates the chat context.
     *
     * @param monthInfo The month information for which the chat context is to be created.
     * @return The chat context.
     */
    private suspend fun createChatContext(monthInfo: MonthInfo): String {
        val startDate = monthInfo.startDate
        val endDate = monthInfo.endDate

        val results = coroutineScope {
            val totalSpending =
                async { analysisDataSource.getTotalSpending(startDate, endDate).first() }
            val categoryAnalysis =
                async {
                    analysisDataSource.getCategoryAnalyses(
                        startDate = startDate,
                        endDate = endDate,
                        topN = ChatRepository.N_TOP_CATEGORY_TO_LOAD,
                    ).first()
                }
            val merchantAnalysis =
                async {
                    analysisDataSource.getMerchantAnalyses(
                        startDate = startDate,
                        endDate = endDate,
                        topN = ChatRepository.N_TOP_MERCHANT_TO_LOAD,
                    ).first()
                }
            val budget = async { budgetDataSource.getBudgetForMonth(startDate).first() }
            val subscriptions = async { expenseDataSource.getRecurringExpenses().first() }

            // Required to infer the types of the results.
            Quintuple(
                totalSpending.await(),
                categoryAnalysis.await(),
                merchantAnalysis.await(),
                budget.await(),
                subscriptions.await(),
            )
        }

        val (totalSpending, categoryAnalysis, merchantAnalysis, budget, subscriptions) = results

        return buildString {
            append("Current Month Overview:\n")
            append("Total Spending: $totalSpending QAR\n")
            append("Budget: ${budget?.amount ?: 0.0} QAR\n")

            append("\nTop Categories:\n")
            categoryAnalysis.forEach { analysis ->
                append("${analysis.categoryOrMerchant}: ${analysis.spending} QAR (${analysis.percentage}%)\n")
            }

            append("\nTop Merchants:\n")
            merchantAnalysis.forEach { analysis ->
                append("${analysis.categoryOrMerchant}: ${analysis.spending} QAR\n")
            }

            append("\nActive Subscriptions:\n")
            subscriptions.forEach { subscription ->
                append("${subscription.merchant}: ${subscription.amount} ${subscription.currency} (${subscription.recurringType})\n")
            }
        }
    }
}

/**
 * A data class representing a quintuple, which is a tuple with five elements.
 *
 * @param A The type of the first element.
 * @param B The type of the second element.
 * @param C The type of the third element.
 * @param D The type of the fourth element.
 * @param E The type of the fifth element.
 * @property first The first element of the quintuple.
 * @property second The second element of the quintuple.
 * @property third The third element of the quintuple.
 * @property fourth The fourth element of the quintuple.
 * @property fifth The fifth element of the quintuple.
 */
data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
)
