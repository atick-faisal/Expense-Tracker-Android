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

import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.budgets.BudgetStatus
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.compose.data.expenses.UiCurrencyType
import dev.atick.compose.ui.components.BudgetChart
import dev.atick.compose.ui.components.rememberCurrencyFormatter
import dev.atick.core.extensions.format
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
    val currencyFormatter = rememberCurrencyFormatter(UiCurrencyType.QAR)

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            BudgetStatusCard(
                screenData = budgetsScreenData,
                currencyFormatter = currencyFormatter,
            )
        }

        item {
            BudgetChart(
                data = budgetsScreenData,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun BudgetStatusCard(
    screenData: BudgetsScreenData,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier,
) {
    val statusConfig = when (screenData.budgetStatus) {
        BudgetStatus.SAFE -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            icon = Icons.Default.CheckCircle,
            title = "Budget On Track",
            message = "You're managing your budget well!",
        )

        BudgetStatus.WARNING -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            icon = Icons.Default.Info,
            title = "Approaching Budget Limit",
            message = "Consider reducing expenses",
        )

        BudgetStatus.CRITICAL -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            icon = Icons.Default.Warning,
            title = "Near Budget Limit",
            message = "Critical threshold reached",
        )

        BudgetStatus.EXCEEDED -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            icon = Icons.Default.Error,
            title = "Budget Exceeded",
            message = "Immediate action required",
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusConfig.containerColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = statusConfig.icon,
                    contentDescription = statusConfig.title,
                    tint = statusConfig.contentColor,
                )
                Text(
                    text = statusConfig.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = statusConfig.contentColor,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar with dynamic colors
            LinearProgressIndicator(
                progress = { (screenData.percentageUsed / 100).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = statusConfig.contentColor,
                trackColor = statusConfig.contentColor.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Budget information
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                BudgetInfoRow(
                    label = "Total Budget:",
                    value = currencyFormatter.format(screenData.budget.amount),
                    color = statusConfig.contentColor,
                )
                BudgetInfoRow(
                    label = "Current Expenses:",
                    value = currencyFormatter.format(screenData.currentExpenses),
                    color = statusConfig.contentColor,
                )
                if (screenData.budgetStatus == BudgetStatus.EXCEEDED) {
                    BudgetInfoRow(
                        label = "Over Budget By:",
                        value = currencyFormatter.format(screenData.overBudgetAmount),
                        color = statusConfig.contentColor,
                    )
                } else {
                    BudgetInfoRow(
                        label = "Remaining:",
                        value = currencyFormatter.format(screenData.remainingBudget),
                        color = statusConfig.contentColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusConfig.message,
                style = MaterialTheme.typography.bodyMedium,
                color = statusConfig.contentColor,
            )

            Text(
                text = "${screenData.percentageUsed.format()}% of budget used",
                style = MaterialTheme.typography.labelMedium,
                color = statusConfig.contentColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private data class StatusConfig(
    val containerColor: Color,
    val contentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val message: String,
)

@Composable
private fun BudgetInfoRow(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color.copy(alpha = 0.8f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}
