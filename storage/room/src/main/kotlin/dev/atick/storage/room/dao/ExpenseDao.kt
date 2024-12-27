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

package dev.atick.storage.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.atick.storage.room.models.CumulativeExpense
import dev.atick.storage.room.models.ExpenseAnalysis
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the expenses table.
 */
@Dao
interface ExpenseDao {
    /**
     * Get the list of all the expenses in the expenses table.
     *
     * @param startDate The start date of the expenses to be retrieved.
     * @param endDate The end date of the expenses to be retrieved.
     * @return A [Flow] of [ExpenseEntity] list representing all the expenses in the expenses table.
     */
    @Query(
        """
        SELECT * FROM expenses 
        WHERE paymentDate BETWEEN :startDate AND :endDate 
        ORDER BY paymentDate DESC
    """,
    )
    fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    /**
     * Get the expense with the given ID.
     *
     * @param id The ID of the expense to be retrieved.
     * @return A [Flow] of [ExpenseEntity] representing the expense with the given ID.
     */
    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Long): Flow<ExpenseEntity?>

    /**
     * Get the list of all the recurring expenses in the expenses table.
     *
     * @return A [Flow] of [ExpenseEntity] list representing all the recurring expenses in the expenses table.
     */
    @Query("SELECT * FROM expenses WHERE recurringType != 'ONETIME' GROUP BY merchant")
    fun getRecurringExpenses(): Flow<List<ExpenseEntity>>

    /**
     * Get the list of all the upcoming recurring expenses in the expenses table.
     *
     * @param date The date for which the upcoming recurring expenses are to be retrieved.
     * @return A [Flow] of [ExpenseEntity] list representing all the upcoming recurring expenses in the expenses table.
     */
    @Query(
        """
        SELECT * FROM expenses 
        WHERE nextRecurringDate IS NOT NULL 
        AND nextRecurringDate <= :date 
        AND paymentStatus != 'CANCELLED'
    """,
    )
    fun getUpcomingRecurringExpenses(date: Long): Flow<List<ExpenseEntity>>

    /**
     * Get the last payment date for the given merchant.
     *
     * @param merchant The merchant for which the last payment date is to be retrieved.
     * @return A [Flow] of [Long] representing the last payment date for the given merchant.
     */
    @Query(
        """
        SELECT paymentDate FROM expenses 
        WHERE merchant = :merchant
        ORDER BY paymentDate DESC
        LIMIT 1
    """,
    )
    suspend fun getLastPaymentDate(merchant: String): Long?

    /**
     * Get the next payment date for the given merchant.
     *
     * @param merchant The merchant for which the next payment date is to be retrieved.
     * @return A [Flow] of [Long] representing the next payment date for the given merchant.
     */
    @Query(
        """
        SELECT nextRecurringDate FROM expenses 
        WHERE merchant = :merchant
    """,
    )
    suspend fun getNextPaymentDate(merchant: String): Long?

    /**
     * Get the list of all the expenses to be cancelled in the expenses table.
     *
     * @param date The date for which the expenses to be cancelled are to be retrieved.
     * @return A [Flow] of [ExpenseEntity] list representing all the expenses to be cancelled in the expenses table.
     */
    @Query(
        """
        SELECT * FROM expenses 
        WHERE toBeCancelled = 1 
        AND nextRecurringDate IS NOT NULL 
        AND nextRecurringDate <= :date
    """,
    )
    fun getExpensesToBeCancelled(date: Long): Flow<List<ExpenseEntity>>

    /**
     * Inserts the given [ExpenseEntity] in the expenses table.
     *
     * @param expense The [ExpenseEntity] to be inserted.
     * @return The ID of the inserted expense.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    /**
     * Updates or inserts the given [ExpenseEntity] in the expenses table.
     *
     * @param expense The [ExpenseEntity] to be updated or inserted.
     */
    @Upsert
    suspend fun updateExpense(expense: ExpenseEntity)

    /**
     * Deletes the given [ExpenseEntity] from the expenses table.
     *
     * @param expense The [ExpenseEntity] to be deleted.
     */
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    /**
     * Get the total spending for the given category.
     *
     * @param category The category for which the total spending is to be retrieved.
     * @param startDate The start date for the spending to be retrieved.
     * @param endDate The end date for the spending to be retrieved.
     * @return A [Flow] of [Double] representing the total spending for the given category.
     */
    @Query(
        """
        SELECT SUM(amount) FROM expenses 
        WHERE category = :category 
        AND paymentDate BETWEEN :startDate AND :endDate
    """,
    )
    fun getCategorySpending(category: String, startDate: Long, endDate: Long): Flow<Double?>

    /**
     * Get the total spending for the given merchant.
     *
     * @param merchant The merchant for which the total spending is to be retrieved.
     * @param startDate The start date for the spending to be retrieved.
     * @param endDate The end date for the spending to be retrieved.
     * @return A [Flow] of [Double] representing the total spending for the given merchant.
     */
    @Query(
        """
        SELECT SUM(amount) FROM expenses 
        WHERE merchant = :merchant
        AND paymentDate BETWEEN :startDate AND :endDate
    """,
    )
    fun getMerchantSpending(merchant: String, startDate: Long, endDate: Long): Flow<Double?>

    /**
     * Get the total spending for a given date range.
     *
     * @param startDate The start date for the spending to be retrieved.
     * @param endDate The end date for the spending to be retrieved.
     * @return A [Flow] of [Double] representing the total spending for the given date range.
     */
    @Query(
        """
        SELECT SUM(amount) FROM expenses 
        WHERE paymentDate BETWEEN :startDate AND :endDate
    """,
    )
    fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double?>

    /**
     * Get top expenses by category for the given date range.
     *
     * @param startDate The start date for the expenses to be retrieved.
     * @param endDate The end date for the expenses to be retrieved.
     * @param n The number of top expenses to be retrieved.
     * @return A [Flow] of [ExpenseAnalysis] list representing the top expenses by category.
     */
    // TODO: Implement better currency handling
    @Query(
        """
        SELECT 
            category as categoryOrMerchant,
            SUM(amount) as spending,
            MAX(amount) as maxAmount,
            MIN(amount) as minAmount,
            0.0 as percentage,
            CASE 
                WHEN MIN(currency) = MAX(currency) THEN MIN(currency)
                ELSE 'QAR'
            END as currency
        FROM expenses 
        WHERE paymentDate BETWEEN :startDate AND :endDate
        GROUP BY category 
        ORDER BY spending DESC 
        LIMIT :n
    """,
    )
    fun getTopExpensesByCategory(
        startDate: Long,
        endDate: Long,
        n: Int = 5,
    ): Flow<List<ExpenseAnalysis>>

    /**
     * Get top expenses by merchant for the given date range.
     *
     * @param startDate The start date for the expenses to be retrieved.
     * @param endDate The end date for the expenses to be retrieved.
     * @param n The number of top expenses to be retrieved.
     * @return A [Flow] of [ExpenseAnalysis] list representing the top expenses by merchant.
     */
    // TODO: Implement better currency handling
    @Query(
        """
        SELECT 
            merchant as categoryOrMerchant,
            SUM(amount) as spending,
            MAX(amount) as maxAmount,
            MIN(amount) as minAmount,
            0.0 as percentage,
            CASE 
                WHEN MIN(currency) = MAX(currency) THEN MIN(currency)
                ELSE 'QAR'
            END as currency
        FROM expenses 
        WHERE paymentDate BETWEEN :startDate AND :endDate
        GROUP BY merchant 
        ORDER BY spending DESC 
        LIMIT :n
    """,
    )
    fun getTopExpensesByMerchant(
        startDate: Long,
        endDate: Long,
        n: Int = 5,
    ): Flow<List<ExpenseAnalysis>>

    @Query("SELECT MAX(paymentDate) FROM expenses")
    suspend fun getLastExpenseTime(): Long?

    /**
     * Get the cumulative expenses for the given date range.
     *
     * @param startDate The start date for the cumulative expenses to be retrieved.
     * @param endDate The end date for the cumulative expenses to be retrieved.
     * @return A [Flow] of [CumulativeExpense] list representing the cumulative expenses for the given date range.
     */
    @Query(
        """
        SELECT 
            (SELECT SUM(e2.amount) 
             FROM expenses e2 
             WHERE e2.paymentDate <= e1.paymentDate 
             AND e2.paymentDate BETWEEN :startDate AND :endDate
            ) as amount,
            paymentDate as atTime
        FROM expenses e1
        WHERE paymentDate BETWEEN :startDate AND :endDate
        GROUP BY paymentDate
        ORDER BY paymentDate ASC
    """,
    )
    fun getCumulativeExpenses(startDate: Long, endDate: Long): Flow<List<CumulativeExpense>>

    /**
     * Set the recurring type and next recurring date for the given merchant.
     *
     * @param merchant The merchant for which the recurring type is to be set.
     * @param recurringType The recurring type to be set.
     * @param nextRecurringDate The next recurring date to be set.
     */
    @Query(
        """
        UPDATE expenses 
        SET recurringType = :recurringType,
        nextRecurringDate = :nextRecurringDate
        WHERE merchant = :merchant
    """,
    )
    suspend fun setRecurringType(merchant: String, recurringType: String, nextRecurringDate: Long)

    /**
     * Updates the cancellation status for the given merchant.
     *
     * @param merchant The merchant for which the cancellation status is to be updated.
     * @param toBeCancelled The new cancellation status to be set.
     */
    @Query(
        """
        UPDATE expenses 
        SET toBeCancelled = :toBeCancelled 
        WHERE merchant = :merchant
    """,
    )
    suspend fun setCancellation(merchant: String, toBeCancelled: Boolean)
}
