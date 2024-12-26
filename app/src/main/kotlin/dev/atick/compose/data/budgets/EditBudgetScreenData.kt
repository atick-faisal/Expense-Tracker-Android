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

import dev.atick.core.ui.utils.OneTimeEvent
import dev.atick.core.utils.getMonthInfoAt

data class EditBudgetScreenData(
    val month: Long = getMonthInfoAt(0).startDate, // The timestamp of the start date of the month
    val amount: Double? = null,
    val navigateBack: OneTimeEvent<Boolean> = OneTimeEvent(false),
) {
    val formattedMonth: String
        get() {
            val monthInfo = getMonthInfoAt(0)
            return "${monthInfo.monthName} ${monthInfo.year}"
        }
}
