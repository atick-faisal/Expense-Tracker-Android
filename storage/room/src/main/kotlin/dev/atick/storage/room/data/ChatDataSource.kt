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

package dev.atick.storage.room.data

import dev.atick.storage.room.models.ChatEntity
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {
    fun getAllMessages(): Flow<List<ChatEntity>>
    suspend fun getRecentMessages(limit: Int): List<ChatEntity>
    suspend fun insertMessage(chatEntity: ChatEntity)
    suspend fun deleteMessage(chatEntity: ChatEntity)
    suspend fun deleteAllMessages()
}
