package dev.atick.compose.navigation.expenses

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.atick.compose.ui.expenses.ExpensesRoute
import kotlinx.serialization.Serializable

@Serializable
data object Expenses

fun NavController.navigateToExpenses(navOptions: NavOptions? = null) {
    navigate(Expenses, navOptions)
}

fun NavGraphBuilder.expensesScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Expenses> {
        ExpensesRoute(
            onShowSnackbar = onShowSnackbar,
        )
    }
}