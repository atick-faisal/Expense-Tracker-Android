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

import androidx.annotation.RequiresPermission
import dev.atick.compose.data.expenses.UiCategoryType
import dev.atick.compose.data.expenses.UiCurrencyType
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.sync.SyncManager
import dev.atick.compose.sync.SyncProgress
import dev.atick.core.utils.suspendRunCatching
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
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.max

class ExpensesRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val geminiRateLimiter: GeminiRateLimiter,
    private val smsDataSource: SMSDataSource,
    private val expenseDataSource: ExpenseDataSource,
    private val syncManager: SyncManager,
) : ExpensesRepository {
    override val isSyncing: Flow<Boolean>
        get() = syncManager.isSyncing

    override fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<UiExpense>> {
        return expenseDataSource.getAllExpenses(startDate, endDate)
            .map { expenses ->
                expenses.map { expense ->
                    UiExpense(
                        id = expense.id,
                        amount = expense.amount,
                        currency = UiCurrencyType.valueOf(expense.currency),
                        merchant = expense.merchant,
                        category = UiCategoryType.valueOf(expense.category),
                        paymentStatus = UiPaymentStatus.valueOf(expense.paymentStatus),
                        recurringType = UiRecurringType.valueOf(expense.recurringType),
                        paymentDate = expense.paymentDate,
                        dueDate = expense.dueDate,
                        toBeCancelled = expense.toBeCancelled,
                    )
                }
            }
    }

    override fun getExpenseById(id: Long): Flow<UiExpense> {
        return expenseDataSource.getExpenseById(id)
            .map { expense ->
                expense ?: throw IllegalArgumentException("Expense not found")
                UiExpense(
                    id = expense.id,
                    amount = expense.amount,
                    currency = UiCurrencyType.valueOf(expense.currency),
                    merchant = expense.merchant,
                    category = UiCategoryType.valueOf(expense.category),
                    paymentStatus = UiPaymentStatus.valueOf(expense.paymentStatus),
                    recurringType = UiRecurringType.valueOf(expense.recurringType),
                    paymentDate = expense.paymentDate,
                    dueDate = expense.dueDate,
                    toBeCancelled = expense.toBeCancelled,
                )
            }
    }

    override suspend fun updateExpense(expense: UiExpense): Result<Unit> {
        return suspendRunCatching {
            expenseDataSource.updateExpense(
                ExpenseEntity(
                    id = expense.id,
                    amount = expense.amount,
                    currency = expense.currency.name,
                    merchant = expense.merchant,
                    category = expense.category.name,
                    paymentStatus = expense.paymentStatus.name,
                    recurringType = expense.recurringType.name,
                    paymentDate = expense.paymentDate,
                    dueDate = expense.dueDate,
                    toBeCancelled = expense.toBeCancelled,
                ),
            )
        }
    }

    @RequiresPermission(android.Manifest.permission.READ_SMS)
    override fun requestSync(): Result<Unit> {
        return runCatching {
            syncManager.requestSync()
        }
    }

    @RequiresPermission(android.Manifest.permission.READ_SMS)
    override fun syncExpensesFromSms() = flow<SyncProgress> {
        val lastExpenseTime = expenseDataSource.getLastExpenseTime()
        val startDate = max(
            lastExpenseTime,
            System.currentTimeMillis() - ExpensesRepository.SYNC_SMS_DURATION,
        ) + 1000L // Add 1 second to avoid duplicate SMSes

        val smsList = smsDataSource.querySMS(
            senderNames = ExpensesRepository.BANK_NAMES,
            keywords = ExpensesRepository.KEYWORDS,
            startDate = startDate,
            endDate = System.currentTimeMillis(),
        )

        val totalSms = smsList.size

        Timber.d("Found $totalSms SMSes")

        for ((i, sms) in smsList.withIndex()) {
            Timber.d("SMS $i$: $sms")

            processSms(sms)

            emit(
                SyncProgress(
                    total = totalSms,
                    current = i + 1,
                    message = "Syncing expenses... $i / $totalSms",
                ),
            )
        }
    }

    override suspend fun setRecurringType(
        merchant: String,
        recurringType: UiRecurringType,
    ): Result<Unit> {
        return suspendRunCatching {
            expenseDataSource.setRecurringType(
                merchant,
                recurringType.name,
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

//                val paymentDate = expense.paymentDate?.run {
//                    LocalDateTime.parse(this)
//                        .toInstant(TimeZone.currentSystemDefault())
//                        .toEpochMilliseconds()
//                } ?: System.currentTimeMillis()

                expenseDataSource.insertExpense(
                    ExpenseEntity(
                        amount = expense.amount,
                        currency = expense.currency.name,
                        paymentDate = sms.date, // Use original SMS date
                        merchant = expense.merchant,
                        category = expense.category.name,
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
