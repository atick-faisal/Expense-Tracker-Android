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

package dev.atick.compose.navigation.budgets

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.atick.compose.ui.budgets.BudgetsRoute
import dev.atick.core.utils.MonthInfo
import kotlinx.serialization.Serializable

/**
 * Represents the budgets route.
 */
@Serializable
data object Budgets

/**
 * Represents the budgets navigation graph.
 */
@Serializable
data object BudgetsNavGraph

/**
 * Navigates to the budgets navigation graph.
 *
 * @param navOptions The navigation options.
 */
fun NavController.navigateToBudgetsNavGraph(navOptions: NavOptions? = null) {
    navigate(BudgetsNavGraph, navOptions)
}

/**
 * Navigates to the budgets screen.
 *
 * @param monthInfo The month information.
 * @param onShowSnackbar The callback to show a snackbar.
 */
fun NavGraphBuilder.budgetsScreen(
    monthInfo: MonthInfo,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Budgets> {
        BudgetsRoute(
            monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )
    }
}

/**
 * Builds the budgets navigation graph.
 *
 * @param nestedNavGraphs The nested navigation graphs.
 */
fun NavGraphBuilder.budgetsNavGraph(
    nestedNavGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<BudgetsNavGraph>(startDestination = Budgets) {
        nestedNavGraphs()
    }
}
