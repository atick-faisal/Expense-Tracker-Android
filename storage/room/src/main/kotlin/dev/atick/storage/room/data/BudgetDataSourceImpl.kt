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
import dev.atick.storage.room.dao.BudgetDao
import dev.atick.storage.room.models.BudgetEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BudgetDataSourceImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BudgetDataSource {
    override fun getAllBudgets(
        startDate: Long,
        endDate: Long,
    ): Flow<List<BudgetEntity>> {
        return budgetDao.getAllBudgets(startDate, endDate).flowOn(ioDispatcher)
    }

    override fun getBudgetById(id: Long): Flow<BudgetEntity?> {
        return budgetDao.getBudgetById(id).flowOn(ioDispatcher)
    }

    override fun getCategoryBudgets(
        startDate: Long,
        endDate: Long,
    ): Flow<List<BudgetEntity>> {
        return budgetDao.getCategoryBudgets(startDate, endDate).flowOn(ioDispatcher)
    }

    override fun getMerchantBudgets(
        startDate: Long,
        endDate: Long,
    ): Flow<List<BudgetEntity>> {
        return budgetDao.getMerchantBudgets(startDate, endDate).flowOn(ioDispatcher)
    }

    override fun getBudgetFor(
        categoryOrMerchantName: String,
        isMerchant: Boolean,
        startDate: Long,
        endDate: Long,
    ): Flow<BudgetEntity?> {
        return budgetDao.getBudgetFor(categoryOrMerchantName, isMerchant, startDate, endDate)
            .flowOn(ioDispatcher)
    }

    override fun getTotalBudget(
        startDate: Long,
        endDate: Long,
    ): Flow<Double?> {
        return budgetDao.getTotalBudget(startDate, endDate).flowOn(ioDispatcher)
    }

    override fun getTotalCategoryBudget(
        startDate: Long,
        endDate: Long,
    ): Flow<Double?> {
        return budgetDao.getTotalCategoryBudget(startDate, endDate).flowOn(ioDispatcher)
    }

    override fun getTotalMerchantBudget(
        startDate: Long,
        endDate: Long,
    ): Flow<Double?> {
        return budgetDao.getTotalMerchantBudget(startDate, endDate).flowOn(ioDispatcher)
    }

    override suspend fun insertBudget(budget: BudgetEntity) {
        withContext(ioDispatcher) {
            budgetDao.insertBudget(budget)
        }
    }

    override suspend fun insertAllBudgets(budgets: List<BudgetEntity>) {
        withContext(ioDispatcher) {
            budgetDao.insertAllBudgets(budgets)
        }
    }

    override suspend fun updateBudget(budget: BudgetEntity) {
        withContext(ioDispatcher) {
            budgetDao.updateBudget(budget)
        }
    }

    override suspend fun deleteBudget(budget: BudgetEntity) {
        withContext(ioDispatcher) {
            budgetDao.deleteBudget(budget)
        }
    }
}
