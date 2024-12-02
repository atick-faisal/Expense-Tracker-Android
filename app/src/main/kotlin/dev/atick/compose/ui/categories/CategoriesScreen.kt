package dev.atick.compose.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.categories.CategoriesScreenData
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun CategoriesRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
) {
    val categoriesUiState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = categoriesUiState,
        onShowSnackbar = onShowSnackbar,
    ) { categoriesScreenData ->
        CategoriesScreen(categoriesScreenData)
    }
}

@Composable
private fun CategoriesScreen(
    categoriesScreenData: CategoriesScreenData,
) {

}