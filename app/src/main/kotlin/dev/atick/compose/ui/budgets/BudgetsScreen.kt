package dev.atick.compose.ui.budgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun BudgetsRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    budgetsViewModel: BudgetsViewModel = hiltViewModel(),
) {
    val budgetsUiState by budgetsViewModel.budgetsUiState.collectAsStateWithLifecycle()

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

}