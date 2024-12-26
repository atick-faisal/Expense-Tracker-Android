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

import android.graphics.drawable.GradientDrawable
import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// @Composable
// fun BudgetChart(
//    data: BudgetsScreenData,
//    modifier: Modifier = Modifier,
// ) {
//    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
//    val colorScheme = MaterialTheme.colorScheme
//
//    ElevatedCard(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(300.dp),
//        colors = CardDefaults.cardColors(),
//    ) {
//        AndroidView(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            factory = { context ->
//                LineChart(context).apply {
//                    // Basic chart setup
//                    description.isEnabled = false
//
//                    legend.apply {
//                        isEnabled = true
//                        textColor = colorScheme.onSurface.toArgb()
//                        form = Legend.LegendForm.LINE
//                    }
//                    setTouchEnabled(true)
//                    setScaleEnabled(false)
//                    setPinchZoom(false)
//
//                    // Set background color
//                    setBackgroundColor(Color.Transparent.toArgb())
//
//                    // X-Axis setup
//                    xAxis.apply {
//                        position = XAxis.XAxisPosition.BOTTOM
//                        valueFormatter = object : IAxisValueFormatter {
// //                            override fun getFormattedValue(value: Float): String {
// //                                val index = value.toInt()
// //                                return if (index >= 0 && index < data.cumulativeExpenses.size) {
// //                                    dateFormatter.format(Date(data.cumulativeExpenses[index].atTime))
// //                                } else ""
// //                            }
//
//                            override fun getFormattedValue(
//                                value: Float,
//                                axis: AxisBase?,
//                            ): String? {
//                                val index = value.toInt()
//                                return if (index >= 0 && index < data.cumulativeExpenses.size) {
//                                    dateFormatter.format(Date(data.cumulativeExpenses[index].atTime))
//                                } else {
//                                    ""
//                                }
//                            }
//                        }
//                        labelRotationAngle = -45f
//                        setDrawGridLines(false)
//                        textColor = colorScheme.onSurface.toArgb()
//                        axisLineColor = colorScheme.outline.toArgb()
//                    }
//
//                    // Y-Axis setup
//                    axisLeft.apply {
//                        setDrawGridLines(true)
//                        gridColor = colorScheme.outlineVariant.copy(alpha = 0.3f).toArgb()
//                        textColor = colorScheme.onSurface.toArgb()
//                        axisLineColor = colorScheme.outline.toArgb()
//                    }
//                    axisRight.isEnabled = false
//
//                    // Additional dark theme adjustments
//                    setNoDataTextColor(colorScheme.onSurface.toArgb())
//                    legend.textColor = colorScheme.onSurface.toArgb()
//                }
//            },
//            update = { chart ->
//                // Prepare expense line data
//                val expenseEntries = data.cumulativeExpenses.mapIndexed { index, expense ->
//                    Entry(index.toFloat(), expense.amount.toFloat())
//                }
//
//                // Prepare budget line data
//                val budgetEntries = data.cumulativeExpenses.mapIndexed { index, _ ->
//                    Entry(index.toFloat(), data.budget.amount.toFloat())
//                }
//
//                // Create expense line dataset
//                val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
//                    color = colorScheme.primary.toArgb()
//                    lineWidth = 3f
//                    setDrawCircles(true)
//                    setDrawValues(false)
//                    circleRadius = 4f
//                    circleColors = listOf(colorScheme.primary.toArgb())
//                    mode = LineDataSet.Mode.CUBIC_BEZIER
//
//                    // Dark theme specific
//                    highLightColor = colorScheme.primary.copy(alpha = 0.7f).toArgb()
//                    valueTextColor = colorScheme.onSurface.toArgb()
//
//                    // Add circle hole for better visibility in dark theme
//                    setDrawCircleHole(true)
//                    circleHoleColor = colorScheme.surface.toArgb()
//                    circleHoleRadius = 2f
//                }
//
//                // Create budget line dataset
//                val budgetDataSet = LineDataSet(budgetEntries, "Budget").apply {
//                    color = colorScheme.error.copy(alpha = 0.7f).toArgb()
//                    lineWidth = 2f
//                    setDrawCircles(false)
//                    setDrawValues(false)
//                    enableDashedLine(10f, 10f, 0f)
//                    setDrawFilled(false)
//                    valueTextColor = colorScheme.onSurface.toArgb()
//                }
//
//                // Set the data
//                chart.data = LineData(expenseDataSet, budgetDataSet)
//
//                // Update chart styling based on theme
//                chart.xAxis.textColor = colorScheme.onSurface.toArgb()
//                chart.axisLeft.textColor = colorScheme.onSurface.toArgb()
//                chart.legend.textColor = colorScheme.onSurface.toArgb()
//
//                // Refresh the chart
//                chart.invalidate()
//            },
//        )
//    }
// }

@Composable
fun BudgetChart(
    data: BudgetsScreenData,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(),
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            factory = { context ->
                LineChart(context).apply {
                    // Enhanced chart setup
                    description.isEnabled = false
                    setExtraOffsets(8f, 16f, 8f, 16f) // Better padding

                    // Improved legend styling
                    legend.apply {
                        isEnabled = true
                        textColor = colorScheme.onSurface.toArgb()
                        form = Legend.LegendForm.LINE
                        textSize = 12f
                        xEntrySpace = 20f
                        yEntrySpace = 0f
                        formSize = 16f
                        formLineWidth = 2f
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    }

                    // Enhanced interaction
                    setTouchEnabled(true)
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    isDoubleTapToZoomEnabled = false

                    // Marker setup for better tooltips
//                    marker = MarkerView(context, R.layout.custom_marker_view).apply {
//                        chartView = this@apply
//                        // Custom marker implementation here
//                    }

                    // Enhanced X-Axis setup
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = object : IAxisValueFormatter {
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
//                                 val index = value.toInt()
//                                return if (index >= 0 && index < data.cumulativeExpenses.size) {
//                                    dateFormatter.format(Date(data.cumulativeExpenses[index].atTime))
//                                } else "aa"

                                return dateFormatter.format(Date(value.toLong()))
                            }
                        }
                        labelRotationAngle = -45f
                        setDrawGridLines(false)
                        textColor = colorScheme.onSurface.toArgb()
                        axisLineColor = colorScheme.outline.toArgb()
                        textSize = 10f
                        granularity = 1f
                        yOffset = 8f
                    }

                    // Enhanced Y-Axis setup
                    axisLeft.apply {
                        setDrawGridLines(true)
                        gridColor = colorScheme.outlineVariant.copy(alpha = 0.2f).toArgb()
                        textColor = colorScheme.onSurface.toArgb()
                        axisLineColor = colorScheme.outline.toArgb()
                        textSize = 10f
                        setLabelCount(6, true)
                        valueFormatter = object : IAxisValueFormatter {
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return NumberFormat.getCurrencyInstance().format(value)
                            }
                        }
                    }
                    axisRight.isEnabled = false

                    // Animation setup
                    animateX(1000)
                    animateY(1000)
                }
            },
            update = { chart ->
                // Prepare expense line data with enhanced visuals
                val expenseEntries = data.cumulativeExpenses.map { expense ->
                    Entry(expense.atTime.toFloat(), expense.amount.toFloat())
                }

                val budgetEntries = data.cumulativeExpenses.map { expense ->
                    Entry(expense.atTime.toFloat(), data.budget.amount.toFloat())
                }

                // Enhanced expense line dataset
                val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
                    color = colorScheme.primary.toArgb()
                    lineWidth = 2.5f
                    setDrawCircles(true)
                    setDrawValues(false)
                    circleRadius = 4f
                    circleColors = listOf(colorScheme.primary.toArgb())
                    mode = LineDataSet.Mode.CUBIC_BEZIER

                    // Enhanced gradient
                    setDrawFilled(true)
                    fillDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(
                            colorScheme.primary.copy(alpha = 0.4f).toArgb(),
                            colorScheme.primary.copy(alpha = 0f).toArgb(),
                        ),
                    )

                    // Improved highlighting
                    highLightColor = colorScheme.primary.copy(alpha = 0.7f).toArgb()
                    highlightLineWidth = 1.5f
                    setDrawHorizontalHighlightIndicator(false)

                    // Enhanced circles
                    setDrawCircleHole(true)
                    circleHoleColor = colorScheme.surface.toArgb()
                    circleHoleRadius = 2f
                }

                // Enhanced budget line dataset
                val budgetDataSet = LineDataSet(budgetEntries, "Budget Limit").apply {
                    color = colorScheme.error.copy(alpha = 0.8f).toArgb()
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                    enableDashedLine(15f, 8f, 0f)
                    setDrawFilled(false)
                    valueTextColor = colorScheme.onSurface.toArgb()

                    // Enhanced highlighting
                    highLightColor = colorScheme.error.copy(alpha = 0.7f).toArgb()
                    highlightLineWidth = 1.5f
                    setDrawHorizontalHighlightIndicator(false)
                }

                // Set the data with animation
                if (data.budget.amount == Double.MAX_VALUE) {
                    chart.data = LineData(expenseDataSet)
                } else {
                    chart.data = LineData(expenseDataSet, budgetDataSet)
                }

                // Refresh with animation
                chart.animateX(1000)
                chart.invalidate()
            },
        )
    }
}
