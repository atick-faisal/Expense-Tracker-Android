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

package dev.atick.compose.navigation.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.atick.compose.ui.chat.ChatRoute
import dev.atick.core.utils.MonthInfo
import kotlinx.serialization.Serializable

@Serializable
data object Chat

fun NavController.navigateToChat(navOptions: NavOptions?) {
    navigate(Chat, navOptions)
}

fun NavGraphBuilder.chatScreen(
    monthInfo: MonthInfo,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Chat> {
        ChatRoute(
            monthInfo = monthInfo,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
