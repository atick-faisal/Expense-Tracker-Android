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

package dev.atick.compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.atick.compose.R
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.data.expenses.asFormattedDate

@Composable
fun ExpenseDetailsDialog(
    expense: UiExpense,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.fillMaxWidth(),
        title = { Text("Expense Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val currencyFormatter = rememberCurrencyFormatter(expense.currency)

                ListItem(
                    headlineContent = { Text(currencyFormatter.format(expense.amount)) },
                    leadingContent = { Icon(Icons.Default.Money, null) },
                    overlineContent = { Text(stringResource(R.string.amount)) },
                )

                ListItem(
                    headlineContent = { Text(expense.merchant) },
                    leadingContent = { Icon(Icons.Default.Store, null) },
                    overlineContent = { Text("Merchant") },
                )

                ListItem(
                    headlineContent = { CategoryTypeChip(expense.category) },
                    leadingContent = { Icon(Icons.Default.Category, null) },
                    overlineContent = { Text("Category") },
                )

                ListItem(
                    headlineContent = { PaymentStatusChip(expense.paymentStatus) },
                    leadingContent = { Icon(Icons.Default.Payment, null) },
                    overlineContent = { Text("Status") },
                )

                if (expense.recurringType != UiRecurringType.ONETIME) {
                    ListItem(
                        headlineContent = { RecurringTypeChip(expense) },
                        leadingContent = { Icon(Icons.Default.Repeat, null) },
                        overlineContent = { Text("Recurring") },
                    )
                }

                ListItem(
                    headlineContent = { Text(expense.formattedDate) },
                    leadingContent = { Icon(Icons.Default.DateRange, null) },
                    overlineContent = { Text("Payment Date") },
                )

                expense.dueDate?.let { dueDate ->
                    ListItem(
                        headlineContent = { Text(dueDate.asFormattedDate()) },
                        leadingContent = { Icon(Icons.Default.Schedule, null) },
                        overlineContent = { Text(stringResource(R.string.due_date)) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}
