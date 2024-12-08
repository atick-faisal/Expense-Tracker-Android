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

package dev.atick.gemini.models

import kotlinx.serialization.Serializable

/**
 * Represents an expense in the AI system.
 *
 * @property amount The amount of the expense.
 * @property currency The currency of the expense.
 * @property category The category of the expense.
 * @property paymentStatus The payment status of the expense.
 * @property recurringType The recurring type of the expense.
 * @property paymentDate The payment date of the expense.
 * @property description The description of the expense.
 */
@Serializable
data class AiExpense(
    val amount: Double,
    val currency: AiCurrencyType,
    val category: AiExpenseCategory,
    val paymentStatus: AiPaymentStatus = AiPaymentStatus.PENDING,
    val recurringType: AiRecurringType = AiRecurringType.NONE,
    val paymentDate: String? = null,
    val description: String? = null,
)
