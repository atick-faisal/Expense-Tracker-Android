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
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.atick.compose.R
import dev.atick.compose.repository.expenses.ExpensesRepository
import dev.atick.core.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val expensesRepository: ExpensesRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        Timber.d("SyncWorker: getForegroundInfo")
        return context.syncForegroundInfo(R.string.sync_work_notification_title, 0, 0)
    }

    private fun getForegroundInfo(@StringRes title: Int, total: Int, current: Int): ForegroundInfo {
        return context.syncForegroundInfo(title, total, current)
    }

    @RequiresPermission(android.Manifest.permission.READ_SMS)
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            try {
                setForeground(getForegroundInfo())
                expensesRepository.syncExpensesFromSms()
                    .flowOn(ioDispatcher)
                    .collect { progress ->
                        Timber.d("SyncWorker: Progress: $progress")
                        setForeground(
                            foregroundInfo = getForegroundInfo(
                                title = R.string.sync_work_notification_title,
                                total = progress.total,
                                current = progress.current,
                            ),
                        )
                    }
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    companion object {
        @RequiresPermission(android.Manifest.permission.READ_SMS)
        fun startUpSyncWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(SyncConstraints)
                .setInputData(SyncWorker::class.delegatedData())
                .build()
        }
    }
}
