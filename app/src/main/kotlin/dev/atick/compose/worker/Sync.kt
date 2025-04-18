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
import androidx.annotation.RequiresPermission
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import timber.log.Timber

object Sync {
    /**
     * Initializes the sync process that keeps the app's data current.
     * This method should be called only once from the app module's Application.onCreate().
     *
     * @param context The application context.
     */
    @RequiresPermission(android.Manifest.permission.READ_SMS)
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            Timber.d("Sync: initialize")
            enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(),
            )
        }
    }
}

/**
 * The name of the sync worker.
 * This name is used to uniquely identify the sync worker.
 * It is used to enqueue the sync worker and to check if the sync worker is already running.
 */
internal const val SYNC_WORK_NAME = "SyncWorkName"
