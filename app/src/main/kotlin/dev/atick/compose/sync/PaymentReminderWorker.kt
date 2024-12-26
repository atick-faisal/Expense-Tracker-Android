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
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.atick.core.extensions.showNotification
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class PaymentReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("PaymentReminderWorker: doWork()")
            val merchantName = inputData.getString(MERCHANT_NAME_KEY)!!
            val nextPaymentDate = inputData.getLong(
                key = NOTIFICATION_TIME_KEY,
                defaultValue = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000, // 3 days from now
            )
            val notification = context.paymentReminderNotification(
                merchantName = merchantName,
                nextPaymentDate = nextPaymentDate,
            )
            context.showNotification(
                notificationId = PAYMENT_REMINDER_NOTIFICATION_ID,
                notification = notification,
            )
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }
    }

    companion object {
        fun schedulePaymentReminder(
            merchantName: String,
            nextPaymentDate: Long,
            reminderTime: Long,
        ): OneTimeWorkRequest {
            val inputData = listOf(
                MERCHANT_NAME_KEY to merchantName,
                NOTIFICATION_TIME_KEY to nextPaymentDate,
            )
            return OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setInitialDelay(reminderTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .setInputData(PaymentReminderWorker::class.delegatedData(inputData))
                .build()
        }
    }
}
