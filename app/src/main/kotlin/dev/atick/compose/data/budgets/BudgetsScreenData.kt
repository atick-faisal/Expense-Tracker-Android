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

data class BudgetsScreenData(
    val budgets: List<UiBudget> = emptyList(),
    val cumulativeExpenses: List<UiCumulativeExpense> = emptyList(),
    val totalBudget: Double = 0.0,
)

data class UiBudget(
    val id: Long = 0,
    val amount: Double,
    val categoryOrMerchantName: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isMerchant: Boolean = false,
)

data class UiCumulativeExpense(
    val amount: Double,
    val atTime: Long,
)