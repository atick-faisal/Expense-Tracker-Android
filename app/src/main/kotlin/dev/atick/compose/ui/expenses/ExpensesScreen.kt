package dev.atick.compose.ui.expenses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.expenses.ExpensesScreenData
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun ExpensesRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    expensesViewModel: ExpensesViewModel = hiltViewModel(),
) {
    val expensesState by expensesViewModel.expensesUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = expensesState,
        onShowSnackbar = onShowSnackbar,
    ) { expensesScreenData ->
        ExpensesScreen(expensesScreenData)
    }
}

@Composable
private fun ExpensesScreen(
    expensesScreenData: ExpensesScreenData,
) {

}