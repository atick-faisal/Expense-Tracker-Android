package dev.atick.compose.navigation.budgets

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.atick.compose.ui.budgets.BudgetsRoute
import kotlinx.serialization.Serializable

@Serializable
data object Budgets

fun NavController.navigateToBudgets(navOptions: NavOptions?) {
    navigate(Budgets, navOptions)
}

fun NavGraphBuilder.budgetsScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Budgets> {
        BudgetsRoute(
            onShowSnackbar = onShowSnackbar,
        )
    }
}