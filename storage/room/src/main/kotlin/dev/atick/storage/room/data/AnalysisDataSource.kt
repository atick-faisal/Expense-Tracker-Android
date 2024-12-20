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

package dev.atick.storage.room.data

import dev.atick.storage.room.models.ExpenseAnalysis
import kotlinx.coroutines.flow.Flow

interface AnalysisDataSource {
    fun getAnalysis(startDate: Long, endDate: Long): Flow<List<ExpenseAnalysis>>
    fun getCategoryAnalysis(
        categoryType: String,
        startDate: Long,
        endDate: Long,
    ): Flow<ExpenseAnalysis?>

    fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double>
    fun getTotalBudget(startDate: Long, endDate: Long): Flow<Double>
}