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
import androidx.navigation.compose.composable
import dev.atick.compose.ui.budgets.EditBudgetRoute
import dev.atick.core.utils.MonthInfo
import kotlinx.serialization.Serializable

@Serializable
data object EditBudget

fun NavController.navigateToEditBudgetScreen() {
    navigate(EditBudget) { launchSingleTop = true }
}

fun NavGraphBuilder.editBudgetScreen(
    monthInfo: MonthInfo,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<EditBudget> {
        EditBudgetRoute(
            monthInfo = monthInfo,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
