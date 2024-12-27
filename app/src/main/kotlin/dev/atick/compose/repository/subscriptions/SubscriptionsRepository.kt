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

package dev.atick.compose.repository.subscriptions

import dev.atick.compose.data.expenses.UiExpense
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching subscription data.
 */
interface SubscriptionsRepository {
    companion object {
        /**
         * The time before reminding user for cancellation.
         */
        const val CANCELLATION_REMINDER_TIME = 3 * 24 * 60 * 60 * 1000L
    }

    /**
     * Gets the subscriptions.
     *
     * @return A [Flow] of [List] of [UiExpense] representing the subscriptions.
     */
    fun getSubscriptions(): Flow<List<UiExpense>>

    /**
     * Sets the subscription to be cancelled.
     *
     * @param merchant The merchant for which the subscription is to be cancelled.
     * @param toBeCancelled The flag indicating if the subscription is to be cancelled.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun setCancellation(merchant: String, toBeCancelled: Boolean): Result<Unit>
}
