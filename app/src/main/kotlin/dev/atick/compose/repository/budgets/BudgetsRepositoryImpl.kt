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
import dev.atick.core.utils.suspendRunCatching
import dev.atick.storage.room.data.BudgetDataSource
import dev.atick.storage.room.data.ExpenseDataSource
import dev.atick.storage.room.models.BudgetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [BudgetsRepository] that fetches data from the [BudgetDataSource] and [ExpenseDataSource].
 *
 * @param expenseDataSource The data source for expense data.
 * @param budgetDataSource The data source for budget data.
 */
class BudgetsRepositoryImpl @Inject constructor(
    private val expenseDataSource: ExpenseDataSource,
    private val budgetDataSource: BudgetDataSource,
) : BudgetsRepository {

    /**
     * Gets the cumulative expenses.
     *
     * @param startDate The start date of the analysis.
     * @param endDate The end date of the analysis.
     * @return A [Flow] of [List] of [UiCumulativeExpense] representing the cumulative expenses.
     */
    override fun getCumulativeExpenses(
        startDate: Long,
        endDate: Long,
    ): Flow<List<UiCumulativeExpense>> {
        return expenseDataSource.getCumulativeExpenses(startDate, endDate)
            .map { cumulativeExpenses ->
                cumulativeExpenses.map { cumulativeExpense ->
                    UiCumulativeExpense(
                        amount = cumulativeExpense.amount,
                        atTime = cumulativeExpense.atTime,

                    )
                }
            }
    }

    /**
     * Gets the budget for the month.
     *
     * @param month The month for which the budget is to be fetched.
     * @return A [Flow] of [UiBudget] representing the budget for the month.
     */
    override fun getBudgetForMonth(month: Long): Flow<UiBudget> {
        return budgetDataSource.getBudgetForMonth(month)
            .map { budgetEntity ->
                UiBudget(
                    month = budgetEntity?.month ?: month,
                    amount = budgetEntity?.amount,
                )
            }
    }

    /**
     * Inserts or updates the budget.
     *
     * @param budget The budget to be inserted or updated.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun insertOrUpdateBudget(budget: UiBudget): Result<Unit> {
        return suspendRunCatching {
            budgetDataSource.insertOrUpdateBudget(
                BudgetEntity(
                    month = budget.month,
                    amount = budget.amount!!,
                ),
            )
        }
    }

    /**
     * Deletes the budget.
     *
     * @param budget The budget to be deleted.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun deleteBudget(budget: UiBudget): Result<Unit> {
        return suspendRunCatching {
            budgetDataSource.deleteBudget(
                BudgetEntity(
                    month = budget.month,
                    amount = budget.amount!!,
                ),
            )
        }
    }
}
