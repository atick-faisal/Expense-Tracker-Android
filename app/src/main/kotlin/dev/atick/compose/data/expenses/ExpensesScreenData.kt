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

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
    val formattedDate: String = paymentDate.asFormattedDate(),
)

enum class UiCurrencyType {
    QAR,
    USD,
    EUR,
    GBP,
    BDT,
}

enum class UiPaymentStatus {
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED,
}

enum class UiRecurringType {
    ONETIME,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}

enum class UiCategoryType {
    FOOD,
    ESSENTIAL,
    LIFESTYLE,
    TRANSPORTATION,
    HEALTHCARE,
    SAVINGS,
    DEBT,
    EDUCATION,
    CUSTOM,
}

fun Long.asFormattedDate(): String {
    val dateTime = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${
        dateTime.month.name
    } ${dateTime.dayOfMonth}, ${dateTime.year} at ${dateTime.hour}:${
        dateTime.minute.toString().padStart(2, '0')
    }"
}
