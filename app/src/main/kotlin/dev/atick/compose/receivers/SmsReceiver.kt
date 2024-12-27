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

package dev.atick.compose.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.repository.expenses.ExpensesRepository
import dev.atick.compose.worker.TaskManager
import timber.log.Timber
import javax.inject.Inject

/**
 * A broadcast receiver that listens for incoming SMS messages.
 */
@AndroidEntryPoint
class SmsReceiver @Inject constructor() : BroadcastReceiver() {

    @Inject
    lateinit var taskManager: TaskManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach { sms ->
                    // Check if the SMS is from a bank
                    if (ExpensesRepository.BANK_NAMES.any { sms.originatingAddress?.contains(it) == true }) {
                        Timber.d("SMS Received: ${sms.originatingAddress} - ${sms.messageBody}")
                        @SuppressLint("MissingPermission")
                        // Request a sync to update the expenses
                        taskManager.requestSync()
                    }
                }
            }
        }
    }
}
