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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.compose.data.expenses.EditExpenseScreenData
import dev.atick.compose.data.expenses.UiCategoryType
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.core.ui.components.JetpackTextField
import dev.atick.core.ui.utils.StatefulComposable

@Composable
fun EditExpenseDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    editExpenseViewModel: EditExpenseViewModel = hiltViewModel(),
) {
    val expenseState by editExpenseViewModel.expenseUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        editExpenseViewModel.getExpense()
    }

    if (showDialog) {
        StatefulComposable(
            state = expenseState,
            onShowSnackbar = onShowSnackbar,
        ) { expense ->
            EditExpenseDialog(
                expense = expense,
                onAmountChange = editExpenseViewModel::setAmount,
                onCategoryChange = editExpenseViewModel::setCategory,
                onPaymentStatusChange = editExpenseViewModel::setPaymentStatus,
                onRecurringTypeChange = editExpenseViewModel::setRecurringType,
                onDescriptionChange = editExpenseViewModel::setDescription,
                onToBeCancelledChange = editExpenseViewModel::setToBeCancelled,
                onConfirm = {
                    editExpenseViewModel.saveExpense()
                    onDismissRequest()
                },
                onDismiss = onDismissRequest,
            )
        }
    }
}

@Composable
private fun EditExpenseDialog(
    expense: EditExpenseScreenData,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (UiCategoryType) -> Unit,
    onPaymentStatusChange: (UiPaymentStatus) -> Unit,
    onRecurringTypeChange: (UiRecurringType) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onToBeCancelledChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    var categoriesExpanded by remember { mutableStateOf(false) }
    var paymentStatusExpanded by remember { mutableStateOf(false) }
    var recurringTypeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(expense.navigateBack) {
        if (expense.navigateBack.getContentIfNotHandled() == true) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Amount
                JetpackTextField(
                    value = expense.amount.toString(),
                    onValueChange = onAmountChange,
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                )

                // Description
                JetpackTextField(
                    value = expense.merchant,
                    onValueChange = onDescriptionChange,
                    label = { Text(stringResource(R.string.merchant)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = stringResource(R.string.merchant),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Category
                ExposedDropdownMenuBox(
                    expanded = categoriesExpanded,
                    onExpandedChange = { categoriesExpanded = it },
                ) {
                    JetpackTextField(
                        value = expense.category.name,
                        onValueChange = { onCategoryChange(UiCategoryType.valueOf(it)) },
                        readOnly = true,
                        label = { Text(stringResource(R.string.expense_category)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = stringResource(R.string.expense_category),
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )

                    DropdownMenu(
                        expanded = categoriesExpanded,
                        onDismissRequest = { categoriesExpanded = false },
                    ) {
                        UiCategoryType.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategoryChange(category)
                                    categoriesExpanded = false
                                },
                            )
                        }
                    }
                }

                // Payment Status
                ExposedDropdownMenuBox(
                    expanded = paymentStatusExpanded,
                    onExpandedChange = { paymentStatusExpanded = it },
                ) {
                    JetpackTextField(
                        value = expense.paymentStatus.name,
                        onValueChange = { onPaymentStatusChange(UiPaymentStatus.valueOf(it)) },
                        readOnly = true,
                        label = { Text(stringResource(R.string.payment_status)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Payment,
                                contentDescription = stringResource(R.string.payment_status),
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )

                    DropdownMenu(
                        expanded = paymentStatusExpanded,
                        onDismissRequest = { paymentStatusExpanded = false },
                    ) {
                        UiPaymentStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    onPaymentStatusChange(status)
                                    paymentStatusExpanded = false
                                },
                            )
                        }
                    }
                }

                // Recurring Type
                ExposedDropdownMenuBox(
                    expanded = recurringTypeExpanded,
                    onExpandedChange = { recurringTypeExpanded = it },
                ) {
                    JetpackTextField(
                        value = expense.recurringType.name,
                        onValueChange = { onRecurringTypeChange(UiRecurringType.valueOf(it)) },
                        readOnly = true,
                        label = { Text(stringResource(R.string.recurring_type)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Repeat,
                                contentDescription = stringResource(R.string.recurring_type),
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )

                    DropdownMenu(
                        expanded = recurringTypeExpanded,
                        onDismissRequest = { recurringTypeExpanded = false },
                    ) {
                        UiRecurringType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    onRecurringTypeChange(type)
                                    recurringTypeExpanded = false
                                },
                            )
                        }
                    }
                }

                // To Be Cancelled
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = expense.toBeCancelled,
                        onCheckedChange = onToBeCancelledChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.error,
                            checkmarkColor = MaterialTheme.colorScheme.onError,
                        ),
                    )
                    Text(stringResource(R.string.to_be_cancelled))
                }
            }
        },
    )
}
