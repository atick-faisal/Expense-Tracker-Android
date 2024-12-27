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

package dev.atick.compose.repository.budgets

import dev.atick.compose.data.budgets.UiBudget
import dev.atick.compose.data.budgets.UiCumulativeExpense
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching budget data.
 */
interface BudgetsRepository {
    /**
     * Gets the cumulative expenses.
     *
     * @param startDate The start date of the analysis.
     * @param endDate The end date of the analysis.
     * @return A [Flow] of [List] of [UiCumulativeExpense] representing the cumulative expenses.
     */
    fun getCumulativeExpenses(startDate: Long, endDate: Long): Flow<List<UiCumulativeExpense>>

    /**
     * Gets the budget for the month.
     *
     * @param month The month for which the budget is to be fetched.
     * @return A [Flow] of [UiBudget] representing the budget for the month.
     */
    fun getBudgetForMonth(month: Long): Flow<UiBudget>

    /**
     * Inserts or updates the budget.
     *
     * @param budget The budget to be inserted or updated.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun insertOrUpdateBudget(budget: UiBudget): Result<Unit>

    /**
     * Deletes the budget.
     *
     * @param budget The budget to be deleted.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun deleteBudget(budget: UiBudget): Result<Unit>
}
