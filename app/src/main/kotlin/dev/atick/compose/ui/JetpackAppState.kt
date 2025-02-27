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

package dev.atick.compose.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import dev.atick.compose.navigation.TopLevelDestination
import dev.atick.compose.navigation.analysis.navigateToAnalysis
import dev.atick.compose.navigation.budgets.navigateToBudgetsNavGraph
import dev.atick.compose.navigation.budgets.navigateToEditBudgetScreen
import dev.atick.compose.navigation.chat.navigateToChat
import dev.atick.compose.navigation.expenses.navigateToEditExpenseScreen
import dev.atick.compose.navigation.expenses.navigateToExpensesNavGraph
import dev.atick.compose.navigation.subscriptions.navigateToSubscriptions
import dev.atick.core.extensions.stateInDelayed
import dev.atick.network.utils.NetworkState
import dev.atick.network.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

@Composable
fun rememberJetpackAppState(
    userOnboarded: Boolean,
    windowSizeClass: WindowSizeClass,
    networkUtils: NetworkUtils,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): JetpackAppState {
    return remember(
        userOnboarded,
        navController,
        windowSizeClass,
        coroutineScope,
        networkUtils,
    ) {
        JetpackAppState(
            userOnboarded,
            navController,
            windowSizeClass,
            coroutineScope,
            networkUtils,
        )
    }
}

@Suppress("MemberVisibilityCanBePrivate", "UNUSED")
@Stable
class JetpackAppState(
    val userOnboarded: Boolean,
    val navController: NavHostController,
    val windowSizeClass: WindowSizeClass,
    coroutineScope: CoroutineScope,
    networkUtils: NetworkUtils,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            // Getting the current back stack entry here instead of using currentDestination
            // solves the vanishing bottom bar issue
            // (https://github.com/atick-faisal/Jetpack-Compose-Starter/issues/255)
            val backStackEntry by navController.currentBackStackEntryAsState()
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                backStackEntry?.destination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    val shouldShowBottomBar: Boolean
        @Composable get() = (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) &&
            (currentTopLevelDestination != null)

    val shouldShowNavRail: Boolean
        @Composable get() = (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) &&
            (currentTopLevelDestination != null)

    val shouldShowMonthSelector: Boolean
        @Composable get() = (currentTopLevelDestination != null) &&
            currentTopLevelDestination != TopLevelDestination.SUBSCRIPTIONS

    val shouldShowFab: Boolean
        @Composable get() = currentTopLevelDestination == TopLevelDestination.EXPENSES ||
            currentTopLevelDestination == TopLevelDestination.BUDGETS

    val isOffline = networkUtils.currentState
        .map { it != NetworkState.CONNECTED }
        .stateInDelayed(false, coroutineScope)

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val topLevelDestinationsWithUnreadResources: StateFlow<Set<TopLevelDestination>> =
        // TODO: Requires Implementation
        MutableStateFlow(setOf<TopLevelDestination>()).asStateFlow()

    fun navigateToEditExpenseScreen() {
        navController.navigateToEditExpenseScreen(0L)
    }

    fun navigateToEditBudgetScreen() {
        navController.navigateToEditBudgetScreen()
    }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.EXPENSES -> navController.navigateToExpensesNavGraph(
                topLevelNavOptions,
            )

            TopLevelDestination.ANALYSIS -> navController.navigateToAnalysis(topLevelNavOptions)
            TopLevelDestination.BUDGETS -> navController.navigateToBudgetsNavGraph(
                topLevelNavOptions,
            )

            TopLevelDestination.SUBSCRIPTIONS -> navController.navigateToSubscriptions(
                topLevelNavOptions,
            )

            TopLevelDestination.CHAT -> navController.navigateToChat(topLevelNavOptions)
        }
    }
}
