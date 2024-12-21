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

import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseDataSource {
    fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>


//    fun getExpenseById(id: Long): Flow<ExpenseEntity?>
//    fun getRecurringExpenses(): Flow<List<ExpenseEntity>>
//    fun getUpcomingRecurringExpenses(date: Long): Flow<List<ExpenseEntity>>
//    fun getExpensesToBeCancelled(date: Long): Flow<List<ExpenseEntity>>
//    fun getExpensesByCategory(categoryType: String): Flow<List<ExpenseEntity>>

    suspend fun insertExpense(expense: ExpenseEntity): Long
    suspend fun updateExpense(expense: ExpenseEntity)
    suspend fun deleteExpense(expense: ExpenseEntity)

//    fun getCategorySpending(
//        categoryType: String,
//        startDate: Long,
//        endDate: Long,
//    ): Flow<Double>
//    suspend fun getTotalAmount(startDate: Long, endDate: Long): Double
//    fun getTopExpensesByDescription(
//        startDate: Long,
//        endDate: Long,
//        n: Int = 10,
//    ): Flow<List<ExpenseGroup>>
    suspend fun getLastExpenseTime(): Long
}
