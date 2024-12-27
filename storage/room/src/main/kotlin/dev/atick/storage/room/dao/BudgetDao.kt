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
import androidx.room.Query
import androidx.room.Upsert
import dev.atick.storage.room.models.BudgetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the budgets table.
 */
@Dao
interface BudgetDao {
    /**
     * Returns a [Flow] of [BudgetEntity] representing the budget for the given month.
     *
     * @param month The month for which the budget is to be retrieved.
     * @return A [Flow] of [BudgetEntity] representing the budget for the given month.
     */
    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetForMonth(month: Long): Flow<BudgetEntity?>

    /**
     * Inserts or updates the given [BudgetEntity] in the budgets table.
     *
     * @param budget The [BudgetEntity] to be inserted or updated.
     */
    @Upsert
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)

    /**
     * Deletes the given [BudgetEntity] from the budgets table.
     *
     * @param budget The [BudgetEntity] to be deleted.
     */
    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)
}
