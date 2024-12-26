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

class BudgetsRepositoryImpl @Inject constructor(
    private val expenseDataSource: ExpenseDataSource,
    private val budgetDataSource: BudgetDataSource,
) : BudgetsRepository {
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

    override fun getBudgetForMonth(month: Long): Flow<UiBudget> {
        return budgetDataSource.getBudgetForMonth(month)
            .map { budgetEntity ->
                UiBudget(
                    month = budgetEntity?.month ?: month,
                    amount = budgetEntity?.amount,
                )
            }
    }

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
