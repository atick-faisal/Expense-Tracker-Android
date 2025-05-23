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
 * Data class representing a cumulative expense.
 *
 * @property amount The total amount of the cumulative expense.
 * @property atTime The timestamp of when the cumulative expense was recorded, in milliseconds since epoch.
 */
data class CumulativeExpense(
    val amount: Double,
    val atTime: Long,
)
