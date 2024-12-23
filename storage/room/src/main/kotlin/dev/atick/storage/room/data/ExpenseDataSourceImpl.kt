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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseDataSourceImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ExpenseDataSource {
    override fun getAllExpenses(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses(startDate, endDate).flowOn(ioDispatcher)
    }

    override fun getExpenseById(id: Long): Flow<ExpenseEntity?> {
        return expenseDao.getExpenseById(id).flowOn(ioDispatcher)
    }

    override fun getRecurringExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getRecurringExpenses().flowOn(ioDispatcher)
    }

//    override fun getExpenseById(id: Long): Flow<ExpenseEntity?> {
//        return expenseDao.getExpenseById(id).flowOn(ioDispatcher)
//    }
//
//    override fun getRecurringExpenses(): Flow<List<ExpenseEntity>> {
//        return expenseDao.getRecurringExpenses().flowOn(ioDispatcher)
//    }
//
//    override fun getUpcomingRecurringExpenses(date: Long): Flow<List<ExpenseEntity>> {
//        return expenseDao.getUpcomingRecurringExpenses(date).flowOn(ioDispatcher)
//    }
//
//    override fun getExpensesToBeCancelled(date: Long): Flow<List<ExpenseEntity>> {
//        return expenseDao.getExpensesToBeCancelled(date).flowOn(ioDispatcher)
//    }

//    override fun getExpensesByCategory(categoryType: String): Flow<List<ExpenseEntity>> {
//        return expenseDao.getExpensesByCategory(categoryType).flowOn(ioDispatcher)
//    }

    override suspend fun insertExpense(expense: ExpenseEntity): Long {
        return withContext(ioDispatcher) {
            expenseDao.insertExpense(expense)
        }
    }

    override suspend fun updateExpense(expense: ExpenseEntity) {
        withContext(ioDispatcher) {
            expenseDao.updateExpense(expense)
        }
    }

    override suspend fun deleteExpense(expense: ExpenseEntity) {
        withContext(ioDispatcher) {
            expenseDao.deleteExpense(expense)
        }
    }

//    override fun getCategorySpending(
//        categoryType: String,
//        startDate: Long,
//        endDate: Long,
//    ): Flow<Double> {
//        return expenseDao.getCategorySpending(
//            categoryType = categoryType,
//            startDate = startDate,
//            endDate = endDate,
//        ).map { it ?: 0.0 }.flowOn(ioDispatcher)
//    }
//
//    override suspend fun getTotalAmount(
//        startDate: Long,
//        endDate: Long,
//    ): Double {
//        return withContext(ioDispatcher) {
//            expenseDao.getTotalAmount(startDate, endDate) ?: 0.0
//        }
//    }
//
//    override fun getTopExpensesByDescription(
//        startDate: Long,
//        endDate: Long,
//        n: Int,
//    ): Flow<List<ExpenseGroup>> {
//        return expenseDao.getTopExpensesByDescription(startDate, endDate, n).flowOn(ioDispatcher)
//    }

    override suspend fun getLastExpenseTime(): Long {
        return withContext(ioDispatcher) {
            expenseDao.getLastExpenseTime() ?: 0
        }
    }

    override fun getCumulativeExpenses(
        startDate: Long,
        endDate: Long,
    ): Flow<List<CumulativeExpense>> {
        return expenseDao.getCumulativeExpenses(startDate, endDate).flowOn(ioDispatcher)
    }

    override suspend fun setRecurringType(merchant: String, recurringType: String) {
        withContext(ioDispatcher) {
            expenseDao.setRecurringType(merchant, recurringType)
        }
    }

    override suspend fun setCancellation(merchant: String, toBeCancelled: Boolean) {
        withContext(ioDispatcher) {
            expenseDao.setCancellation(merchant, toBeCancelled)
        }
    }
}
