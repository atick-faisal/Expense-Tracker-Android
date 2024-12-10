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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.storage.room.data.BudgetDatabase
import dev.atick.storage.room.data.CategoryDatabase
import dev.atick.storage.room.data.ChatDatabase
import dev.atick.storage.room.data.ExpenseDatabase
import dev.atick.storage.room.data.JetpackDatabase
import javax.inject.Singleton

/**
 * Dagger module for data access object.
 */
@Module(
    includes = [
        DatabaseModule::class,
    ],
)
@InstallIn(SingletonComponent::class)
object DaoModule {

    /**
     * Get the data access.
     *
     * @param jetpackDatabase The database for Jetpack.
     * @return The data access object.
     */
    @Singleton
    @Provides
    fun provideJetpackDao(jetpackDatabase: JetpackDatabase) = jetpackDatabase.getJetpackDao()

    /**
     * Get the expense data access.
     *
     * @param expenseDatabase The database for expenses.
     * @return The expense data access object.
     */
    fun provideExpenseDao(expenseDatabase: ExpenseDatabase) = expenseDatabase.getExpenseDao()

    /**
     * Get the budget data access.
     *
     * @param budgetDatabase The database for budgets.
     * @return The budget data access object.
     */
    fun provideBudgetDao(budgetDatabase: BudgetDatabase) = budgetDatabase.getBudgetDao()

    /**
     * Get the category data access.
     *
     * @param categoryDatabase The database for categories.
     * @return The category data access object.
     */
    fun provideCategoryDao(categoryDatabase: CategoryDatabase) = categoryDatabase.getCategoryDao()

    /**
     * Get the chat data access.
     *
     * @param chatDatabase The database for chat messages.
     * @return The chat data access object.
     */
    @Singleton
    @Provides
    fun provideChatDao(chatDatabase: ChatDatabase) = chatDatabase.getChatDao()
}
