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

package dev.atick.compose.ui.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.compose.repository.budgets.BudgetsRepository
import dev.atick.core.ui.utils.OneTimeEvent
import dev.atick.core.ui.utils.UiState
import dev.atick.core.ui.utils.updateState
import dev.atick.core.utils.MonthInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val budgetsRepository: BudgetsRepository,
) : ViewModel() {
    private val _budgetsUiState = MutableStateFlow(UiState(BudgetsScreenData()))
    val budgetsUiState = _budgetsUiState.asStateFlow()

    fun refreshBudgets(monthInfo: MonthInfo) {
        budgetsRepository.getCumulativeExpenses(monthInfo.startDate, monthInfo.endDate)
            .catch { e -> _budgetsUiState.update { it.copy(error = OneTimeEvent(e)) } }
            .onEach { cumulativeExpenses ->
                _budgetsUiState.updateState { copy(cumulativeExpenses = cumulativeExpenses) }
            }
            .launchIn(viewModelScope)
    }
}
