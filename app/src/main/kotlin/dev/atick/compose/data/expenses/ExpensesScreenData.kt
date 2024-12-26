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

data class ExpensesScreenData(
    val expenses: List<UiExpense> = emptyList(),
    val displayMonthYear: String = "",
)

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

enum class UiCurrencyType {
    QAR,
    USD,
    EUR,
    GBP,
    BDT,
}

enum class UiPaymentStatus(@StringRes val value: Int) {
    PENDING(R.string.payment_status_pending),
    PAID(R.string.payment_status_paid),
    OVERDUE(R.string.payment_status_overdue),
    CANCELLED(R.string.payment_status_cancelled),
}

enum class UiRecurringType(@StringRes val value: Int) {
    ONETIME(R.string.recurring_type_onetime),
    DAILY(R.string.recurring_type_daily),
    WEEKLY(R.string.recurring_type_weekly),
    MONTHLY(R.string.recurring_type_monthly),
    YEARLY(R.string.recurring_type_yearly),
}

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
