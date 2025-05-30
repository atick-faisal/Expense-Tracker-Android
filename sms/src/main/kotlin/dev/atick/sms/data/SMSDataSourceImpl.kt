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

import android.content.ContentResolver
import android.provider.Telephony
import androidx.annotation.RequiresPermission
import dev.atick.core.di.IoDispatcher
import dev.atick.sms.models.SMSMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [SMSDataSource] that queries SMS messages from the device.
 */
class SMSDataSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SMSDataSource {
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
    override suspend fun querySMS(
        senderNames: List<String>,
        keywords: List<String>?,
        ignoreWords: List<String>?,
        startDate: Long?,
        endDate: Long,
    ): List<SMSMessage> {
        val selection = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        // Add sender names to selection
        if (senderNames.isNotEmpty()) {
            val senderConditions = senderNames.map {
                "${Telephony.Sms.ADDRESS} LIKE ?"
            }.joinToString(" OR ")

            selection.add("($senderConditions)")
            selectionArgs.addAll(senderNames.map { "%$it%" })
        }

        // Add keywords to selection
        keywords?.let { keywordList ->
            if (keywordList.isNotEmpty()) {
                val keywordConditions = keywordList.map {
                    "${Telephony.Sms.BODY} LIKE ?"
                }.joinToString(" OR ")

                selection.add("($keywordConditions)")
                selectionArgs.addAll(keywordList.map { "%$it%" })
            }
        }

        // Add ignore words to selection
        ignoreWords?.let { words ->
            if (words.isNotEmpty()) {
                val ignoreConditions = words.map {
                    "${Telephony.Sms.BODY} NOT LIKE ?"
                }.joinToString(" AND ")

                selection.add("($ignoreConditions)")
                selectionArgs.addAll(words.map { "%$it%" })
            }
        }

        // Add start date if provided
        startDate?.let {
            selection.add("${Telephony.Sms.DATE} BETWEEN ? AND ?")
            selectionArgs.add(startDate.toString())
            selectionArgs.add(endDate.toString())
        }

        Timber.d("START DATE: $startDate, END DATE: $endDate")

        return querySMS(
            selection.joinToString(" AND "),
            selectionArgs.toTypedArray(),
        )
    }

    /**
     * Queries SMS messages based on the provided selection and selection arguments.
     * @param selection The selection to filter by.
     * @param selectionArgs The selection arguments.
     * @return The list of SMS messages that match the provided selection.
     */
    private suspend fun querySMS(
        selection: String,
        selectionArgs: Array<String>,
    ): List<SMSMessage> = withContext(ioDispatcher) {
        val smsList = mutableListOf<SMSMessage>()

        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE,
        )

        contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${Telephony.Sms.DATE} ASC", // get the oldest messages first
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                Timber.d("SMS DATE: $date")

                smsList.add(SMSMessage(id, address, body, date, type))
            }
        }

        smsList
    }
}
