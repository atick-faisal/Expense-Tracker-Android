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
import androidx.room.Update
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Long): Flow<ExpenseEntity?>

    @Query("SELECT * FROM expenses WHERE recurringType != 'NONE'")
    fun getRecurringExpenses(): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT * FROM expenses 
        WHERE nextRecurringDate IS NOT NULL 
        AND nextRecurringDate <= :date 
        AND paymentStatus != 'CANCELLED'
    """,
    )
    fun getUpcomingRecurringExpenses(date: Long): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT * FROM expenses 
        WHERE toBeCancelled = 1 
        AND nextRecurringDate IS NOT NULL 
        AND nextRecurringDate <= :date
    """,
    )
    fun getExpensesToBeCancelled(date: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE categoryType = :categoryType")
    fun getExpensesByCategory(categoryType: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query(
        """
        SELECT SUM(amount) FROM expenses 
        WHERE categoryType = :categoryType 
        AND paymentStatus = 'PAID' 
        AND paymentDate BETWEEN :startDate AND :endDate
    """,
    )
    fun getCategorySpending(categoryType: String, startDate: Long, endDate: Long): Flow<Double?>
}
