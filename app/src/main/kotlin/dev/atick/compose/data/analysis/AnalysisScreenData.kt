package dev.atick.compose.data.analysis

import dev.atick.compose.data.categories.UiCategory

data class AnalysisScreenData(
    val analysis: List<UiAnalysis> = emptyList(),
)

data class UiAnalysis(
    val spending: Double,
    val budget: Double,
    val category: UiCategory,
    val description: String? = null,
)