package dev.atick.compose.ui.analysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.analysis.AnalysisScreenData
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun AnalysisRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    analysisViewModel: AnalysisViewModel = hiltViewModel(),
) {
    val analysisUiState by analysisViewModel.analysisUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = analysisUiState,
        onShowSnackbar = onShowSnackbar,
    ) { analysisScreenData ->
        AnalysisScreen(analysisScreenData)
    }
}

@Composable
private fun AnalysisScreen(
    analysisScreenData: AnalysisScreenData,
) {

}