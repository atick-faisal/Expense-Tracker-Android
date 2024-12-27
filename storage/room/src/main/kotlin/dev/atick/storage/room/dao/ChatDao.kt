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

package dev.atick.storage.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.atick.storage.room.models.ChatEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the messages table.
 */
@Dao
interface ChatDao {
    /**
     * Returns a [Flow] of [ChatEntity] representing all the messages in the messages table.
     *
     * @return A [Flow] of [ChatEntity] representing all the messages in the messages table.
     */
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatEntity>>

    /**
     * Returns a [Flow] of [ChatEntity] representing the message with the given ID.
     *
     * @param id The ID of the message to be retrieved.
     * @return A [Flow] of [ChatEntity] representing the message with the given ID.
     */
    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int): List<ChatEntity>

    /**
     * Inserts the given [ChatEntity] in the messages table.
     *
     * @param chatEntity The [ChatEntity] to be inserted.
     */
    @Insert
    suspend fun insertMessage(chatEntity: ChatEntity)

    /**
     * Deletes the given [ChatEntity] from the messages table.
     *
     * @param chatEntity The [ChatEntity] to be deleted.
     */
    @Delete
    suspend fun deleteMessage(chatEntity: ChatEntity)

    /**
     * Deletes all the messages from the messages table.
     */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}
