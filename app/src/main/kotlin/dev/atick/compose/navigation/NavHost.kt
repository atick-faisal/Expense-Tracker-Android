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
import dev.atick.auth.navigation.authNavGraph
import dev.atick.auth.navigation.navigateToSignInRoute
import dev.atick.auth.navigation.navigateToSignUpRoute
import dev.atick.auth.navigation.signInScreen
import dev.atick.auth.navigation.signUpScreen
import dev.atick.compose.navigation.analysis.analysisScreen
import dev.atick.compose.navigation.budgets.budgetsScreen
import dev.atick.compose.navigation.chat.chatScreen
import dev.atick.compose.navigation.expenses.ExpensesNavGraph
import dev.atick.compose.navigation.expenses.editExpenseScreen
import dev.atick.compose.navigation.expenses.expensesNavGraph
import dev.atick.compose.navigation.expenses.expensesScreen
import dev.atick.compose.navigation.expenses.navigateToEditExpenseScreen
import dev.atick.compose.navigation.subscriptions.subscriptionsScreen
import dev.atick.compose.ui.JetpackAppState
import dev.atick.core.utils.MonthInfo

@Composable
fun JetpackNavHost(
    appState: JetpackAppState,
    monthInfo: MonthInfo,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onFabClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val startDestination = ExpensesNavGraph::class
    // if (appState.isUserLoggedIn) HomeNavGraph::class else AuthNavGraph::class
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        authNavGraph(
            nestedNavGraphs = {
                signInScreen(
                    onSignUpClick = navController::navigateToSignUpRoute,
                    onShowSnackbar = onShowSnackbar,
                )
                signUpScreen(
                    onSignInClick = navController::navigateToSignInRoute,
                    onShowSnackbar = onShowSnackbar,
                )
            },
        )
        /*
        homeNavGraph(
            nestedNavGraphs = {
                homeScreen(
                    onPostClick = navController::navigateToDetailsScreen,
                    onShowSnackbar = onShowSnackbar,
                )
                detailsScreen(
                    onBackClick = navController::popBackStack,
                    onShowSnackbar = onShowSnackbar,
                )
            },
        )
        profileScreen(
            onShowSnackbar = onShowSnackbar,
            onPurchaseClick = navController::navigateToBilling,
        )
        billingScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        */
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
        analysisScreen(
            monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )
        budgetsScreen(
            monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )
        subscriptionsScreen(
            onShowSnackbar = onShowSnackbar,
        )
        chatScreen(
            // monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
