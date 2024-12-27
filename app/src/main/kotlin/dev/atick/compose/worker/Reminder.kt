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

package dev.atick.compose.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager

/**
 * Object that schedules reminders for payment, cancellation, and budget exceed warnings.
 */
object Reminders {
    /**
     * Schedules a payment reminder for the given merchant name, next payment date, and reminder time.
     *
     * @param context The application context.
     * @param merchantName The name of the merchant.
     * @param nextPaymentDate The next payment date.
     * @param reminderTime The reminder time.
     */
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

    /**
     * Schedules a cancellation reminder for the given merchant name, next payment date, and reminder time.
     *
     * @param context The application context.
     * @param merchantName The name of the merchant.
     * @param nextPaymentDate The next payment date.
     * @param reminderTime The reminder time.
     */
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

    /**
     * Shows a budget exceed warning for the given budget amount and current amount.
     *
     * @param context The application context.
     * @param budgetAmount The budget amount.
     * @param currentAmount The current amount.
     */
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

/**
 * The name of the payment reminder work.
 */
internal const val PAYMENT_REMINDER_WORK_NAME = "PaymentReminderWorkName"

/**
 * The name of the cancellation reminder work.
 */
internal const val CANCELLATION_REMINDER_WORK_NAME = "CancellationReminderWorkName"

/**
 * The name of the budget exceed reminder work.
 */
internal const val BUDGET_REMINDER_WORK_NAME = "BudgetReminderWorkName"
