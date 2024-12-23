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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.data.subscriptions.SubscriptionsScreenData
import dev.atick.compose.ui.components.ExpenseCard
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
            onCancellationClick = subscriptionsViewModel::setCancellation,
        )
    }
}

@Composable
private fun SubscriptionsScreen(
    subscriptionsScreenData: SubscriptionsScreenData,
    onCancellationClick: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(subscriptionsScreenData.subscriptions, key = { it.id }) { subscription ->
            ExpenseCard(
                expense = subscription,
                onExpenseClick = {},
                onRecurringTypeClick = null,
                onCancellationClick = onCancellationClick,
            )
        }
    }
}
