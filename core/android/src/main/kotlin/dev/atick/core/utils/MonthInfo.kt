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

package dev.atick.core.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class MonthInfo(
    val monthName: String,
    val year: Int,
    val startDate: Long,
    val endDate: Long,
)

fun getMonthInfoAt(monthOffset: Int = 0): MonthInfo {
    val offsetDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date.plus(monthOffset, DateTimeUnit.MONTH)

    val startOfMonth = LocalDateTime(
        year = offsetDate.year,
        month = offsetDate.month,
        dayOfMonth = 1,
        hour = 0,
        minute = 0,
        second = 0,
        nanosecond = 0,
    )

    val startOfMonthMillis = startOfMonth
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()

    val endOfMonthMillis = startOfMonth
        .date.plus(1, DateTimeUnit.MONTH)
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()

    return MonthInfo(
        monthName = startOfMonth.month.name,
        year = startOfMonth.year,
        startDate = startOfMonthMillis,
        endDate = endOfMonthMillis,
    )
}
