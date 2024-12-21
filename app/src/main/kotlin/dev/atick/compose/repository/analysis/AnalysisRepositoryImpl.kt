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
import dev.atick.storage.room.data.AnalysisDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class AnalysisRepositoryImpl @Inject constructor(
    private val analysisDataSource: AnalysisDataSource,
) : AnalysisRepository {
    override fun getCategoryAnalyses(
        startDate: Long,
        endDate: Long,
        topN: Int,
    ): Flow<List<UiAnalysis>> {
        return analysisDataSource.getCategoryAnalyses(startDate, endDate, topN)
            .map { analyses ->
                analyses.map {
                    UiAnalysis(
                        categoryOrMerchant = it.categoryOrMerchant,
                        spending = it.spending,
                        currency = it.currency,
                        percentage = it.percentage ?: 0.0,
                    )
                }
            }
    }

    override fun getMerchantAnalyses(
        startDate: Long,
        endDate: Long,
        topN: Int,
    ): Flow<List<UiAnalysis>> {
        return analysisDataSource.getMerchantAnalyses(startDate, endDate, topN)
            .map { analyses ->
                analyses.map {
                    UiAnalysis(
                        categoryOrMerchant = it.categoryOrMerchant,
                        spending = it.spending,
                        currency = it.currency,
                        percentage = it.percentage ?: 0.0,
                    )
                }
            }
    }

    override fun getTotalSpending(
        startDate: Long,
        endDate: Long,
    ): Flow<Double> {
        return analysisDataSource.getTotalSpending(startDate, endDate)
    }
}
