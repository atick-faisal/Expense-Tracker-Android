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

package dev.atick.storage.room.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.atick.storage.room.data.BudgetDatabase
import dev.atick.storage.room.data.CategoryDatabase
import dev.atick.storage.room.data.CategoryDatabaseCallback
import dev.atick.storage.room.data.ChatDatabase
import dev.atick.storage.room.data.ExpenseDatabase
import dev.atick.storage.room.data.JetpackDatabase
import javax.inject.Singleton

/**
 * Dagger module for database.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val JETPACK_DATABASE_NAME = "dev.atick.jetpack.room"
    private const val EXPENSE_DATABASE_NAME = "dev.atick.expense.room"
    private const val CATEGORY_DATABASE_NAME = "dev.atick.category.room"
    private const val BUDGET_DATABASE_NAME = "dev.atick.budget.room"
    private const val CHAT_DATABASE_NAME = "dev.atick.chat.room"

    /**
     * Get the database for Jetpack.
     *
     * @param appContext The application context.
     * @return The database for Jetpack.
     */
    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext appContext: Context,
    ): JetpackDatabase {
        return Room.databaseBuilder(
            appContext,
            JetpackDatabase::class.java,
            JETPACK_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Get the database for Expense.
     *
     * @param appContext The application context.
     * @return The database for Expense.
     */
    @Singleton
    @Provides
    fun provideExpenseDatabase(
        @ApplicationContext appContext: Context,
    ): ExpenseDatabase {
        return Room.databaseBuilder(
            appContext,
            ExpenseDatabase::class.java,
            EXPENSE_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Get the database for Category.
     *
     * @param appContext The application context.
     * @param callback The callback for Category database.
     * @return The database for Category.
     */
    @Singleton
    @Provides
    fun provideCategoryDatabase(
        @ApplicationContext appContext: Context,
        callback: CategoryDatabaseCallback,
    ): CategoryDatabase {
        return Room.databaseBuilder(
            appContext,
            CategoryDatabase::class.java,
            CATEGORY_DATABASE_NAME,
        ).addCallback(callback).fallbackToDestructiveMigration().build()
    }

    /**
     * Get the database for Budget.
     *
     * @param appContext The application context.
     * @return The database for Budget.
     */
    @Singleton
    @Provides
    fun provideBudgetDatabase(
        @ApplicationContext appContext: Context,
    ): BudgetDatabase {
        return Room.databaseBuilder(
            appContext,
            BudgetDatabase::class.java,
            BUDGET_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Get the database for Chat.
     *
     * @param appContext The application context.
     * @return The database for Chat.
     */
    @Singleton
    @Provides
    fun provideChatDatabase(
        @ApplicationContext appContext: Context,
    ): ChatDatabase {
        return Room.databaseBuilder(
            appContext,
            ChatDatabase::class.java,
            CHAT_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }
}
