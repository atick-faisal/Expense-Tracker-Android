/*
 * Copyright 2023 Atick Faisal
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

package dev.atick.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import dev.atick.compose.navigation.analysis.analysisScreen
import dev.atick.compose.navigation.budgets.budgetsNavGraph
import dev.atick.compose.navigation.budgets.budgetsScreen
import dev.atick.compose.navigation.budgets.editBudgetScreen
import dev.atick.compose.navigation.chat.chatScreen
import dev.atick.compose.navigation.expenses.ExpensesNavGraph
import dev.atick.compose.navigation.expenses.editExpenseScreen
import dev.atick.compose.navigation.expenses.expensesNavGraph
import dev.atick.compose.navigation.expenses.expensesScreen
import dev.atick.compose.navigation.expenses.navigateToEditExpenseScreen
import dev.atick.compose.navigation.intro.Intro
import dev.atick.compose.navigation.intro.introScreen
import dev.atick.compose.navigation.subscriptions.subscriptionsScreen
import dev.atick.compose.ui.JetpackAppState
import dev.atick.core.utils.MonthInfo

/**
 * Builds the App NavHost.
 *
 * @param appState The Jetpack app state.
 * @param monthInfo The month information.
 * @param onShowSnackbar The callback to show a snackbar.
 * @param modifier The modifier.
 */
@Composable
fun JetpackNavHost(
    appState: JetpackAppState,
    monthInfo: MonthInfo,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val startDestination = if (appState.userOnboarded) ExpensesNavGraph::class else Intro::class

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Intro
        introScreen(
            onShowSnackbar = onShowSnackbar,
        )

        // Expenses
        expensesNavGraph {
            expensesScreen(
                monthInfo = monthInfo,
                onExpenseClick = navController::navigateToEditExpenseScreen,
                onShowSnackbar = onShowSnackbar,
            )
            editExpenseScreen(
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
            )
        }

        // Analysis
        analysisScreen(
            monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )

        // Budgets
        budgetsNavGraph {
            budgetsScreen(
                monthInfo = monthInfo,
                onShowSnackbar = onShowSnackbar,
            )
            editBudgetScreen(
                monthInfo = monthInfo,
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
            )
        }

        // Subscriptions
        subscriptionsScreen(
            onShowSnackbar = onShowSnackbar,
        )

        // Chat
        chatScreen(
            monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
