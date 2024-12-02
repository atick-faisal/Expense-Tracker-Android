package dev.atick.compose.navigation.categories

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.atick.compose.ui.categories.CategoriesRoute
import kotlinx.serialization.Serializable

@Serializable
data object Categories

fun NavController.navigateToCategories(navOptions: NavOptions? = null) {
    navigate(Categories, navOptions)
}

fun NavGraphBuilder.categoriesScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Categories> {
        CategoriesRoute(
            onShowSnackbar = onShowSnackbar,
        )
    }
}