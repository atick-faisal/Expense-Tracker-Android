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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.compose.data.budgets.UiCumulativeExpense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BudgetChart(
    data: BudgetsScreenData,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    val colorScheme = MaterialTheme.colorScheme

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(),
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            factory = { context ->
                LineChart(context).apply {
                    // Basic chart setup
                    description.isEnabled = false
                    legend.apply {
                        isEnabled = true
                        textColor = colorScheme.onSurface.toArgb()
                        form = Legend.LegendForm.LINE
                    }
                    setTouchEnabled(true)
                    setScaleEnabled(false)
                    setPinchZoom(false)

                    // Set background color
                    setBackgroundColor(Color.Transparent.toArgb())

                    // X-Axis setup
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = object : IAxisValueFormatter {
//                            override fun getFormattedValue(value: Float): String {
//                                val index = value.toInt()
//                                return if (index >= 0 && index < data.cumulativeExpenses.size) {
//                                    dateFormatter.format(Date(data.cumulativeExpenses[index].atTime))
//                                } else ""
//                            }

                            override fun getFormattedValue(
                                value: Float,
                                axis: AxisBase?,
                            ): String? {
                                val index = value.toInt()
                                return if (index >= 0 && index < data.cumulativeExpenses.size) {
                                    dateFormatter.format(Date(data.cumulativeExpenses[index].atTime))
                                } else {
                                    ""
                                }
                            }
                        }
                        labelRotationAngle = -45f
                        setDrawGridLines(false)
                        textColor = colorScheme.onSurface.toArgb()
                        axisLineColor = colorScheme.outline.toArgb()
                    }

                    // Y-Axis setup
                    axisLeft.apply {
                        setDrawGridLines(true)
                        gridColor = colorScheme.outlineVariant.copy(alpha = 0.3f).toArgb()
                        textColor = colorScheme.onSurface.toArgb()
                        axisLineColor = colorScheme.outline.toArgb()
                    }
                    axisRight.isEnabled = false

                    // Additional dark theme adjustments
                    setNoDataTextColor(colorScheme.onSurface.toArgb())
                    getLegend().textColor = colorScheme.onSurface.toArgb()
                }
            },
            update = { chart ->
                // Prepare expense line data
                val expenseEntries = data.cumulativeExpenses.mapIndexed { index, expense ->
                    Entry(index.toFloat(), expense.amount.toFloat())
                }

                // Prepare budget line data
                val budgetEntries = data.cumulativeExpenses.mapIndexed { index, _ ->
                    Entry(index.toFloat(), data.totalBudget.toFloat())
                }

                // Create expense line dataset
                val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
                    color = colorScheme.primary.toArgb()
                    lineWidth = 3f
                    setDrawCircles(true)
                    setDrawValues(false)
                    circleRadius = 4f
                    circleColors = listOf(colorScheme.primary.toArgb())
                    mode = LineDataSet.Mode.CUBIC_BEZIER

                    // Dark theme specific
                    highLightColor = colorScheme.primary.copy(alpha = 0.7f).toArgb()
                    valueTextColor = colorScheme.onSurface.toArgb()

                    // Add circle hole for better visibility in dark theme
                    setDrawCircleHole(true)
                    circleHoleColor = colorScheme.surface.toArgb()
                    circleHoleRadius = 2f
                }

                // Create budget line dataset
                val budgetDataSet = LineDataSet(budgetEntries, "Budget").apply {
                    color = colorScheme.error.copy(alpha = 0.7f).toArgb()
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                    enableDashedLine(10f, 10f, 0f)
                    setDrawFilled(false)
                    valueTextColor = colorScheme.onSurface.toArgb()
                }

                // Set the data
                chart.data = LineData(expenseDataSet, budgetDataSet)

                // Update chart styling based on theme
                chart.xAxis.textColor = colorScheme.onSurface.toArgb()
                chart.axisLeft.textColor = colorScheme.onSurface.toArgb()
                chart.legend.textColor = colorScheme.onSurface.toArgb()

                // Refresh the chart
                chart.invalidate()
            },
        )
    }
}

@Preview
@Composable
fun BudgetChartPreview() {
    MaterialTheme {
        val sampleData = BudgetsScreenData(
            cumulativeExpenses = listOf(
                UiCumulativeExpense(1000.0, System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000),
                UiCumulativeExpense(2500.0, System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),
                UiCumulativeExpense(3200.0, System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000),
                UiCumulativeExpense(4100.0, System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000),
                UiCumulativeExpense(4800.0, System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                UiCumulativeExpense(5500.0, System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                UiCumulativeExpense(6200.0, System.currentTimeMillis()),
            ),
            totalBudget = 5000.0,
        )
        BudgetChart(data = sampleData)
    }
}
