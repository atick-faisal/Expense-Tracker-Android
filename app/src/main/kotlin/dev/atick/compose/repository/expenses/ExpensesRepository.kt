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
import dev.atick.compose.worker.SyncProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching expenses data.
 */
interface ExpensesRepository {
    companion object {
        /**
         * The duration for which the SMS should be synced.
         */
        const val SYNC_SMS_DURATION = 30 * 24 * 60 * 60 * 1000L // 30 days

        /**
         * The list of bank names.
         */
        val BANK_NAMES = listOf("QNB", "QIB", "CBQ", "Doha Bank")

        /**
         * The list of keywords to search for in the SMS.
         */
        val KEYWORDS = listOf("purchase", "transaction")

        /**
         * The list of words to ignore in the SMS.
         */
        val IGNORE_WORDS = listOf("OTP", "withdrawal", "deposit", "bonus", "refund")

        /**
         * The recurring duration for daily expenses.
         */
        const val RECURRING_DAILY = 24 * 60 * 60 * 1000L

        /**
         * The recurring duration for weekly expenses.
         */
        const val RECURRING_WEEKLY = 7 * 24 * 60 * 60 * 1000L

        /**
         * The recurring duration for monthly expenses.
         */
        const val RECURRING_MONTHLY = 30 * 24 * 60 * 60 * 1000L

        /**
         * The recurring duration for yearly expenses.
         */
        const val RECURRING_YEARLY = 365 * 24 * 60 * 60 * 1000L

        /**
         * The reminder time before payment.
         */
        const val REMINDER_TIME_BEFORE_PAYMENT = 3 * 24 * 60 * 60 * 1000L
    }

    /**
     * A [Flow] that emits a [Boolean] indicating whether the expenses are syncing.
     */
    val isSyncing: Flow<Boolean>

    /**
     * Gets all the expenses.
     *
     * @param startDate The start date of the expenses.
     * @param endDate The end date of the expenses.
     * @return A [Flow] of [List] of [UiExpense] representing the expenses.
     */
    fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<UiExpense>>

    /**
     * Gets the expense by ID.
     *
     * @param id The ID of the expense.
     * @return A [Flow] of [UiExpense] representing the expense.
     */
    fun getExpenseById(id: Long): Flow<UiExpense>

    /**
     * Inserts the expense.
     *
     * @param expense The expense to be inserted.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun updateExpense(expense: UiExpense): Result<Unit>

    /**
     * Deletes the expense.
     *
     * @param expense The expense to be deleted.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun deleteExpense(expense: UiExpense): Result<Unit>

    /**
     * Requests a sync of the expenses.
     *
     * @return A [Result] indicating the success or failure of the operation.
     */
    @RequiresPermission(android.Manifest.permission.READ_SMS)
    fun requestSync(): Result<Unit>

    /**
     * Syncs the expenses from the SMS.
     *
     * @return A [Flow] of [SyncProgress] representing the progress of the sync.
     */
    @RequiresPermission(android.Manifest.permission.READ_SMS)
    fun syncExpensesFromSms(): Flow<SyncProgress>

    /**
     * Sets the recurring type for the merchant.
     *
     * @param merchant The merchant for which the recurring type is to be set.
     * @param recurringType The recurring type to be set.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun setRecurringType(merchant: String, recurringType: UiRecurringType): Result<Unit>
}
