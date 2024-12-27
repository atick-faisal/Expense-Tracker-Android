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

package dev.atick.compose.data.expenses

import androidx.annotation.StringRes
import dev.atick.compose.R
import dev.atick.core.extensions.asFormattedDateTime
import dev.atick.storage.room.models.ExpenseEntity

/**
 * Data class representing the expenses screen data.
 * @param expenses The list of expenses.
 * @param displayMonthYear The display month and year.
 */
data class ExpensesScreenData(
    val expenses: List<UiExpense> = emptyList(),
    val displayMonthYear: String = "",
)

/**
 * Data class representing the UI expense data.
 * @param id The expense ID.
 * @param amount The expense amount.
 * @param currency The currency of the expense.
 * @param merchant The merchant name.
 * @param category The category of the expense.
 * @param paymentStatus The payment status of the expense.
 * @param recurringType The recurring type of the expense.
 * @param paymentDate The payment date of the expense.
 * @param dueDate The due date of the expense.
 * @param toBeCancelled Whether the expense is to be cancelled.
 * @param formattedDate The formatted payment date.
 */
data class UiExpense(
    val id: Long = 0,
    val amount: Double = 0.0,
    val currency: UiCurrencyType = UiCurrencyType.QAR,
    val merchant: String = "Unknown",
    val category: UiCategoryType = UiCategoryType.ESSENTIAL,
    val paymentStatus: UiPaymentStatus = UiPaymentStatus.PENDING,
    val recurringType: UiRecurringType = UiRecurringType.ONETIME,
    val paymentDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val toBeCancelled: Boolean = false,
    val formattedDate: String = paymentDate.asFormattedDateTime(),
)

/**
 * UI currency type.
 */
enum class UiCurrencyType {
    QAR,
    USD,
    EUR,
    GBP,
    BDT,
}

/**
 * UI payment status.
 */
enum class UiPaymentStatus(@StringRes val value: Int) {
    PENDING(R.string.payment_status_pending),
    PAID(R.string.payment_status_paid),
    OVERDUE(R.string.payment_status_overdue),
    CANCELLED(R.string.payment_status_cancelled),
}

/**
 * UI recurring type.
 */
enum class UiRecurringType(@StringRes val value: Int) {
    ONETIME(R.string.recurring_type_onetime),
    DAILY(R.string.recurring_type_daily),
    WEEKLY(R.string.recurring_type_weekly),
    MONTHLY(R.string.recurring_type_monthly),
    YEARLY(R.string.recurring_type_yearly),
}

/**
 * UI category type.
 */
enum class UiCategoryType(@StringRes val value: Int) {
    FOOD(R.string.category_food),
    ESSENTIAL(R.string.category_essential),
    LIFESTYLE(R.string.category_lifestyle),
    TRANSPORTATION(R.string.category_transportation),
    HEALTHCARE(R.string.category_healthcare),
    SAVINGS(R.string.category_savings),
    DEBT(R.string.category_debt),
    EDUCATION(R.string.category_education),
    OTHERS(R.string.category_others),
}

/**
 * Converts a [ExpenseEntity] to a [UiExpense].
 */
fun UiExpense.toEditableExpense(): EditExpenseScreenData {
    return EditExpenseScreenData(
        id = id,
        amount = amount,
        currency = currency,
        merchant = merchant,
        category = category,
        paymentStatus = paymentStatus,
        recurringType = recurringType,
        paymentDate = paymentDate,
        dueDate = dueDate,
        toBeCancelled = toBeCancelled,
        formattedDate = formattedDate,
    )
}

/**
 * Converts a [ExpenseEntity] to a [UiExpense].
 */
fun UiExpense.toExpenseEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        currency = currency.name,
        merchant = merchant,
        category = category.name,
        paymentStatus = paymentStatus.name,
        recurringType = recurringType.name,
        paymentDate = paymentDate,
        dueDate = dueDate,
        toBeCancelled = toBeCancelled,
    )
}

/**
 * Converts a [ExpenseEntity] to a [UiExpense].
 */
fun ExpenseEntity.toUiExpense(): UiExpense {
    return UiExpense(
        id = id,
        amount = amount,
        currency = UiCurrencyType.valueOf(currency),
        merchant = merchant,
        category = UiCategoryType.valueOf(category),
        paymentStatus = UiPaymentStatus.valueOf(paymentStatus),
        recurringType = UiRecurringType.valueOf(recurringType),
        paymentDate = paymentDate,
        dueDate = dueDate,
        toBeCancelled = toBeCancelled,
        formattedDate = paymentDate.asFormattedDateTime(),
    )
}

/**
 * Converts a list of [ExpenseEntity] to a list of [UiExpense].
 */
fun List<ExpenseEntity>.toUiExpenses(): List<UiExpense> {
    return map(ExpenseEntity::toUiExpense)
}
