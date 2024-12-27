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

package dev.atick.storage.room.models

/**
 * Data class representing an analysis of expenses.
 *
 * @property categoryOrMerchant The category or merchant associated with the expenses.
 * @property spending The total spending amount.
 * @property currency The currency of the spending amount.
 * @property maxAmount The maximum amount spent in a single transaction.
 * @property minAmount The minimum amount spent in a single transaction.
 * @property percentage The optional percentage representation of the spending.
 */
data class ExpenseAnalysis(
    val categoryOrMerchant: String,
    val spending: Double,
    val currency: String,
    val maxAmount: Double,
    val minAmount: Double,
    val percentage: Double? = null,
)
