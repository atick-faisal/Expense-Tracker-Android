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

import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.categories.UiCategoryType
import dev.atick.compose.data.expenses.ExpensesScreenData
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.core.ui.utils.StatefulComposable
import java.util.Locale

@Composable
internal fun ExpensesRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    expensesViewModel: ExpensesViewModel = hiltViewModel(),
) {
    val expensesState by expensesViewModel.expensesUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = expensesState,
        onShowSnackbar = onShowSnackbar,
    ) { expensesScreenData ->
        ExpensesScreen()
    }
}

@Composable
private fun ExpensesScreen(
    expensesScreenData: ExpensesScreenData = ExpensesScreenData(
        // Create dummy expenses data
        listOf(
            UiExpense(
                amount = 1200.00,
                category = UiCategoryType.ESSENTIAL,
                paymentStatus = UiPaymentStatus.PAID,
                recurringType = UiRecurringType.MONTHLY,
                description = "Apartment Rent",
                paymentDate = "2024-12-01",
                dueDate = "2024-12-01",
            ),
            UiExpense(
                amount = 89.99,
                category = UiCategoryType.TRANSPORTATION,
                paymentStatus = UiPaymentStatus.PENDING,
                recurringType = UiRecurringType.MONTHLY,
                description = "Internet Bill",
                dueDate = "2024-12-15",
            ),
            UiExpense(
                amount = 65.50,
                category = UiCategoryType.HEALTHCARE,
                paymentStatus = UiPaymentStatus.OVERDUE,
                recurringType = UiRecurringType.MONTHLY,
                description = "Electricity Bill",
                dueDate = "2024-12-01",
            ),
            UiExpense(
                amount = 499.99,
                category = UiCategoryType.LIFESTYLE,
                paymentStatus = UiPaymentStatus.PAID,
                recurringType = UiRecurringType.NONE,
                description = "New Smartphone",
                paymentDate = "2024-12-03",
            ),
            UiExpense(
                amount = 12.99,
                category = UiCategoryType.SAVINGS,
                paymentStatus = UiPaymentStatus.CANCELLED,
                recurringType = UiRecurringType.MONTHLY,
                description = "Streaming Service Subscription",
                dueDate = "2024-12-10",
            ),
            UiExpense(
                amount = 45.00,
                category = UiCategoryType.DEBT,
                paymentStatus = UiPaymentStatus.PAID,
                recurringType = UiRecurringType.NONE,
                description = "Grocery Shopping",
                paymentDate = "2024-12-05",
            ),
            UiExpense(
                amount = 29.99,
                category = UiCategoryType.EDUCATION,
                paymentStatus = UiPaymentStatus.PENDING,
                recurringType = UiRecurringType.MONTHLY,
                description = "Gym Membership",
                dueDate = "2024-12-20",
            ),
            UiExpense(
                amount = 150.00,
                category = UiCategoryType.CUSTOM,
                paymentStatus = UiPaymentStatus.PENDING,
                recurringType = UiRecurringType.MONTHLY,
                description = "Public Transport Pass",
                dueDate = "2024-12-25",
            ),
        ),
    ),
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(expensesScreenData.expenses) { expense ->
            ExpenseCard(expense = expense)
        }
    }
}

@Composable
fun ExpenseCard(
    expense: UiExpense,
    modifier: Modifier = Modifier,
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
//        colors = CardDefaults.elevatedCardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = currencyFormatter.format(expense.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                PaymentStatusChip(status = expense.paymentStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryTypeChip(categoryType = expense.category)

                expense.description?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (expense.recurringType != UiRecurringType.NONE) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                expense.recurringType.name.lowercase().replaceFirstChar {
                                    if (it.isLowerCase()) {
                                        it.titlecase(
                                            Locale.getDefault(),
                                        )
                                    } else {
                                        it.toString()
                                    }
                                },
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Repeat,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }
            }

            if (expense.dueDate != null || expense.paymentDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    expense.dueDate?.let {
                        Text(
                            text = "Due: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    expense.paymentDate?.let {
                        Text(
                            text = "Paid: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTypeChip(
    categoryType: UiCategoryType,
    modifier: Modifier = Modifier,
) {
    val chipColors = when (categoryType) {
        UiCategoryType.ESSENTIAL -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )

        UiCategoryType.LIFESTYLE -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )

        UiCategoryType.TRANSPORTATION -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
        )

        UiCategoryType.HEALTHCARE -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
        )

        UiCategoryType.SAVINGS -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
        )

        UiCategoryType.DEBT -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )

        UiCategoryType.EDUCATION -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )

        UiCategoryType.CUSTOM -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )

        UiCategoryType.FOOD -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }

    Surface(
        modifier = modifier,
        color = chipColors.first,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = categoryType.name.lowercase()
                .replaceFirstChar { it.titlecase(Locale.getDefault()) },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = chipColors.second,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun PaymentStatusChip(
    status: UiPaymentStatus,
    modifier: Modifier = Modifier,
) {
    val chipColors = when (status) {
        UiPaymentStatus.PAID -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
        )

        UiPaymentStatus.PENDING -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )

        UiPaymentStatus.OVERDUE -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )

        UiPaymentStatus.CANCELLED -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    Surface(
        modifier = modifier,
        color = chipColors.first,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = status.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = chipColors.second,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
