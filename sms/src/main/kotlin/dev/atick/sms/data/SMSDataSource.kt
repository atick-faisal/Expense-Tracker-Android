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

package dev.atick.sms.data

import androidx.annotation.RequiresPermission
import dev.atick.sms.models.SMSMessage

/**
 * Interface for a data source that provides SMS messages.
 */
interface SMSDataSource {
    /**
     * Queries SMS messages based on the provided parameters.
     * @param senderNames The names of the senders to filter by.
     * @param keywords The keywords to filter by.
     * @param ignoreWords The words to ignore.
     * @param startDate The start date to filter by.
     * @param endDate The end date to filter by.
     * @return The list of SMS messages that match the provided parameters.
     */
    @RequiresPermission(android.Manifest.permission.READ_SMS)
    suspend fun querySMS(
        senderNames: List<String>,
        keywords: List<String>? = null,
        ignoreWords: List<String>? = null,
        startDate: Long? = null,
        endDate: Long = System.currentTimeMillis(),
    ): List<SMSMessage>
}
