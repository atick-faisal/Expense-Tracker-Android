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

package dev.atick.compose.repository.expenses

import androidx.annotation.RequiresPermission
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.sync.SyncProgress
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    companion object {
        const val SYNC_SMS_DURATION = 30 * 24 * 60 * 60 * 1000L // 30 days
        val BANK_NAMES = listOf("QNB", "QIB", "CBQ", "Doha Bank")
        val KEYWORDS = listOf("purchase", "transaction")
        val IGNORE_WORDS = listOf("OTP", "withdrawal", "deposit", "bonus", "refund")

        const val RECURRING_DAILY = 24 * 60 * 60 * 1000L
        const val RECURRING_WEEKLY = 7 * 24 * 60 * 60 * 1000L
        const val RECURRING_MONTHLY = 30 * 24 * 60 * 60 * 1000L
        const val RECURRING_YEARLY = 365 * 24 * 60 * 60 * 1000L

        const val REMINDER_TIME_BEFORE_PAYMENT = 24 * 60 * 60 * 1000L
    }

    val isSyncing: Flow<Boolean>

    fun getAllExpenses(startDate: Long, endDate: Long): Flow<List<UiExpense>>
    fun getExpenseById(id: Long): Flow<UiExpense>
    suspend fun updateExpense(expense: UiExpense): Result<Unit>
    suspend fun deleteExpense(expense: UiExpense): Result<Unit>

    @RequiresPermission(android.Manifest.permission.READ_SMS)
    fun requestSync(): Result<Unit>

    @RequiresPermission(android.Manifest.permission.READ_SMS)
    fun syncExpensesFromSms(): Flow<SyncProgress>
    suspend fun setRecurringType(merchant: String, recurringType: UiRecurringType): Result<Unit>
}
