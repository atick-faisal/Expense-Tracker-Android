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

package dev.atick.storage.room.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.atick.storage.room.dao.BudgetDao
import dev.atick.storage.room.dao.ChatDao
import dev.atick.storage.room.dao.ExpenseDao
import dev.atick.storage.room.models.BudgetEntity
import dev.atick.storage.room.models.ChatEntity
import dev.atick.storage.room.models.ExpenseEntity

/**
 * Room database for Expense.
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = [
        ExpenseEntity::class,
    ],
)
abstract class ExpenseDatabase : RoomDatabase() {
    /**
     * Get the data access object for [ExpenseEntity] entity.
     *
     * @return The data access object for [ExpenseEntity] entity.
     */
    abstract fun getExpenseDao(): ExpenseDao
}

/**
 * Room database for Budget.
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = [
        BudgetEntity::class,
    ],
)
abstract class BudgetDatabase : RoomDatabase() {
    /**
     * Get the data access object for [BudgetEntity] entity.
     *
     * @return The data access object for [BudgetEntity] entity.
     */
    abstract fun getBudgetDao(): BudgetDao
}

/**
 * Room database for Chat.
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = [
        ChatEntity::class,
    ],
)
abstract class ChatDatabase : RoomDatabase() {
    /**
     * Get the data access object for [ChatEntity] entity.
     *
     * @return The data access object for [ChatEntity] entity.
     */
    abstract fun getChatDao(): ChatDao
}
