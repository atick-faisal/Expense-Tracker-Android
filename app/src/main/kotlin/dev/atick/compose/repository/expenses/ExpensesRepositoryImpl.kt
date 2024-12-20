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

package dev.atick.compose.repository.expenses

import dev.atick.compose.data.categories.UiCategoryType
import dev.atick.compose.data.expenses.UiCurrencyType
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.sync.SyncProgress
import dev.atick.gemini.data.GeminiDataSource
import dev.atick.gemini.data.GeminiException
import dev.atick.gemini.data.GeminiRateLimiter
import dev.atick.gemini.models.AiSMS
import dev.atick.sms.data.SMSDataSource
import dev.atick.sms.models.SMSMessage
import dev.atick.storage.room.data.ExpenseDataSource
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import timber.log.Timber
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val geminiRateLimiter: GeminiRateLimiter,
    private val smsDataSource: SMSDataSource,
    private val expenseDataSource: ExpenseDataSource,
) : ExpensesRepository {
    override val expenses: Flow<List<UiExpense>>
        get() = expenseDataSource.getAllExpenses()
            .map { expenses ->
                expenses.map { expense ->
                    UiExpense(
                        id = expense.id,
                        amount = expense.amount,
                        currency = UiCurrencyType.valueOf(expense.currency),
                        category = UiCategoryType.valueOf(expense.categoryType),
                        paymentStatus = UiPaymentStatus.valueOf(expense.paymentStatus),
                        recurringType = UiRecurringType.valueOf(expense.recurringType),
                        paymentDate = expense.paymentDate,
                        dueDate = expense.dueDate,
                        description = expense.description,
                        toBeCancelled = expense.toBeCancelled,
                    )
                }
            }

    override fun syncExpensesFromSms() = flow<SyncProgress> {
        val smsList = smsDataSource.querySMS(
            senderName = "QNB",
            keywords = listOf("purchase"),
            startDate = System.currentTimeMillis() - ExpensesRepository.SYNC_SMS_DURATION,
            endDate = System.currentTimeMillis(),
        )

        val totalSms = smsList.size

        Timber.d("Found $totalSms SMSes")

        for ((i, sms) in smsList.withIndex()) {
            Timber.d("SMS $i$: $sms")

            processSms(sms)

            if (i >= 20) break

            emit(
                SyncProgress(
                    total = totalSms,
                    current = i + 1,
                    message = "Syncing expenses... $i / $totalSms",
                ),
            )
        }
    }

    private suspend fun processSms(sms: SMSMessage) {
        var isSuccess = false
        var retryAttempts = 0

        // Needed for 15 requests/min quota limit
        geminiRateLimiter.checkAndDelay()

        while (!isSuccess && retryAttempts < GeminiRateLimiter.MAX_RETRIES) {
            try {
                val expense = geminiDataSource.getExpenseFromSMS(
                    AiSMS(
                        address = sms.address,
                        body = sms.body,
                        date = sms.date,
                    ),
                )

                Timber.d("Expense: $expense")

                val paymentDate = expense.paymentDate?.run {
                    LocalDate.parse(this)
                        .atStartOfDayIn(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds()
                } ?: System.currentTimeMillis()

                expenseDataSource.insertExpense(
                    ExpenseEntity(
                        amount = expense.amount,
                        currency = expense.currency.name,
                        paymentDate = paymentDate,
                        description = expense.description,
                        categoryType = expense.category.name,
                        paymentStatus = expense.paymentStatus.name,
                        recurringType = expense.recurringType.name,
                    ),
                )

                isSuccess = true
                retryAttempts = 0 // Reset retry counter on success
            } catch (e: GeminiException) {
                when (e) {
                    // Retryable errors
                    is GeminiException.QuotaExceeded,
                    is GeminiException.Server,
                    is GeminiException.RequestTimeout,
                    is GeminiException.ResponseStopped,
                    -> {
                        val backoffDelay =
                            GeminiRateLimiter.BASE_DELAY_BETWEEN_REQUESTS *
                                (1 shl retryAttempts)
                        Timber.w(
                            "Retryable error (${e::class.simpleName}), attempt " +
                                "$retryAttempts after ${backoffDelay}ms: ${e.message}",
                        )
                        delay(backoffDelay)
                        retryAttempts++
                        continue
                    }

                    // Non-retryable errors - throw immediately
                    is GeminiException.InvalidAPIKey,
                    is GeminiException.PromptBlocked,
                    is GeminiException.UnsupportedUserLocation,
                    is GeminiException.InvalidState,
                    is GeminiException.Serialization,
                    is GeminiException.Unknown,
                    -> throw e
                }
            }
        }
    }
}
