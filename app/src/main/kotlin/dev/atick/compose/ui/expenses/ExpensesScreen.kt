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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.expenses.ExpensesScreenData
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.ui.components.ExpenseCard
import dev.atick.core.ui.utils.StatefulComposable
import dev.atick.core.utils.MonthInfo

@Composable
internal fun ExpensesRoute(
    monthInfo: MonthInfo,
    onExpenseClick: (Long) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    expensesViewModel: ExpensesViewModel = hiltViewModel(),
) {
    val expensesState by expensesViewModel.expensesUiState.collectAsStateWithLifecycle()

    LaunchedEffect(monthInfo) {
        expensesViewModel.refreshExpenses(monthInfo)
    }

    StatefulComposable(
        state = expensesState,
        onShowSnackbar = onShowSnackbar,
    ) { expensesScreenData ->
        ExpensesScreen(
            expensesScreenData = expensesScreenData,
            monthInfo = monthInfo,
//            onNextMonthClick = expensesViewModel::incrementMonth,
//            onPreviousMonthClick = expensesViewModel::decrementMonth,
            onExpenseClick = onExpenseClick,
            onRecurringTypeClick = expensesViewModel::setRecurringType,
        )
    }
}

@Composable
private fun ExpensesScreen(
    expensesScreenData: ExpensesScreenData,
    monthInfo: MonthInfo,
//    onNextMonthClick: () -> Unit,
//    onPreviousMonthClick: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    onRecurringTypeClick: (String, UiRecurringType) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(expensesScreenData.expenses, key = { it.id }) { expense ->
            ExpenseCard(
                expense = expense,
                onExpenseClick = onExpenseClick,
                onRecurringTypeClick = onRecurringTypeClick,
                // modifier = Modifier.animateItem(),
            )
        }
    }
}
