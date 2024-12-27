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

import dev.atick.core.extensions.asFormattedDateTime
import dev.atick.core.ui.utils.OneTimeEvent

/**
 * Data class representing the edit expense screen data.
 * @param id The expense ID.
 * @param amount The expense amount.
 * @param currency The expense currency.
 * @param merchant The expense merchant.
 * @param category The expense category.
 * @param paymentStatus The expense payment status.
 * @param recurringType The expense recurring type.
 * @param paymentDate The expense payment date.
 * @param dueDate The expense due date.
 * @param toBeCancelled Whether the expense is to be cancelled.
 * @param formattedDate The formatted payment date.
 * @param navigateBack The event to navigate back.
 */
data class EditExpenseScreenData(
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
    val navigateBack: OneTimeEvent<Boolean> = OneTimeEvent(false),
)
