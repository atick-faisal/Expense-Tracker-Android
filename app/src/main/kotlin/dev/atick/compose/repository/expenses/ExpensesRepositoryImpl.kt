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
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.data.expenses.toExpenseEntity
import dev.atick.compose.data.expenses.toUiExpense
import dev.atick.compose.data.expenses.toUiExpenses
import dev.atick.compose.worker.SyncProgress
import dev.atick.compose.worker.TaskManager
import dev.atick.core.utils.getMonthInfoAt
import dev.atick.core.utils.suspendRunCatching
import dev.atick.gemini.data.GeminiDataSource
import dev.atick.gemini.data.GeminiException
import dev.atick.gemini.data.GeminiRateLimiter
import dev.atick.gemini.models.AiSMS
import dev.atick.sms.data.SMSDataSource
import dev.atick.sms.models.SMSMessage
import dev.atick.storage.room.data.BudgetDataSource
import dev.atick.storage.room.data.ExpenseDataSource
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.max

/**
 * Implementation of [ExpensesRepository] that fetches data from the [ExpenseDataSource], [BudgetDataSource], [SMSDataSource], and [GeminiDataSource].
 *
 * @param geminiDataSource The data source for Gemini data.
 * @param geminiRateLimiter The rate limiter for Gemini API requests.
 * @param smsDataSource The data source for SMS data.
 * @param expenseDataSource The data source for expense data.
 * @param budgetDataSource The data source for budget data.
 * @param taskManager The task manager for syncing and notifications.
 */
class ExpensesRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val geminiRateLimiter: GeminiRateLimiter,
    private val smsDataSource: SMSDataSource,
    private val expenseDataSource: ExpenseDataSource,
    private val budgetDataSource: BudgetDataSource,
    private val taskManager: TaskManager,
) : ExpensesRepository {
    /**
     * Flow of Boolean that indicates whether the repository is syncing.
     */
    override val isSyncing: Flow<Boolean>
        get() = taskManager.isSyncing

    /**
     * Gets all the expenses.
     *
     * @param startDate The start date of the expenses.
     * @param endDate The end date of the expenses.
     * @return A [Flow] of [List] of [UiExpense] representing the expenses.
     */
    override fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<UiExpense>> {
        return expenseDataSource.getAllExpenses(startDate, endDate)
            .map { expenses -> expenses.toUiExpenses() }
    }

    /**
     * Gets the expense by ID.
     *
     * @param id The ID of the expense.
     * @return A [Flow] of [UiExpense] representing the expense.
     */
    override fun getExpenseById(id: Long): Flow<UiExpense> {
        return expenseDataSource.getExpenseById(id)
            .map { expense ->
                expense?.toUiExpense()
                    ?: throw IllegalArgumentException("Expense not found")
            }
    }

    /**
     * Updates the expense.
     *
     * @param expense The expense to be updated.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun updateExpense(expense: UiExpense): Result<Unit> {
        return suspendRunCatching {
            expenseDataSource.updateExpense(expense.toExpenseEntity())

            // Update recurring type of all expenses from the same merchant
            if (expense.recurringType != UiRecurringType.ONETIME) {
                setRecurringType(expense.merchant, expense.recurringType)
            }

            // Check if budget exceeded after updating expense
            // TODO: Might be better a to do this
            checkBudgetExceeded()
        }
    }

    /**
     * Deletes the expense.
     *
     * @param expense The expense to be deleted.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun deleteExpense(expense: UiExpense): Result<Unit> {
        return suspendRunCatching {
            expenseDataSource.deleteExpense(expense.toExpenseEntity())
        }
    }

    /**
     * Requests a sync of the expenses.
     *
     * @return A [Result] indicating the success or failure of the operation.
     */
    @RequiresPermission(android.Manifest.permission.READ_SMS)
    override fun requestSync(): Result<Unit> {
        return runCatching {
            taskManager.requestSync()
        }
    }

    /**
     * Syncs the expenses from the SMS.
     *
     * @return A [Flow] of [SyncProgress] representing the progress of the sync.
     */
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
            ignoreWords = ExpensesRepository.IGNORE_WORDS,
            startDate = startDate,
            endDate = System.currentTimeMillis(),
        )

        val totalSms = smsList.size

        Timber.d("Found $totalSms SMSes")

        for ((i, sms) in smsList.withIndex()) {
            Timber.d("SMS $i$: $sms")

            processSms(sms)
            checkBudgetExceeded()

            emit(
                SyncProgress(
                    total = totalSms,
                    current = i + 1,
                    message = "Syncing expenses... $i / $totalSms",
                ),
            )
        }
    }

    /**
     * Sets the recurring type for the merchant.
     *
     * @param merchant The merchant for which the recurring type is to be set.
     * @param recurringType The recurring type to be set.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun setRecurringType(
        merchant: String,
        recurringType: UiRecurringType,
    ): Result<Unit> {
        return suspendRunCatching {
            val lastPaymentDate = expenseDataSource.getLastPaymentDate(merchant)
            val nextPaymentDate = when (recurringType) {
                UiRecurringType.ONETIME -> Long.MAX_VALUE
                UiRecurringType.DAILY -> lastPaymentDate + ExpensesRepository.RECURRING_DAILY
                UiRecurringType.WEEKLY -> lastPaymentDate + ExpensesRepository.RECURRING_WEEKLY
                UiRecurringType.MONTHLY -> lastPaymentDate + ExpensesRepository.RECURRING_MONTHLY
                UiRecurringType.YEARLY -> lastPaymentDate + ExpensesRepository.RECURRING_YEARLY
            }

            expenseDataSource.setRecurringPayment(
                merchant,
                recurringType.name,
                nextPaymentDate,
            )

            // Schedule reminder for next payment
            taskManager.schedulePaymentReminder(
                merchantName = merchant,
                nextPaymentDate = nextPaymentDate,
                reminderTime = nextPaymentDate - ExpensesRepository.REMINDER_TIME_BEFORE_PAYMENT,
            )
        }
    }

    /**
     * Processes the SMS to extract the expense information.
     *
     * @param sms The SMS to be processed.
     */
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

    /**
     * Checks if the budget is exceeded and shows a warning if it is.
     */
    private suspend fun checkBudgetExceeded() {
        val monthInfo = getMonthInfoAt(0)

        val budgetAmount = budgetDataSource.getBudgetForMonth(monthInfo.startDate)
            .firstOrNull()?.amount

        if (budgetAmount == null) {
            Timber.w("Budget not set for month: ${monthInfo.startDate}")
            return
        }

        val totalSpending = expenseDataSource.getTotalSpending(
            startDate = monthInfo.startDate,
            endDate = monthInfo.endDate,
        ).firstOrNull() ?: 0.0

        if (totalSpending > budgetAmount) {
            Timber.w("Budget exceeded! Total spending: $totalSpending")
            taskManager.showBudgetExceedWarning(
                budgetAmount = budgetAmount,
                currentAmount = totalSpending,
            )
        }
    }
}
