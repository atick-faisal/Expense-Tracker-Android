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

package dev.atick.compose.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.expenses.ExpensesScreenData
import dev.atick.compose.repository.expenses.ExpensesRepository
import dev.atick.core.ui.utils.OneTimeEvent
import dev.atick.core.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
) : ViewModel() {
    private val _expensesUiState = MutableStateFlow(UiState(ExpensesScreenData()))
    val expensesUiState = _expensesUiState.asStateFlow()

    init {
        expensesRepository.expenses
            .map { expenses ->
                ExpensesScreenData(expenses)
            }
            .map { expensesScreenData ->
                UiState(expensesScreenData)
            }
            .onEach { uiState ->
                _expensesUiState.value = uiState
            }
            .catch { e -> _expensesUiState.update { it.copy(error = OneTimeEvent(e)) } }
            .launchIn(viewModelScope)

//        _expensesUiState.updateWith(viewModelScope) {
//            expensesRepository.syncExpensesFromSms()
//        }
    }
}
