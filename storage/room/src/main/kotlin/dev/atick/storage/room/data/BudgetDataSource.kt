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

/**
 * Interface for accessing and managing budget data.
 */
interface BudgetDataSource {
    /**
     * Retrieves the budget for a specific month.
     *
     * @param month The month for which the budget is to be retrieved, represented as milliseconds since epoch.
     * @return A Flow emitting the BudgetEntity for the specified month, or null if no budget is found.
     */
    fun getBudgetForMonth(month: Long): Flow<BudgetEntity?>

    /**
     * Inserts or updates a budget in the data source.
     *
     * @param budget The BudgetEntity to be inserted or updated.
     */
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)

    /**
     * Deletes a budget from the data source.
     *
     * @param budget The BudgetEntity to be deleted.
     */
    suspend fun deleteBudget(budget: BudgetEntity)
}
