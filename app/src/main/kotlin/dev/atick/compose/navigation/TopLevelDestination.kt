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

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import dev.atick.compose.R
import dev.atick.compose.navigation.analysis.Analysis
import dev.atick.compose.navigation.budgets.Budgets
import dev.atick.compose.navigation.categories.Categories
import dev.atick.compose.navigation.chat.Chat
import dev.atick.compose.navigation.expenses.Expenses
import kotlin.reflect.KClass

/**
 * Enum class representing top-level destinations in a navigation system.
 *
 * @property selectedIcon The selected icon associated with the destination.
 * @property unselectedIcon The unselected icon associated with the destination.
 * @property iconTextId The resource ID for the icon's content description text.
 * @property titleTextId The resource ID for the title text.
 * @property route The route associated with the destination.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
) {
    EXPENSES(
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt,
        iconTextId = R.string.expenses,
        titleTextId = R.string.expenses,
        route = Expenses::class,
    ),
    ANALYSIS(
        selectedIcon = Icons.Filled.PieChart,
        unselectedIcon = Icons.Outlined.PieChart,
        iconTextId = R.string.analysis,
        titleTextId = R.string.analysis,
        route = Analysis::class,
    ),
    BUDGETS(
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate,
        iconTextId = R.string.budgets,
        titleTextId = R.string.budgets,
        route = Budgets::class,
    ),
    CATEGORIES(
        selectedIcon = Icons.Filled.Tag,
        unselectedIcon = Icons.Outlined.Tag,
        iconTextId = R.string.categories,
        titleTextId = R.string.categories,
        route = Categories::class,
    ),
    CHAT(
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        iconTextId = R.string.chat,
        titleTextId = R.string.chat,
        route = Chat::class,
    )
}
