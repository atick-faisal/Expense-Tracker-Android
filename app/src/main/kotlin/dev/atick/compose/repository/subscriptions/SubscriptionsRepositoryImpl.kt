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

package dev.atick.compose.repository.subscriptions

import dev.atick.compose.data.expenses.UiCategoryType
import dev.atick.compose.data.expenses.UiCurrencyType
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.worker.TaskManager
import dev.atick.core.utils.suspendRunCatching
import dev.atick.storage.room.data.ExpenseDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [SubscriptionsRepository] that fetches data from the [ExpenseDataSource].
 *
 * @param expenseDataSource The data source for expense data.
 * @param taskManager The task manager for scheduling tasks.
 */
class SubscriptionsRepositoryImpl @Inject constructor(
    private val expenseDataSource: ExpenseDataSource,
    private val taskManager: TaskManager,
) : SubscriptionsRepository {

    /**
     * Gets the subscriptions.
     *
     * @return A [Flow] of [List] of [UiExpense] representing the subscriptions.
     */
    override fun getSubscriptions(): Flow<List<UiExpense>> {
        return expenseDataSource.getRecurringExpenses()
            .map { expenses ->
                expenses.map { expense ->
                    UiExpense(
                        id = expense.id,
                        amount = expense.amount,
                        currency = UiCurrencyType.valueOf(expense.currency),
                        merchant = expense.merchant,
                        category = UiCategoryType.valueOf(expense.category),
                        paymentStatus = UiPaymentStatus.valueOf(expense.paymentStatus),
                        recurringType = UiRecurringType.valueOf(expense.recurringType),
                        paymentDate = expense.paymentDate,
                        dueDate = expense.dueDate,
                        toBeCancelled = expense.toBeCancelled,
                    )
                }
            }
    }

    /**
     * Sets the subscription to be cancelled.
     *
     * @param merchant The merchant for which the subscription is to be cancelled.
     * @param toBeCancelled The flag indicating if the subscription is to be cancelled.
     * @return A [Result] indicating the success or failure of the operation.
     */
    override suspend fun setCancellation(
        merchant: String,
        toBeCancelled: Boolean,
    ): Result<Unit> {
        return suspendRunCatching {
            expenseDataSource.setCancellation(merchant, toBeCancelled)

            // Schedule cancellation reminder if the subscription to be cancelled
            val nextPaymentDate = expenseDataSource.getNextPaymentDate(merchant)
            if (toBeCancelled && nextPaymentDate != null) {
                taskManager.scheduleCancellationReminder(
                    merchantName = merchant,
                    nextPaymentDate = nextPaymentDate,
                    reminderTime = nextPaymentDate - SubscriptionsRepository.CANCELLATION_REMINDER_TIME,
                )
            }
        }
    }
}
