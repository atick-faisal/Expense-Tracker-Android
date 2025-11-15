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

package dev.atick.compose.ui.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.compose.data.subscriptions.SubscriptionsScreenData
import dev.atick.compose.ui.common.ExpenseCard
import dev.atick.compose.ui.common.Placeholder
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun SubscriptionsRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    subscriptionsViewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val subscriptionsUiState by subscriptionsViewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        subscriptionsViewModel.refreshSubscriptions()
    }

    StatefulComposable(
        state = subscriptionsUiState,
        onShowSnackbar = onShowSnackbar,
    ) { categoriesScreenData ->
        SubscriptionsScreen(
            subscriptionsScreenData = categoriesScreenData,
            onRecurringTypeClick = subscriptionsViewModel::setRecurringType,
            onCancellationClick = subscriptionsViewModel::setCancellation,
        )
    }
}

@Composable
private fun SubscriptionsScreen(
    subscriptionsScreenData: SubscriptionsScreenData,
    onRecurringTypeClick: (String, UiRecurringType) -> Unit,
    onCancellationClick: (String, Boolean) -> Unit,
) {
    if (subscriptionsScreenData.subscriptions.isEmpty()) {
        Placeholder(
            title = R.string.no_subscriptions_title,
            description = R.string.no_subscriptions_description,
        )
    } else {
        SubscriptionsList(
            subscriptions = subscriptionsScreenData.subscriptions,
            onRecurringTypeClick = onRecurringTypeClick,
            onCancellationClick = onCancellationClick,
        )
    }
}

@Composable
private fun SubscriptionsList(
    subscriptions: List<UiExpense>,
    onRecurringTypeClick: (String, UiRecurringType) -> Unit,
    onCancellationClick: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(subscriptions, key = { it.id }) { subscription ->
            ExpenseCard(
                expense = subscription,
                onExpenseClick = null,
                onRecurringTypeClick = onRecurringTypeClick,
                onCancellationClick = onCancellationClick,
            )
        }
    }
}
