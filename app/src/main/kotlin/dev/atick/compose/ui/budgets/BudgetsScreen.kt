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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.compose.data.budgets.BudgetStatus
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.compose.data.expenses.UiCurrencyType
import dev.atick.compose.ui.common.rememberCurrencyFormatter
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
        if (budgetsScreenData.budget.amount != null) {
            item {
                BudgetStatusCard(
                    screenData = budgetsScreenData,
                    currencyFormatter = currencyFormatter,
                )
            }
        } else {
            item {
                BudgetNotSetCard()
            }
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
fun BudgetNotSetCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(R.string.budget_not_set),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    text = stringResource(R.string.budget_not_set),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.set_budget_to_track_expenses),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
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
            title = stringResource(R.string.budget_on_track),
            message = stringResource(R.string.you_re_managing_your_budget_well),
        )

        BudgetStatus.WARNING -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            icon = Icons.Default.Info,
            title = stringResource(R.string.approaching_budget_limit),
            message = stringResource(R.string.consider_reducing_expenses),
        )

        BudgetStatus.CRITICAL -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            icon = Icons.Default.Warning,
            title = stringResource(R.string.near_budget_limit),
            message = stringResource(R.string.critical_threshold_reached),
        )

        BudgetStatus.EXCEEDED -> StatusConfig(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            icon = Icons.Default.Error,
            title = stringResource(R.string.budget_exceeded),
            message = stringResource(R.string.immediate_action_required),
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
                    label = stringResource(R.string.total_budget),
                    value = if (screenData.budget.amount == null) {
                        stringResource(R.string.not_set)
                    } else {
                        currencyFormatter.format(screenData.budget.amount)
                    },
                    color = statusConfig.contentColor,
                )
                BudgetInfoRow(
                    label = stringResource(R.string.current_expenses),
                    value = currencyFormatter.format(screenData.currentExpenses),
                    color = statusConfig.contentColor,
                )
                if (screenData.budgetStatus == BudgetStatus.EXCEEDED) {
                    BudgetInfoRow(
                        label = stringResource(R.string.over_budget_by),
                        value = currencyFormatter.format(screenData.overBudgetAmount),
                        color = statusConfig.contentColor,
                    )
                } else {
                    BudgetInfoRow(
                        label = stringResource(R.string.remaining),
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
                text = stringResource(R.string.of_budget_used, screenData.percentageUsed.format()),
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
