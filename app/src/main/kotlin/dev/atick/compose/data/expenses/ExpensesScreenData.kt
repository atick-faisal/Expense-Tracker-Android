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

import dev.atick.compose.data.categories.UiCategoryType
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents

data class ExpensesScreenData(
    val expenses: List<UiExpense> = emptyList(),
)

data class UiExpense(
    val id: Long = 0,
    val amount: Double = 0.0,
    val category: UiCategoryType = UiCategoryType.ESSENTIAL,
    val paymentStatus: UiPaymentStatus = UiPaymentStatus.PENDING,
    val recurringType: UiRecurringType = UiRecurringType.NONE,
    val paymentDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val description: String? = null,
    val toBeCancelled: Boolean = false,
)

enum class UiPaymentStatus {
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED,
}

enum class UiRecurringType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}

fun Long.asFormattedDate(): String {
    return Instant.fromEpochMilliseconds(this)
        .format(DateTimeComponents.Formats.RFC_1123)
}