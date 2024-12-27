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

import dev.atick.core.di.IoDispatcher
import dev.atick.storage.room.dao.ExpenseDao
import dev.atick.storage.room.models.CumulativeExpense
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of the ExpenseDataSource interface for accessing and managing expense data.
 *
 * @property expenseDao The DAO for accessing expense data.
 * @property ioDispatcher The CoroutineDispatcher for IO operations.
 */
class ExpenseDataSourceImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ExpenseDataSource {

    /**
     * Retrieves all expenses within a specified date range.
     *
     * @param startDate The start date of the range (inclusive) in milliseconds since epoch.
     * @param endDate The end date of the range (inclusive) in milliseconds since epoch.
     * @return A Flow emitting a list of ExpenseEntity objects representing the expenses within the specified date range.
     */
    override fun getAllExpenses(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses(startDate, endDate).flowOn(ioDispatcher)
    }

    /**
     * Retrieves an expense by its ID.
     *
     * @param id The ID of the expense to retrieve.
     * @return A Flow emitting the ExpenseEntity object with the specified ID, or null if not found.
     */
    override fun getExpenseById(id: Long): Flow<ExpenseEntity?> {
        return expenseDao.getExpenseById(id).flowOn(ioDispatcher)
    }

    /**
     * Retrieves all recurring expenses.
     *
     * @return A Flow emitting a list of ExpenseEntity objects that are recurring.
     */
    override fun getRecurringExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getRecurringExpenses().flowOn(ioDispatcher)
    }

    /**
     * Inserts a new expense into the data source.
     *
     * @param expense The ExpenseEntity object representing the expense to be inserted.
     * @return The ID of the newly inserted expense.
     */
    override suspend fun insertExpense(expense: ExpenseEntity): Long {
        return withContext(ioDispatcher) {
            expenseDao.insertExpense(expense)
        }
    }

    /**
     * Updates an existing expense in the data source.
     *
     * @param expense The ExpenseEntity object representing the expense to be updated.
     */
    override suspend fun updateExpense(expense: ExpenseEntity) {
        withContext(ioDispatcher) {
            expenseDao.updateExpense(expense)
        }
    }

    /**
     * Deletes an expense from the data source.
     *
     * @param expense The ExpenseEntity object representing the expense to be deleted.
     */
    override suspend fun deleteExpense(expense: ExpenseEntity) {
        withContext(ioDispatcher) {
            expenseDao.deleteExpense(expense)
        }
    }

    /**
     * Retrieves the last payment date for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @return The last payment date in milliseconds since epoch.
     */
    override suspend fun getLastPaymentDate(merchant: String): Long {
        return withContext(ioDispatcher) {
            expenseDao.getLastPaymentDate(merchant) ?: System.currentTimeMillis()
        }
    }

    /**
     * Retrieves the next payment date for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @return The next payment date in milliseconds since epoch, or null if not found.
     */
    override suspend fun getNextPaymentDate(merchant: String): Long? {
        return withContext(ioDispatcher) {
            expenseDao.getNextPaymentDate(merchant)
        }
    }

    /**
     * Retrieves the time of the last expense.
     *
     * @return The time of the last expense in milliseconds since epoch.
     */
    override suspend fun getLastExpenseTime(): Long {
        return withContext(ioDispatcher) {
            expenseDao.getLastExpenseTime() ?: 0
        }
    }

    /**
     * Retrieves the total spending within a specified date range.
     *
     * @param startDate The start date of the range (inclusive) in milliseconds since epoch.
     * @param endDate The end date of the range (inclusive) in milliseconds since epoch.
     * @return A Flow emitting the total spending as a Double.
     */
    override fun getTotalSpending(
        startDate: Long,
        endDate: Long,
    ): Flow<Double> {
        return expenseDao.getTotalSpending(startDate, endDate).flowOn(ioDispatcher)
            .map { it ?: 0.0 }
    }

    /**
     * Retrieves cumulative expenses within a specified date range.
     *
     * @param startDate The start date of the range (inclusive) in milliseconds since epoch.
     * @param endDate The end date of the range (inclusive) in milliseconds since epoch.
     * @return A Flow emitting a list of CumulativeExpense objects within the specified date range.
     */
    override fun getCumulativeExpenses(
        startDate: Long,
        endDate: Long,
    ): Flow<List<CumulativeExpense>> {
        return expenseDao.getCumulativeExpenses(startDate, endDate).flowOn(ioDispatcher)
    }

    /**
     * Sets a recurring payment for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @param recurringType The type of recurring payment.
     * @param nextRecurringDate The next recurring date in milliseconds since epoch.
     */
    override suspend fun setRecurringPayment(
        merchant: String,
        recurringType: String,
        nextRecurringDate: Long,
    ) {
        withContext(ioDispatcher) {
            expenseDao.setRecurringType(merchant, recurringType, nextRecurringDate)
        }
    }

    /**
     * Sets the cancellation status for a specific merchant.
     *
     * @param merchant The name of the merchant.
     * @param toBeCancelled The cancellation status to be set.
     */
    override suspend fun setCancellation(merchant: String, toBeCancelled: Boolean) {
        withContext(ioDispatcher) {
            expenseDao.setCancellation(merchant, toBeCancelled)
        }
    }
}
