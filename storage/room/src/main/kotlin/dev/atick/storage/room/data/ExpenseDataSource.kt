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

package dev.atick.storage.room.data

import dev.atick.storage.room.models.CumulativeExpense
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing and managing expense data.
 */
interface ExpenseDataSource {
    /**
     * Retrieves all expenses within a specified date range.
     *
     * @param startDate The start date of the range (inclusive) in milliseconds since epoch.
     * @param endDate The end date of the range (inclusive) in milliseconds since epoch.
     * @return A Flow emitting a list of ExpenseEntity objects within the specified date range.
     */
    fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    /**
     * Retrieves an expense by its ID.
     *
     * @param id The ID of the expense to retrieve.
     * @return A Flow emitting the ExpenseEntity object with the specified ID, or null if not found.
     */
    fun getExpenseById(id: Long): Flow<ExpenseEntity?>

    /**
     * Retrieves all recurring expenses.
     *
     * @return A Flow emitting a list of ExpenseEntity objects that are recurring.
     */
    fun getRecurringExpenses(): Flow<List<ExpenseEntity>>

    /**
     * Inserts a new expense into the data source.
     *
     * @param expense The ExpenseEntity object representing the expense to be inserted.
     * @return The ID of the newly inserted expense.
     */
    suspend fun insertExpense(expense: ExpenseEntity): Long

    /**
     * Updates an existing expense in the data source.
     *
     * @param expense The ExpenseEntity object representing the expense to be updated.
     */
    suspend fun updateExpense(expense: ExpenseEntity)

    /**
     * Deletes an expense from the data source.
     *
     * @param expense The ExpenseEntity object representing the expense to be deleted.
     */
    suspend fun deleteExpense(expense: ExpenseEntity)

    /**
     * Retrieves the last payment date for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @return The last payment date in milliseconds since epoch.
     */
    suspend fun getLastPaymentDate(merchant: String): Long

    /**
     * Retrieves the next payment date for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @return The next payment date in milliseconds since epoch, or null if not found.
     */
    suspend fun getNextPaymentDate(merchant: String): Long?

    /**
     * Retrieves the time of the last expense.
     *
     * @return The time of the last expense in milliseconds since epoch.
     */
    suspend fun getLastExpenseTime(): Long

    /**
     * Retrieves the total spending within a specified date range.
     *
     * @param startDate The start date of the range (inclusive) in milliseconds since epoch.
     * @param endDate The end date of the range (inclusive) in milliseconds since epoch.
     * @return A Flow emitting the total spending as a Double.
     */
    fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double>

    /**
     * Retrieves cumulative expenses within a specified date range.
     *
     * @param startDate The start date of the range (inclusive) in milliseconds since epoch.
     * @param endDate The end date of the range (inclusive) in milliseconds since epoch.
     * @return A Flow emitting a list of CumulativeExpense objects within the specified date range.
     */
    fun getCumulativeExpenses(startDate: Long, endDate: Long): Flow<List<CumulativeExpense>>

    /**
     * Sets a recurring payment for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @param recurringType The type of recurring payment.
     * @param nextRecurringDate The next recurring date in milliseconds since epoch.
     */
    suspend fun setRecurringPayment(
        merchant: String,
        recurringType: String,
        nextRecurringDate: Long,
    )

    /**
     * Sets the cancellation status for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @param toBeCancelled The cancellation status to be set.
     */
    suspend fun setCancellation(merchant: String, toBeCancelled: Boolean)
}
