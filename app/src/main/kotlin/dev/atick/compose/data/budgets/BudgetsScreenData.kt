package dev.atick.compose.data.budgets

import dev.atick.compose.data.categories.UiCategory

data class BudgetsScreenData(
    val budgets: List<UiBudget> = emptyList(),
)

data class UiBudget(
    val amount: Double,
    val category: UiCategory,
    val description: String? = null,
)