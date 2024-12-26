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

package dev.atick.compose.sync

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager

object Reminders {
    fun schedulePaymentReminder(
        context: Context,
        merchantName: String,
        nextPaymentDate: Long,
        reminderTime: Long,
    ) {
        if (nextPaymentDate < System.currentTimeMillis()) return
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                PAYMENT_REMINDER_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                PaymentReminderWorker.schedulePaymentReminder(
                    merchantName,
                    nextPaymentDate,
                    reminderTime,
                ),
            )
        }
    }

    fun scheduleCancellationReminder(
        context: Context,
        merchantName: String,
        nextPaymentDate: Long,
        reminderTime: Long,
    ) {
        if (nextPaymentDate < System.currentTimeMillis()) return
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                CANCELLATION_REMINDER_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                CancellationReminderWorker.scheduleCancellationReminder(
                    merchantName,
                    nextPaymentDate,
                    reminderTime,
                ),
            )
        }
    }

    fun showBudgetExceedWarning(
        context: Context,
        budgetAmount: Double,
        currentAmount: Double,
    ) {
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                BUDGET_REMINDER_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                BudgetReminderWorker.showBudgetExceedWarning(
                    budgetAmount,
                    currentAmount,
                ),
            )
        }
    }
}

internal const val PAYMENT_REMINDER_WORK_NAME = "PaymentReminderWorkName"
internal const val CANCELLATION_REMINDER_WORK_NAME = "CancellationReminderWorkName"
internal const val BUDGET_REMINDER_WORK_NAME = "BudgetReminderWorkName"
