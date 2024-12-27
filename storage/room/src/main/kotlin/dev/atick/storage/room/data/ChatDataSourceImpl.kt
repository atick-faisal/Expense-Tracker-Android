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

import dev.atick.storage.room.dao.ChatDao
import dev.atick.storage.room.models.ChatEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of the ChatDataSource interface for accessing and managing chat messages.
 *
 * @property chatDao The DAO for accessing chat data.
 */
class ChatDataSourceImpl @Inject constructor(
    private val chatDao: ChatDao,
) : ChatDataSource {

    /**
     * Retrieves all chat messages.
     *
     * @return A Flow emitting a list of ChatEntity objects representing all chat messages.
     */
    override fun getAllMessages(): Flow<List<ChatEntity>> = chatDao.getAllMessages()

    /**
     * Retrieves a limited number of recent chat messages.
     *
     * @param limit The maximum number of recent messages to retrieve.
     * @return A list of ChatEntity objects representing the recent chat messages.
     */
    override suspend fun getRecentMessages(limit: Int): List<ChatEntity> {
        return chatDao.getRecentMessages(limit)
    }

    /**
     * Inserts a new chat message.
     *
     * @param chatEntity The ChatEntity object representing the message to be inserted.
     */
    override suspend fun insertMessage(chatEntity: ChatEntity) = chatDao.insertMessage(chatEntity)

    /**
     * Deletes a specific chat message.
     *
     * @param chatEntity The ChatEntity object representing the message to be deleted.
     */
    override suspend fun deleteMessage(chatEntity: ChatEntity) = chatDao.deleteMessage(chatEntity)

    /**
     * Deletes all chat messages.
     */
    override suspend fun deleteAllMessages() = chatDao.deleteAllMessages()
}
