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

import dev.atick.storage.room.models.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface BudgetDataSource {
    fun getAllBudgets(startDate: Long, endDate: Long): Flow<List<BudgetEntity>>
    fun getBudgetById(id: Long): Flow<BudgetEntity?>
    fun getCategoryBudgets(startDate: Long, endDate: Long): Flow<List<BudgetEntity>>
    fun getMerchantBudgets(startDate: Long, endDate: Long): Flow<List<BudgetEntity>>
    fun getBudgetFor(
        categoryOrMerchantName: String,
        isMerchant: Boolean,
        startDate: Long,
        endDate: Long,
    ): Flow<BudgetEntity?>
    fun getTotalBudget(startDate: Long, endDate: Long): Flow<Double?>
    fun getTotalCategoryBudget(startDate: Long, endDate: Long): Flow<Double?>
    fun getTotalMerchantBudget(startDate: Long, endDate: Long): Flow<Double?>
    suspend fun insertBudget(budget: BudgetEntity)
    suspend fun insertAllBudgets(budgets: List<BudgetEntity>)
    suspend fun updateBudget(budget: BudgetEntity)
    suspend fun deleteBudget(budget: BudgetEntity)
}
