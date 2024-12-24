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

import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.atick.compose.R
import dev.atick.compose.data.expenses.UiCategoryType
import dev.atick.compose.data.expenses.UiCurrencyType
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.core.ui.extensions.rememberToastState
import java.util.Locale

@Composable
fun ExpenseCard(
    expense: UiExpense,
    onExpenseClick: ((Long) -> Unit)? = null,
    onRecurringTypeClick: ((String, UiRecurringType) -> Unit)? = null,
    onCancellationClick: ((String, Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (onExpenseClick != null) {
        ElevatedCard(
            modifier = modifier,
            onClick = { onExpenseClick(expense.id) },
        ) {
            ExpenseCardContent(
                expense = expense,
                onRecurringTypeClick = onRecurringTypeClick,
                onCancellationClick = onCancellationClick,
            )
        }
    } else {
        Card(
            modifier = modifier,
        ) {
            ExpenseCardContent(
                expense = expense,
                onRecurringTypeClick = onRecurringTypeClick,
                onCancellationClick = onCancellationClick,
            )
        }
    }
}

@Composable
internal fun ExpenseCardContent(
    expense: UiExpense,
    onRecurringTypeClick: ((String, UiRecurringType) -> Unit)? = null,
    onCancellationClick: ((String, Boolean) -> Unit)? = null,
) {
    val currencyFormatter = rememberCurrencyFormatter(expense.currency)
    val cancelBackgroundColor =
        if (expense.toBeCancelled) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val cancelForegroundColor =
        if (expense.toBeCancelled) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

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

            expense.merchant.let {
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
            RecurringTypeChip(expense, onRecurringTypeClick)

            if (onCancellationClick != null) {
                AssistChip(
                    onClick = {
                        onCancellationClick(
                            expense.merchant,
                            !expense.toBeCancelled,
                        )
                    },
                    label = {
                        Text(
                            if (expense.toBeCancelled) {
                                stringResource(R.string.to_be_cancelled)
                            } else {
                                stringResource(
                                    R.string.to_be_continued,
                                )
                            },
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (expense.toBeCancelled) {
                                Icons.Filled.Cancel
                            } else {
                                Icons.Filled.Check
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = cancelForegroundColor,
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = cancelForegroundColor,
                        leadingIconContentColor = cancelForegroundColor,
                        containerColor = cancelBackgroundColor,
                    ),
                )
            }
        }

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
            Text(
                text = "Paid: ${expense.formattedDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun RecurringTypeChip(
    expense: UiExpense,
    onRecurringTypeClick: ((String, UiRecurringType) -> Unit)? = null,
) {
    val (icon, color) = when (expense.recurringType) {
        UiRecurringType.ONETIME -> Icons.Filled.Schedule to MaterialTheme.colorScheme.onSurfaceVariant
        UiRecurringType.DAILY -> Icons.Filled.Today to MaterialTheme.colorScheme.error
        UiRecurringType.WEEKLY -> Icons.Filled.ViewWeek to MaterialTheme.colorScheme.primary
        UiRecurringType.MONTHLY -> Icons.Filled.CalendarMonth to MaterialTheme.colorScheme.secondary
        UiRecurringType.YEARLY -> Icons.Filled.CalendarMonth to MaterialTheme.colorScheme.tertiary
    }

    val showToast = rememberToastState()

    AssistChip(
        onClick = {
            onRecurringTypeClick?.invoke(expense.merchant, expense.recurringType)
                ?: showToast("Recurring type: ${expense.recurringType.name}")
        },
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
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color,
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = color,
            leadingIconContentColor = color,
        ),
        // enabled = onRecurringTypeClick != null,
    )
}

@Composable
internal fun CategoryTypeChip(
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
internal fun PaymentStatusChip(
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

@Composable
internal fun rememberCurrencyFormatter(currencyType: UiCurrencyType): NumberFormat {
    return remember(currencyType) {
        when (currencyType) {
            UiCurrencyType.QAR -> NumberFormat.getCurrencyInstance(Locale("en", "QA"))
            UiCurrencyType.USD -> NumberFormat.getCurrencyInstance(Locale.US)
            UiCurrencyType.EUR -> NumberFormat.getCurrencyInstance(Locale.GERMANY)
            UiCurrencyType.GBP -> NumberFormat.getCurrencyInstance(Locale.UK)
            UiCurrencyType.BDT -> NumberFormat.getCurrencyInstance(Locale("en", "BD"))
        }.apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }
}
