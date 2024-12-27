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

package dev.atick.compose.data.analysis

import dev.atick.storage.room.models.ExpenseAnalysis

/** Data class representing the analysis screen data.
 * @param categoryAnalyses The list of category analyses.
 * @param merchantAnalyses The list of merchant analyses.
 */
data class AnalysisScreenData(
    val categoryAnalyses: List<UiAnalysis> = emptyList(),
    val merchantAnalyses: List<UiAnalysis> = emptyList(),
)

/** Data class representing the UI analysis data.
 * @param categoryOrMerchant The category or merchant name.
 * @param spending The total spending in the category or merchant.
 * @param currency The currency of the spending.
 * @param maxAmount The maximum amount spent in the category or merchant.
 * @param minAmount The minimum amount spent in the category or merchant.
 * @param percentage The percentage of the total spending in the category or merchant.
 */
data class UiAnalysis(
    val categoryOrMerchant: String,
    val spending: Double,
    val currency: String,
    val maxAmount: Double,
    val minAmount: Double,
    val percentage: Double,
)

/** Converts an [ExpenseAnalysis] to a [UiAnalysis].
 * @return The [UiAnalysis] representation of the [ExpenseAnalysis].
 */
fun ExpenseAnalysis.toUiAnalysis(): UiAnalysis {
    return UiAnalysis(
        categoryOrMerchant = categoryOrMerchant,
        spending = spending,
        currency = currency,
        maxAmount = maxAmount,
        minAmount = minAmount,
        percentage = percentage ?: 0.0,
    )
}

/** Converts a list of [ExpenseAnalysis] to a list of [UiAnalysis].
 * @return The list of [UiAnalysis] representation of the list of [ExpenseAnalysis].
 */
fun List<ExpenseAnalysis>.toUiAnalyses(): List<UiAnalysis> {
    return map { it.toUiAnalysis() }
}
