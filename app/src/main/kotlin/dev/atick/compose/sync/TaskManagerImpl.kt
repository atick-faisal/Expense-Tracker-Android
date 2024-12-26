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
import androidx.annotation.RequiresPermission
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * [TaskManager] backed by [WorkInfo] from [WorkManager]
 */
class TaskManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : TaskManager {
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    @RequiresPermission(android.Manifest.permission.READ_SMS)
    override fun requestSync() {
        Sync.initialize(context)
    }

    override fun schedulePaymentReminder(
        merchantName: String,
        nextPaymentDate: Long,
        reminderTime: Long,
    ) {
        Timber.d("Schedule payment reminder for $merchantName on $nextPaymentDate at $reminderTime")
        Reminders.schedulePaymentReminder(
            context = context,
            merchantName = merchantName,
            nextPaymentDate = nextPaymentDate,
            reminderTime = reminderTime,
        )
    }

    override fun scheduleCancellationReminder(
        merchantName: String,
        nextPaymentDate: Long,
        reminderTime: Long,
    ) {
        Timber.d("Schedule cancellation reminder for $merchantName on $nextPaymentDate at $reminderTime")
        Reminders.scheduleCancellationReminder(
            context = context,
            merchantName = merchantName,
            nextPaymentDate = nextPaymentDate,
            reminderTime = reminderTime,
        )
    }

    override fun showBudgetExceedWarning(
        budgetAmount: Double,
        currentAmount: Double,
    ) {
        Timber.d("Show budget exceed warning for $budgetAmount with $currentAmount")
        Reminders.showBudgetExceedWarning(
            context = context,
            budgetAmount = budgetAmount,
            currentAmount = currentAmount,
        )
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == State.RUNNING }
