package dev.atick.compose.navigation.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.atick.compose.ui.chat.ChatRoute
import kotlinx.serialization.Serializable

@Serializable
data object Chat

fun NavController.navigateToChat(navOptions: NavOptions?) {
    navigate(Chat, navOptions)
}

fun NavGraphBuilder.chatScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<Chat> {
        ChatRoute(
            onShowSnackbar = onShowSnackbar,
        )
    }
}