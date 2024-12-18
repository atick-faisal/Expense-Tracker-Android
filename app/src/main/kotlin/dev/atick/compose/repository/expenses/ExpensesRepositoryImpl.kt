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

import dev.atick.compose.data.categories.UiCategoryType
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.sync.SyncProgress
import dev.atick.gemini.data.GeminiDataSource
import dev.atick.gemini.models.AiSMS
import dev.atick.sms.data.SMSDataSource
import dev.atick.storage.room.data.ExpenseDataSource
import dev.atick.storage.room.models.ExpenseEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import timber.log.Timber
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val smsDataSource: SMSDataSource,
    private val expenseDataSource: ExpenseDataSource,
) : ExpensesRepository {
    override val expenses: Flow<List<UiExpense>>
        get() = expenseDataSource.getAllExpenses()
            .map { expenses ->
                expenses.map { expense ->
                    UiExpense(
                        id = expense.id,
                        amount = expense.amount,
                        category = UiCategoryType.valueOf(expense.categoryType),
                        paymentStatus = UiPaymentStatus.valueOf(expense.paymentStatus),
                        recurringType = UiRecurringType.valueOf(expense.recurringType),
                        paymentDate = expense.paymentDate,
                        dueDate = expense.dueDate,
                        description = expense.description,
                        toBeCancelled = expense.toBeCancelled,
                    )
                }
            }

    override fun syncExpensesFromSms() = flow<SyncProgress> {
        val smsList = smsDataSource.querySMS(
            senderName = "QNB",
            keywords = listOf("purchase"),
        )

        val totalSms = 10

        Timber.d("Found $totalSms SMSes")

        for ((i, sms) in smsList.withIndex()) {
            Timber.d("SMS $i$: $sms")

            val expense = geminiDataSource.getExpenseFromSMS(
                AiSMS(
                    address = sms.address,
                    body = sms.body,
                    date = sms.date,
                ),
            )

            val paymentDate = expense.paymentDate?.run {
                LocalDate.parse(this)
                    .atStartOfDayIn(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
            } ?: System.currentTimeMillis()

            expenseDataSource.insertExpense(
                ExpenseEntity(
                    amount = expense.amount,
                    paymentDate = paymentDate,
                    description = expense.description,
                    categoryType = expense.category.name,
                    paymentStatus = expense.paymentStatus.name,
                    recurringType = expense.recurringType.name,
                ),
            )

            delay(4000)

            if (i >= 10) break

            emit(
                SyncProgress(
                    total = totalSms,
                    current = i + 1,
                    message = "Syncing expenses... $i / $totalSms",
                ),
            )

            Timber.d("Expense $i$: $expense")
        }
    }
}
