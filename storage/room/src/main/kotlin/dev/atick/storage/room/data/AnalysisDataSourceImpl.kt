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

import dev.atick.core.di.ApplicationScope
import dev.atick.core.di.IoDispatcher
import dev.atick.storage.room.dao.ExpenseDao
import dev.atick.storage.room.models.ExpenseAnalysis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnalysisDataSourceImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : AnalysisDataSource {
    override fun getCategoryAnalyses(
        startDate: Long,
        endDate: Long,
        topN: Int,
    ): Flow<List<ExpenseAnalysis>> {
        val topExpenses = expenseDao.getTopExpensesByCategory(startDate, endDate, topN)
        val totalSpending = getTotalSpending(startDate, endDate)
        return combine(topExpenses, totalSpending) { expenses, total ->
            expenses.map {
                ExpenseAnalysis(
                    categoryOrMerchant = it.categoryOrMerchant,
                    spending = it.spending,
                    currency = it.currency,
                    maxAmount = it.maxAmount,
                    minAmount = it.minAmount,
                    percentage = if (total == 0.0) 0.0 else it.spending / total * 100,
                )
            }
        }.flowOn(ioDispatcher)
    }

    override fun getMerchantAnalyses(
        startDate: Long,
        endDate: Long,
        topN: Int,
    ): Flow<List<ExpenseAnalysis>> {
        val topExpenses = expenseDao.getTopExpensesByMerchant(startDate, endDate, topN)
        val totalSpending = getTotalSpending(startDate, endDate)
        return combine(topExpenses, totalSpending) { expenses, total ->
            expenses.map {
                ExpenseAnalysis(
                    categoryOrMerchant = it.categoryOrMerchant,
                    spending = it.spending,
                    currency = it.currency,
                    maxAmount = it.maxAmount,
                    minAmount = it.minAmount,
                    percentage = if (total == 0.0) 0.0 else it.spending / total * 100,
                )
            }
        }.flowOn(ioDispatcher)
    }

    override fun getTotalSpending(
        startDate: Long,
        endDate: Long,
    ): Flow<Double> {
        return expenseDao.getTotalSpending(startDate, endDate)
            .map { it ?: 0.0 }
            .flowOn(ioDispatcher)
    }
}
