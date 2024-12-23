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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.compose.ui.components.BudgetChart
import dev.atick.core.ui.utils.StatefulComposable
import dev.atick.core.utils.MonthInfo

@Composable
internal fun BudgetsRoute(
    monthInfo: MonthInfo,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    budgetsViewModel: BudgetsViewModel = hiltViewModel(),
) {
    val budgetsUiState by budgetsViewModel.budgetsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(monthInfo) {
        budgetsViewModel.refreshBudgets(monthInfo)
    }

    StatefulComposable(
        state = budgetsUiState,
        onShowSnackbar = onShowSnackbar,
    ) { budgetsScreenData ->
        BudgetsScreen(budgetsScreenData)
    }
}

@Composable
private fun BudgetsScreen(
    budgetsScreenData: BudgetsScreenData,
) {
    BudgetChart(budgetsScreenData)
}
