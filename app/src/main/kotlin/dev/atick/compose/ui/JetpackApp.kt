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

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateBefore
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.atick.compose.R
import dev.atick.compose.navigation.JetpackNavHost
import dev.atick.compose.navigation.TopLevelDestination
import dev.atick.compose.ui.settings.SettingsDialog
import dev.atick.core.ui.components.AppBackground
import dev.atick.core.ui.components.AppGradientBackground
import dev.atick.core.ui.components.JetpackNavigationBar
import dev.atick.core.ui.components.JetpackNavigationBarItem
import dev.atick.core.ui.components.JetpackNavigationRail
import dev.atick.core.ui.components.JetpackNavigationRailItem
import dev.atick.core.ui.components.JetpackTextButton
import dev.atick.core.ui.components.JetpackTopAppBar
import dev.atick.core.ui.theme.GradientColors
import dev.atick.core.ui.theme.LocalGradientColors
import dev.atick.core.utils.getMonthInfoAt
import dev.atick.network.utils.NetworkUtils

@Composable
fun JetpackApp(
    userOnboarded: Boolean,
    windowSizeClass: WindowSizeClass,
    networkUtils: NetworkUtils,
    appState: JetpackAppState = rememberJetpackAppState(
        userOnboarded = userOnboarded,
        windowSizeClass = windowSizeClass,
        networkUtils = networkUtils,
    ),
) {
    val shouldShowGradientBackground =
        appState.currentTopLevelDestination == TopLevelDestination.EXPENSES
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    var monthOffset by rememberSaveable { mutableIntStateOf(0) }
    val monthInfo by remember(monthOffset) {
        mutableStateOf(getMonthInfoAt(monthOffset))
    }

    AppBackground {
        AppGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val unreadDestinations by appState.topLevelDestinationsWithUnreadResources.collectAsStateWithLifecycle()
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            // If user is not connected to the internet show a snack bar to inform them.
            val notConnectedMessage = stringResource(R.string.not_connected)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = SnackbarDuration.Indefinite,
                    )
                }
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    onDismiss = { showSettingsDialog = false },
                )
            }

            val fabIcon: ImageVector = when (appState.currentTopLevelDestination) {
                TopLevelDestination.EXPENSES -> Icons.Default.Edit
                TopLevelDestination.BUDGETS -> Icons.Default.AddChart
                else -> Icons.Default.Add
            }

            val fabLabel = when (appState.currentTopLevelDestination) {
                TopLevelDestination.EXPENSES -> R.string.add_expense
                TopLevelDestination.BUDGETS -> R.string.add_budget
                else -> R.string.add
            }

            val fabAction = when (appState.currentTopLevelDestination) {
                TopLevelDestination.EXPENSES -> appState::navigateToEditExpenseScreen
                TopLevelDestination.BUDGETS -> appState::navigateToEditBudgetScreen
                else -> appState::navigateToEditExpenseScreen
            }

            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                // Snackbar displays incorrectly if used
                // contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        JetpackBottomBar(
                            destinations = appState.topLevelDestinations,
                            destinationsWithUnreadResources = emptySet(),
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                        )
                    }
                },
                floatingActionButton = {
                    if (appState.shouldShowFab) {
                        JetpackFloatingActionButton(
                            icon = fabIcon,
                            text = fabLabel,
                            onClick = fabAction,
                        )
                    }
                },
            ) { padding ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        ),
                ) {
                    if (appState.shouldShowNavRail) {
                        JetpackNavRail(
                            destinations = appState.topLevelDestinations,
                            destinationsWithUnreadResources = unreadDestinations,
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                            modifier = Modifier.safeDrawingPadding(),
                        )
                    }

                    Column(Modifier.fillMaxSize()) {
                        // Show the top app bar on top level destinations.
                        val destination = appState.currentTopLevelDestination
                        if (destination != null) {
                            JetpackTopAppBar(
                                titleRes = destination.titleTextId,
                                navigationIcon = Icons.Default.Menu,
                                navigationIconContentDescription = stringResource(id = R.string.search),
                                actionIcon = Icons.Default.Settings,
                                actionIconContentDescription = stringResource(id = R.string.more),
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent,
                                ),
                                onActionClick = { showSettingsDialog = true },
                                onNavigationClick = { },
                            )
                            AnimatedVisibility(visible = appState.shouldShowMonthSelector) {
                                MonthSelector(
                                    displayMonthYear = "${monthInfo.monthName} ${monthInfo.year}",
                                    onNextMonthClick = { monthOffset++ },
                                    onPreviousMonthClick = { monthOffset-- },
                                )
                            }
                        }
                        JetpackNavHost(
                            appState = appState,
                            monthInfo = monthInfo,
                            onShowSnackbar = { message, action ->
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    actionLabel = action,
                                    duration = SnackbarDuration.Short,
                                ) == SnackbarResult.ActionPerformed
                            },
                        )
                        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                    }
                }
            }
        }
    }
}

@Composable
fun JetpackNavRail(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    JetpackNavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            JetpackNavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.iconTextId)) },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
            )
        }
    }
}

@Composable
fun JetpackBottomBar(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    JetpackNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            JetpackNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        stringResource(destination.iconTextId),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
            )
        }
    }
}

@Composable
fun JetpackFloatingActionButton(
    icon: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = { Icon(icon, stringResource(text)) },
        text = { Text(text = stringResource(text)) },
    )
}

@Composable
fun MonthSelector(
    displayMonthYear: String,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        JetpackTextButton(
            onClick = onPreviousMonthClick,
            text = {
                Icon(
                    Icons.AutoMirrored.Outlined.NavigateBefore,
                    contentDescription = "Next",
                )
            },
        )
        Text(displayMonthYear)
        JetpackTextButton(
            onClick = onNextMonthClick,
            text = {
                Icon(
                    Icons.AutoMirrored.Outlined.NavigateNext,
                    contentDescription = "Before",
                )
            },
        )
    }
}

private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false
