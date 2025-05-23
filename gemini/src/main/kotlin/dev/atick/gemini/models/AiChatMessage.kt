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

package dev.atick.gemini.models

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content

/**
 * Data class representing a chat message.
 * @param text The text of the message.
 * @param sender The sender of the message.
 */
data class AiChatMessage(
    val text: String,
    val sender: AiChatSender,
)

/**
 * Converter function to convert an [AiChatMessage] to a [Content].
 * @return The converted [Content].
 */
fun AiChatMessage.toGeminiContent(): Content {
    return return content(
        role = if (sender == AiChatSender.USER) "user" else "model",
    ) { text(text) }
}

/**
 * Converter function to convert a list of [AiChatMessage] to a list of [Content].
 */
fun List<AiChatMessage>.toGeminiContents(): List<Content> {
    return map { it.toGeminiContent() }
}
