package dev.atick.compose.navigation.analysis

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.atick.compose.ui.analysis.AnalysisRoute
import kotlinx.serialization.Serializable

@Serializable
data object Analysis

fun NavController.navigateToAnalysis(navOptions: NavOptions?) {
    navigate(Analysis, navOptions)
}

fun NavGraphBuilder.analysisScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Analysis> {
        AnalysisRoute(
            onShowSnackbar = onShowSnackbar,
        )
    }
}