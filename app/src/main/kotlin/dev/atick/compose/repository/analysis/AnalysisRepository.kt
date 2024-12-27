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

package dev.atick.compose.repository.analysis

import dev.atick.compose.data.analysis.UiAnalysis
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching analysis data.
 */
interface AnalysisRepository {
    /**
     * Gets the category analyses.
     *
     * @param startDate The start date of the analysis.
     * @param endDate The end date of the analysis.
     * @param topN The number of top categories to fetch.
     * @return A [Flow] of [List] of [UiAnalysis] representing the category analyses.
     */
    fun getCategoryAnalyses(startDate: Long, endDate: Long, topN: Int): Flow<List<UiAnalysis>>

    /**
     * Gets the merchant analyses.
     *
     * @param startDate The start date of the analysis.
     * @param endDate The end date of the analysis.
     * @param topN The number of top merchants to fetch.
     * @return A [Flow] of [List] of [UiAnalysis] representing the merchant analyses.
     */
    fun getMerchantAnalyses(startDate: Long, endDate: Long, topN: Int): Flow<List<UiAnalysis>>

    /**
     * Gets the total spending.
     *
     * @param startDate The start date of the analysis.
     * @param endDate The end date of the analysis.
     * @return A [Flow] of [Double] representing the total spending.
     */
    fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double>
}
