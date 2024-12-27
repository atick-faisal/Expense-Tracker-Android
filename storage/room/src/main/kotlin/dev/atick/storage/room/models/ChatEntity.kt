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

package dev.atick.storage.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a chat message in the database.
 *
 * @property id The unique identifier for the chat message, auto-generated.
 * @property text The content of the chat message.
 * @property isFromUser A flag indicating whether the message is from the user.
 * @property timestamp The timestamp of when the message was created, in milliseconds since epoch.
 */
@Entity(tableName = "messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isFromUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
)
