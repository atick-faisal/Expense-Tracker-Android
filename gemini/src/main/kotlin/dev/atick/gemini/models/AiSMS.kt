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

package dev.atick.gemini.models

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing an SMS.
 * @param address The sender's phone number.
 * @param body The body of the SMS.
 * @param date The date the SMS was sent.
 */
data class AiSMS(
    val address: String,
    val body: String,
    val date: Long,
) {
    /**
     * Function to get the text of the SMS.
     * @return The text of the SMS.
     */
    fun getTextSMS(): String {
        val dateObject = Date(date)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(dateObject)

        return "Sender: $address\nBody: $body\nDate: $formattedDate"
    }
}
