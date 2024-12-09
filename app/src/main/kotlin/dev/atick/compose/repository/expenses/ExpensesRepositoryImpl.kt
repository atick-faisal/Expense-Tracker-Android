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

package dev.atick.compose.repository.expenses

import dev.atick.core.utils.suspendRunCatching
import dev.atick.gemini.data.GeminiDataSource
import dev.atick.gemini.models.AiSMS
import dev.atick.sms.data.SMSDataSource
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val smsDataSource: SMSDataSource,
) : ExpensesRepository {
    override suspend fun syncExpensesFromSms(): Result<Unit> {
        return suspendRunCatching {
            val smsList = smsDataSource.querySMS(
                senderName = "QNB",
                keywords = listOf("purchase"),
            )

            Timber.d("Found ${smsList.size} SMSes")

            for ((i, sms) in smsList.withIndex()) {
                Timber.d("SMS $i$: $sms")

                val expense = geminiDataSource.getExpenseFromSMS(
                    AiSMS(
                        address = sms.address,
                        body = sms.body,
                        date = sms.date,
                    ),
                )

                delay(4000)

                if (i > 10) break

                Timber.d("Expense $i$: $expense")
            }
        }
    }
}
