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

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import dev.atick.compose.MainActivity
import dev.atick.compose.R
import dev.atick.core.extensions.asFormattedDateTime
import dev.atick.core.extensions.createNotification
import dev.atick.core.extensions.createNotificationChannel
import dev.atick.core.extensions.createProgressNotification

private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

const val PAYMENT_REMINDER_NOTIFICATION_ID = 1
const val MERCHANT_NAME_KEY = "MerchantName"
const val NOTIFICATION_TIME_KEY = "NotificationTime"
private const val PAYMENT_REMINDER_NOTIFICATION_CHANNEL_ID = "PaymentReminderNotificationChannel"

const val CANCELLATION_REMINDER_NOTIFICATION_ID = 2
private const val CANCELLATION_REMINDER_NOTIFICATION_CHANNEL_ID =
    "CancellationReminderNotificationChannel"

const val BUDGET_AMOUNT_KEY = "BudgetAmount"
const val CURRENT_AMOUNT_KEY = "CurrentAmount"
const val BUDGET_EXCEED_NOTIFICATION_ID = 3
private const val BUDGET_EXCEED_NOTIFICATION_CHANNEL_ID = "BudgetExceedNotificationChannel"

private const val MAIN_DEFAULT_INTENT_REQUEST_CODE = 0

/**
 * Constraints for sync work
 */
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 *
 * @param total The total number of items.
 * @param current The current number of items.
 * @return The foreground information for the sync work.
 */
fun Context.syncForegroundInfo(total: Int, current: Int) = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification(total, current),
)

/**
 * Progress notification for sync work
 *
 * @param total The total number of items.
 * @param current The current number of items.
 * @return The notification for the sync work.
 */
private fun Context.syncWorkNotification(total: Int, current: Int): Notification {
    createNotificationChannel(
        channelId = SYNC_NOTIFICATION_CHANNEL_ID,
        channelName = R.string.sync_work_notification_channel_name,
        channelDescription = R.string.sync_work_notification_channel_description,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
    )

    return createProgressNotification(
        channelId = SYNC_NOTIFICATION_CHANNEL_ID,
        title = R.string.sync_work_notification_title,
        total = total,
        current = current,
        icon = R.drawable.ic_ai_sms_sync,
        pendingIntent = getDefaultIntent(this),
    )
}

/**
 * Notification for payment reminder
 *
 * @param merchantName The name of the merchant.
 * @param nextPaymentDate The next payment date.
 * @return The notification for the payment reminder.
 */
fun Context.paymentReminderNotification(
    merchantName: String,
    nextPaymentDate: Long,
): Notification {
    val formattedDate = nextPaymentDate.asFormattedDateTime()

    createNotificationChannel(
        channelId = PAYMENT_REMINDER_NOTIFICATION_CHANNEL_ID,
        channelName = R.string.patnent_reminder_notification_channel_name,
        channelDescription = R.string.patnent_reminder_notification_channel_description,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
    )

    return createNotification(
        channelId = PAYMENT_REMINDER_NOTIFICATION_CHANNEL_ID,
        title = getString(R.string.payment_reminder_notification_title, merchantName),
        content = getString(R.string.payment_reminder_notification_content, formattedDate),
        icon = R.drawable.ic_upcoming,
        pendingIntent = getDefaultIntent(this),
    )
}

/**
 * Notification for cancellation reminder
 *
 * @param merchantName The name of the merchant.
 * @param nextPaymentDate The next payment date.
 * @return The notification for the cancellation reminder.
 */
fun Context.cancellationReminderNotification(
    merchantName: String,
    nextPaymentDate: Long,
): Notification {
    val formattedDate = nextPaymentDate.asFormattedDateTime()

    createNotificationChannel(
        channelId = CANCELLATION_REMINDER_NOTIFICATION_CHANNEL_ID,
        channelName = R.string.canellation_reminder_notification_channel_name,
        channelDescription = R.string.canellation_reminder_notification_channel_description,
        importance = NotificationManager.IMPORTANCE_HIGH,
    )

    return createNotification(
        channelId = CANCELLATION_REMINDER_NOTIFICATION_CHANNEL_ID,
        title = getString(R.string.cancellation_reminder_notification_title, merchantName),
        content = getString(R.string.cancellation_reminder_notification_content, formattedDate),
        icon = R.drawable.ic_hourglass_bottom,
        pendingIntent = getDefaultIntent(this),
    )
}

/**
 * Notification for budget exceed
 *
 * @param budgetAmount The budget amount.
 * @param currentAmount The current amount.
 * @return The notification for the budget exceed.
 */
fun Context.budgetExceedNotification(
    budgetAmount: Double,
    currentAmount: Double,
): Notification {
    createNotificationChannel(
        channelId = BUDGET_EXCEED_NOTIFICATION_CHANNEL_ID,
        channelName = R.string.budget_exceed_notification_channel_name,
        channelDescription = R.string.budget_exceed_notification_channel_description,
        importance = NotificationManager.IMPORTANCE_HIGH,
    )

    return createNotification(
        channelId = BUDGET_EXCEED_NOTIFICATION_CHANNEL_ID,
        title = getString(R.string.budget_exceed_notification_title),
        content = getString(
            R.string.budget_exceed_notification_content,
            budgetAmount,
            currentAmount,
        ),
        icon = R.drawable.ic_wallet,
        pendingIntent = getDefaultIntent(this),
    )
}

/**
 * Returns the default [MainActivity] intent for the notification
 *
 * @param context The application context.
 * @return The default intent for the notification.
 */
private fun getDefaultIntent(context: Context) = PendingIntent.getActivity(
    /* context = */ context,
    /* requestCode = */ MAIN_DEFAULT_INTENT_REQUEST_CODE,
    /* intent = */ Intent(context, MainActivity::class.java),
    /* flags = */ PendingIntent.FLAG_IMMUTABLE,
)
