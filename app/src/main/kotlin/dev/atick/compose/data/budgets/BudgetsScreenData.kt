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

package dev.atick.compose.data.budgets

import dev.atick.core.utils.getMonthInfoAt

/**
 * Data class representing the budgets screen data.
 * @param budget The budget data.
 * @param cumulativeExpenses The list of cumulative expenses.
 */
data class BudgetsScreenData(
    val budget: UiBudget = UiBudget(),
    val cumulativeExpenses: List<UiCumulativeExpense> = emptyList(),
) {
    /**
     * The current expenses.
     */
    val currentExpenses: Double
        get() = cumulativeExpenses.maxByOrNull { it.atTime }?.amount ?: 0.0

    /**
     * The percentage of the budget used.
     */
    val percentageUsed: Double
        get() = if (budget.amount == null) {
            0.0
        } else if (budget.amount == 0.0) {
            100.0
        } else {
            (currentExpenses / budget.amount) * 100
        }

    /**
     * The remaining budget.
     */
    val remainingBudget: Double
        get() = (budget.amount ?: currentExpenses) - currentExpenses

    /**
     * Whether the budget is over.
     */
    val isOverBudget: Boolean
        get() = currentExpenses > (budget.amount ?: Double.MAX_VALUE)

    /**
     * The amount by which the budget is over.
     */
    val overBudgetAmount: Double
        get() = if (isOverBudget && budget.amount != null) {
            currentExpenses - budget.amount
        } else {
            0.0
        }

    /**
     * The status of the budget.
     */
    val budgetStatus: BudgetStatus
        get() = when {
            isOverBudget -> BudgetStatus.EXCEEDED
            percentageUsed >= 95 -> BudgetStatus.CRITICAL
            percentageUsed >= 75 -> BudgetStatus.WARNING
            else -> BudgetStatus.SAFE
        }
}

/**
 * Data class representing the UI budget data.
 * @param month The month for which the budget is set.
 * @param amount The budget amount.
 */
data class UiBudget(
    val month: Long = getMonthInfoAt(0).startDate,
    val amount: Double? = null,
)

/**
 * Enum class representing the status of the budget.
 */
enum class BudgetStatus {
    SAFE, // < 75% used
    WARNING, // 75-95% used
    CRITICAL, // 95-100% used
    EXCEEDED, // > 100% used
}

/**
 * Data class representing the UI cumulative expense data.
 * @param amount The amount of the expense.
 * @param atTime The time at which the expense was made.
 */
data class UiCumulativeExpense(
    val amount: Double,
    val atTime: Long,
)
