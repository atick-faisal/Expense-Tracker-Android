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

package dev.atick.compose.ui.chat

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.compose.data.chat.ChatScreenData
import dev.atick.compose.data.chat.UiMessage
import dev.atick.core.ui.components.MultilineJetpackTextField
import dev.atick.core.ui.utils.StatefulComposable

@Composable
internal fun ChatRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    chatViewModel: ChatViewModel = hiltViewModel(),
) {
    val chatUiState by chatViewModel.chatUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = chatUiState,
        onShowSnackbar = onShowSnackbar,
    ) { chatScreenData ->
        ChatScreen(
            chatScreenData,
            chatViewModel::onNewMessageUpdate,
            chatViewModel::onSendMessage,
        )
    }
}

@Composable
private fun ChatScreen(
    chatScreenData: ChatScreenData,
    onNewMessageUpdate: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        },
                    )
                },
            reverseLayout = true,
        ) {
            items(chatScreenData.messages.reversed()) { message ->
                ChatMessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        MultilineJetpackTextField(
            value = chatScreenData.newMessage.value,
            onValueChange = onNewMessageUpdate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text(stringResource(id = R.string.type_message)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = stringResource(id = R.string.message),
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSendMessage.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(id = R.string.send_message),
                    )
                }
            },
        )
    }
}

@Composable
fun ChatMessageItem(message: UiMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!message.isFromUser) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Message",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = if (message.isFromUser) 16.dp else 0.dp,
                topEnd = if (message.isFromUser) 0.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp,
            ),
            color = if (message.isFromUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            },
            tonalElevation = 1.dp,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (message.isFromUser) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
            )
        }

        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.Person,
                    contentDescription = "Message",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
