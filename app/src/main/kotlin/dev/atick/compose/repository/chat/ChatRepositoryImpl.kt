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

class ChatRepositoryImpl @Inject constructor(
    private val expenseDataSource: ExpenseDataSource,
    private val analysisDataSource: AnalysisDataSource,
    private val budgetDataSource: BudgetDataSource,
    private val chatDataSource: ChatDataSource,
    private val geminiDataSource: GeminiDataSource,
) : ChatRepository {

    override fun getAllMessages(): Flow<List<UiMessage>> {
        return chatDataSource.getAllMessages().map(List<ChatEntity>::asUiMessages)
    }

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

    private suspend fun createChatContext(monthInfo: MonthInfo): String {
        val startDate = monthInfo.startDate
        val endDate = monthInfo.endDate

        val results = coroutineScope {
            val totalSpending =
                async { analysisDataSource.getTotalSpending(startDate, endDate).first() }
            val categoryAnalysis =
                async { analysisDataSource.getCategoryAnalyses(startDate, endDate, 10).first() }
            val merchantAnalysis =
                async { analysisDataSource.getMerchantAnalyses(startDate, endDate, 10).first() }
            val budget = async { budgetDataSource.getBudgetForMonth(startDate).first() }
            val subscriptions = async { expenseDataSource.getRecurringExpenses().first() }

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

data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
)
