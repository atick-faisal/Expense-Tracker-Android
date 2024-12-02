package dev.atick.compose.data.expenses

import dev.atick.compose.data.categories.UiCategory

data class ExpensesScreenData(
    val expenses: List<UiExpense> = emptyList(),
)

data class UiExpense(
    val amount: Double,
    val category: UiCategory,
    val paymentStatus: UiPaymentStatus = UiPaymentStatus.PENDING,
    val recurringType: UiRecurringType = UiRecurringType.NONE,
    val paymentDate: String? = null,
    val dueDate: String? = null,
    val description: String? = null,
)

enum class UiPaymentStatus {
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED
}

enum class UiRecurringType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}