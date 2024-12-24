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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.compose.data.budgets.EditBudgetScreenData
import dev.atick.core.ui.components.JetpackButton
import dev.atick.core.ui.components.JetpackOutlinedButton
import dev.atick.core.ui.components.JetpackTextField
import dev.atick.core.ui.utils.StatefulComposable
import dev.atick.core.utils.MonthInfo

@Composable
internal fun EditBudgetRoute(
    monthInfo: MonthInfo,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    editBudgetViewModel: EditBudgetViewModel = hiltViewModel(),
) {
    val editBudgetState by editBudgetViewModel.editBudgetUiState.collectAsStateWithLifecycle()

    LaunchedEffect(monthInfo) {
        editBudgetViewModel.refreshBudget(monthInfo)
    }

    StatefulComposable(
        state = editBudgetState,
        onShowSnackbar = onShowSnackbar,
    ) { budgetData ->
        EditBudgetScreen(
            budget = budgetData,
            onAmountChange = { editBudgetViewModel.updateAmount(it) },
            monthInfo = monthInfo,
            onSaveClick = editBudgetViewModel::saveBudget,
            onCancelClick = onBackClick,
        )
    }
}

@Composable
private fun EditBudgetScreen(
    budget: EditBudgetScreenData,
    onAmountChange: (String) -> Unit,
    monthInfo: MonthInfo,
    onSaveClick: (MonthInfo) -> Unit,
    onCancelClick: () -> Unit,
) {
    LaunchedEffect(budget.navigateBack) {
        if (budget.navigateBack.getContentIfNotHandled() == true) {
            onCancelClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.edit_budget),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "${monthInfo.monthName}, ${monthInfo.year}",
            style = MaterialTheme.typography.titleSmall,
        )
        JetpackTextField(
            value = budget.amount.toString(),
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.budget)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            JetpackButton(
                onClick = { onSaveClick.invoke(monthInfo) },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.save))
            }
            JetpackOutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
