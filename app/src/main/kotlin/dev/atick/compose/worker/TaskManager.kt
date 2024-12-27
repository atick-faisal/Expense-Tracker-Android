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

import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.Flow

/**
 * Interface that provides methods to manage tasks.
 */
interface TaskManager {
    /**
     * Flow that emits a boolean value indicating whether the sync operation is in progress.
     */
    val isSyncing: Flow<Boolean>

    /**
     * Requests a sync operation.
     */
    @RequiresPermission(android.Manifest.permission.READ_SMS)
    fun requestSync()

    /**
     * Schedules a payment reminder for the specified merchant.
     *
     * @param merchantName The name of the merchant.
     * @param nextPaymentDate The next payment date.
     * @param reminderTime The reminder time.
     */
    fun schedulePaymentReminder(
        merchantName: String,
        nextPaymentDate: Long,
        reminderTime: Long,
    )

    /**
     * Schedules a cancellation reminder for the specified merchant.
     *
     * @param merchantName The name of the merchant.
     * @param nextPaymentDate The next payment date.
     * @param reminderTime The reminder time.
     */
    fun scheduleCancellationReminder(
        merchantName: String,
        nextPaymentDate: Long,
        reminderTime: Long,
    )

    /**
     * Shows a warning when the budget exceeds the specified amount.
     *
     * @param budgetAmount The budget amount.
     * @param currentAmount The current amount.
     */
    fun showBudgetExceedWarning(
        budgetAmount: Double,
        currentAmount: Double,
    )
}
