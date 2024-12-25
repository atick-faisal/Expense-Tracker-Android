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

package dev.atick.core.extensions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats a number (Float or Double) to a string with specified number of decimal places.
 * Handles special cases like NaN and Infinity.
 * Uses locale-specific decimal separator.
 * Removes trailing zeros after decimal point.
 *
 * @param nDecimal Number of decimal places (default is 2)
 * @return Formatted string representation of the number
 *
 * Examples:
 * 123.4567.format() -> "123.46"
 * 123.4f.format() -> "123.40"
 * 123.0.format() -> "123"
 * (-123.45).format() -> "-123.45"
 * Double.NaN.format() -> "NaN"
 * Float.POSITIVE_INFINITY.format() -> "∞"
 */
fun <T> T.format(nDecimal: Int = 2): String where T : Number, T : Comparable<T> {
    return when {
        // Handle special cases for both Float and Double
        this.toDouble().isNaN() -> "NaN"
        this.toDouble().isInfinite() -> if (this.toDouble() > 0) "∞" else "-∞"
        else -> {
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                // Ensure consistent decimal separator
                decimalSeparator = '.'
            }

            DecimalFormat("#,##0.#").apply {
                decimalFormatSymbols = symbols
                maximumFractionDigits = nDecimal
                minimumFractionDigits = 0 // Don't force decimal places if not needed
                isGroupingUsed = true // Use thousands separator
            }.format(this)
        }
    }
}