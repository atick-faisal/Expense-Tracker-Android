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

package dev.atick.gemini.utils

import dev.atick.gemini.models.AiExpense
import kotlinx.serialization.json.Json

/**
 * Utility class to parse JSON strings into [AiExpense] objects.
 */
object ExpenseParser {
    private val json = Json {
        ignoreUnknownKeys = true // Ignore JSON fields that don't match our data class
        coerceInputValues = true // Try to coerce values to the correct type when possible
        encodeDefaults = true // Include default values in JSON output
    }

    /**
     * Parses a JSON string into an [AiExpense] object.
     *
     * @param jsonString The JSON string to parse.
     * @return The parsed [AiExpense] object.
     */
    fun parseExpense(jsonString: String): AiExpense {
        return json.decodeFromString<AiExpense>(jsonString)
    }
}
