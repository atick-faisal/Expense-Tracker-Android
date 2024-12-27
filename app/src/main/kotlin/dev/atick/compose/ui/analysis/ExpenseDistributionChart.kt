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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.atick.compose.data.analysis.UiAnalysis

@Composable
fun ExpenseDistributionChart(
    analyses: List<UiAnalysis>,
    modifier: Modifier = Modifier,
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing,
        ),
        label = "pie chart animation",
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    // Generate colors for the pie chart
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.errorContainer,
    )

    val surfaceColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        // Pie Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            ) {
                val total = analyses.sumOf { it.percentage }
                var startAngle = -90f // Start from top

                analyses.forEachIndexed { index, analysis ->
                    val sweepAngle =
                        ((analysis.percentage / total * 360f) * animatedProgress).toFloat()
                    val color = colors[index % colors.size]

                    // Draw arc
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height),
                    )

                    // Draw outline
                    drawArc(
                        color = surfaceColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        style = Stroke(width = 2f),
                        size = Size(size.width, size.height),
                    )

                    startAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            analyses.forEachIndexed { index, analysis ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Color indicator
                    Surface(
                        modifier = Modifier.size(12.dp),
                        color = colors[index % colors.size],
                        shape = MaterialTheme.shapes.small,
                    ) {}

                    // Category name
                    Text(
                        text = analysis.categoryOrMerchant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )

                    // Percentage
                    Text(
                        text = "${analysis.percentage.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    // Amount
                    Text(
                        text = "${analysis.currency} ${analysis.spending}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpenseDistributionChartPreview() {
    MaterialTheme {
        ExpenseDistributionChart(
            analyses = listOf(
                UiAnalysis(
                    categoryOrMerchant = "Groceries",
                    spending = 1234.56,
                    currency = "QAR",
                    maxAmount = 2500.00,
                    minAmount = 800.00,
                    percentage = 35.0,
                ),
                UiAnalysis(
                    categoryOrMerchant = "Transport",
                    spending = 876.54,
                    currency = "QAR",
                    maxAmount = 1500.00,
                    minAmount = 500.00,
                    percentage = 25.0,
                ),
                // Add more sample data as needed
            ),
        )
    }
}
