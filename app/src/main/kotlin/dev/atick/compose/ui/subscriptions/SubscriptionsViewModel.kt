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

package dev.atick.compose.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.data.subscriptions.SubscriptionsScreenData
import dev.atick.compose.repository.expenses.ExpensesRepository
import dev.atick.compose.repository.subscriptions.SubscriptionsRepository
import dev.atick.core.ui.utils.OneTimeEvent
import dev.atick.core.ui.utils.UiState
import dev.atick.core.ui.utils.updateState
import dev.atick.core.ui.utils.updateWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository,
    private val expensesRepository: ExpensesRepository,
) : ViewModel() {
    private val _subscriptionsUiState = MutableStateFlow(UiState(SubscriptionsScreenData()))
    val subscriptionsUiState = _subscriptionsUiState.asStateFlow()

    init {
        refreshSubscriptions()
    }

    fun refreshSubscriptions() {
        subscriptionsRepository.getSubscriptions()
            .onEach { subscriptions ->
                _subscriptionsUiState.updateState { copy(subscriptions = subscriptions) }
            }
            .catch { e -> _subscriptionsUiState.update { it.copy(error = OneTimeEvent(e)) } }
            .launchIn(viewModelScope)
    }

    fun setRecurringType(merchant: String, recurringType: UiRecurringType) {
        val newRecurringType = when (recurringType) {
            UiRecurringType.ONETIME -> UiRecurringType.DAILY
            UiRecurringType.DAILY -> UiRecurringType.WEEKLY
            UiRecurringType.WEEKLY -> UiRecurringType.MONTHLY
            UiRecurringType.MONTHLY -> UiRecurringType.YEARLY
            UiRecurringType.YEARLY -> UiRecurringType.ONETIME
        }

        _subscriptionsUiState.updateWith(viewModelScope) {
            expensesRepository.setRecurringType(merchant, newRecurringType)
        }
    }

    fun setCancellation(merchant: String, toBeCancelled: Boolean) {
        _subscriptionsUiState.updateWith(viewModelScope) {
            subscriptionsRepository.setCancellation(merchant, toBeCancelled)
        }
    }
}
