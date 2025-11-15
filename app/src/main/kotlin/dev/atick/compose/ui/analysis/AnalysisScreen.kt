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

package dev.atick.compose.ui.analysis

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.compose.data.analysis.AnalysisScreenData
import dev.atick.compose.data.analysis.UiAnalysis
import dev.atick.compose.ui.common.Placeholder
import dev.atick.core.ui.components.JetpackToggleOptions
import dev.atick.core.ui.components.ToggleOption
import dev.atick.core.ui.utils.StatefulComposable
import dev.atick.core.utils.MonthInfo

@Composable
internal fun AnalysisRoute(
    monthInfo: MonthInfo,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    analysisViewModel: AnalysisViewModel = hiltViewModel(),
) {
    val analysisUiState by analysisViewModel.analysisUiState.collectAsStateWithLifecycle()

    LaunchedEffect(monthInfo) {
        analysisViewModel.refreshAnalysis(monthInfo)
    }

    StatefulComposable(
        state = analysisUiState,
        onShowSnackbar = onShowSnackbar,
    ) { analysisScreenData ->
        AnalysisScreen(analysisScreenData)
    }
}

private val AnalysisToggleOptions = listOf(
    ToggleOption(R.string.categories, Icons.Filled.Category),
    ToggleOption(R.string.merchants, Icons.Filled.Store),
)

@Composable
private fun AnalysisScreen(
    analysisScreenData: AnalysisScreenData,
) {
    // Show placeholder if there are no analyses
    if (analysisScreenData.categoryAnalyses.isEmpty() &&
        analysisScreenData.merchantAnalyses.isEmpty()
    ) {
        Placeholder()
    } else {
        AnalysisList(
            categoryAnalyses = analysisScreenData.categoryAnalyses,
            merchantAnalyses = analysisScreenData.merchantAnalyses,
        )
    }
}

@Composable
private fun AnalysisList(
    categoryAnalyses: List<UiAnalysis>,
    merchantAnalyses: List<UiAnalysis>,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    // Show the analyses based on the selected index
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Toggle options
        item {
            JetpackToggleOptions(
                options = AnalysisToggleOptions,
                selectedIndex = selectedIndex,
                onSelectionChanged = { selectedIndex = it },
            )
        }

        if (selectedIndex == 0) {
            // Show the top categories
            item {
                SectionHeader(
                    title = stringResource(R.string.top_categories),
                    icon = Icons.Filled.Category,
                )
            }

            // Show the distribution chart
            item {
                ExpenseDistributionChart(
                    analyses = categoryAnalyses,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Show the category analyses
            items(categoryAnalyses) { analysis ->
                ExpenseAnalysisCard(analysis = analysis)
            }
        }

        if (selectedIndex == 1) {
            // Show the top merchants
            item {
                SectionHeader(
                    title = stringResource(R.string.top_merchants),
                    icon = Icons.Filled.Store,
                )
            }

            // Show the distribution chart
            item {
                ExpenseDistributionChart(
                    analyses = merchantAnalyses,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Show the merchant analyses
            items(merchantAnalyses) { analysis ->
                ExpenseAnalysisCard(analysis = analysis)
            }
        }
    }
}

@Composable
private fun ExpenseAnalysisCard(
    analysis: UiAnalysis,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surface

    val animatedPercentage by animateFloatAsState(
        targetValue = analysis.percentage.toFloat(),
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "percentage animation",
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Category or merchant name
                Text(
                    text = analysis.categoryOrMerchant,
                    style = MaterialTheme.typography.bodyLarge,
                )

                // Spending amount
                Text(
                    text = "${analysis.currency} ${analysis.spending}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                // Min and max amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Minimum amount
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Minimum icon
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = "Minimum amount",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )

                        Column {
                            // Minimum label
                            Text(
                                text = stringResource(R.string.min),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            // Minimum amount
                            Text(
                                text = "${analysis.currency} ${analysis.minAmount}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    // Maximum amount
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Maximum icon
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = "Maximum amount",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp),
                        )

                        Column {
                            // Maximum label
                            Text(
                                text = stringResource(R.string.max),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            // Maximum amount
                            Text(
                                text = "${analysis.currency} ${analysis.maxAmount}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }

            // Percentage and chart
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = surfaceVariantColor,
                        style = Stroke(width = 8f, cap = StrokeCap.Round),
                    )

                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * (animatedPercentage / 100f),
                        useCenter = false,
                        style = Stroke(width = 8f, cap = StrokeCap.Round),
                    )
                }

                Text(
                    text = "${animatedPercentage.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Icon with background
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // Title and decoration
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            // Decorative line
            VerticalDivider(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(0.5f),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            )
        }
    }
}
