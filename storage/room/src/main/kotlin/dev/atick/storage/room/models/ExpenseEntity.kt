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

package dev.atick.storage.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an expense in the database.
 *
 * @property id The unique identifier for the expense, auto-generated.
 * @property amount The amount of the expense.
 * @property currency The currency of the expense amount.
 * @property merchant The merchant associated with the expense.
 * @property category The category of the expense.
 * @property paymentStatus The payment status of the expense.
 * @property recurringType The type of recurring payment for the expense.
 * @property paymentDate The date of the payment in milliseconds since epoch.
 * @property dueDate The optional due date of the expense in milliseconds since epoch.
 * @property toBeCancelled A flag indicating whether the expense is to be cancelled.
 * @property nextRecurringDate The optional next recurring date in milliseconds since epoch.
 * @property createdAt The timestamp of when the expense was created, in milliseconds since epoch.
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val currency: String,
    val merchant: String,
    val category: String,
    val paymentStatus: String,
    val recurringType: String,
    val paymentDate: Long,
    val dueDate: Long? = null,
    val toBeCancelled: Boolean = false,
    val nextRecurringDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
