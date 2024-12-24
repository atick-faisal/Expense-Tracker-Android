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

data class BudgetsScreenData(
    val budget: UiBudget = UiBudget(),
    val cumulativeExpenses: List<UiCumulativeExpense> = emptyList(),
) {
    val currentExpenses: Double
        get() = cumulativeExpenses.maxByOrNull { it.atTime }?.amount ?: 0.0

    val percentageUsed: Double
        get() = if (budget.amount == 0.0) {
            100.0
        } else {
            (currentExpenses / budget.amount) * 100
        }

    val remainingBudget: Double
        get() = budget.amount - currentExpenses

    val isOverBudget: Boolean
        get() = currentExpenses > budget.amount

    val overBudgetAmount: Double
        get() = if (isOverBudget) currentExpenses - budget.amount else 0.0

    val budgetStatus: BudgetStatus
        get() = when {
            isOverBudget -> BudgetStatus.EXCEEDED
            percentageUsed >= 95 -> BudgetStatus.CRITICAL
            percentageUsed >= 75 -> BudgetStatus.WARNING
            else -> BudgetStatus.SAFE
        }
}

data class UiBudget(
    val month: Long = getMonthInfoAt(0).startDate,
    val amount: Double = 0.0,
)

enum class BudgetStatus {
    SAFE, // < 75% used
    WARNING, // 75-95% used
    CRITICAL, // 95-100% used
    EXCEEDED, // > 100% used
}

data class UiCumulativeExpense(
    val amount: Double,
    val atTime: Long,
)
