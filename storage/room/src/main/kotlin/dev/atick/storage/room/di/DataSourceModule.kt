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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.storage.room.data.BudgetDataSource
import dev.atick.storage.room.data.BudgetDataSourceImpl
import dev.atick.storage.room.data.CategoryDataSource
import dev.atick.storage.room.data.CategoryDataSourceImpl
import dev.atick.storage.room.data.ChatDataSource
import dev.atick.storage.room.data.ChatDataSourceImpl
import dev.atick.storage.room.data.ExpenseDataSource
import dev.atick.storage.room.data.ExpenseDataSourceImpl
import dev.atick.storage.room.data.LocalDataSource
import dev.atick.storage.room.data.LocalDataSourceImpl
import javax.inject.Singleton

/**
 * Dagger Hilt module responsible for providing implementations of data source interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /**
     * Binds the [LocalDataSourceImpl] implementation to the [LocalDataSource] interface.
     *
     * @param localDataSourceImpl The concrete implementation of [LocalDataSourceImpl].
     * @return An instance of [LocalDataSource] representing the local data source.
     */
    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl,
    ): LocalDataSource

    /**
     * Binds the [ChatDataSourceImpl] implementation to the [ChatDataSource] interface.
     *
     * @param chatDataSourceImpl The concrete implementation of [ChatDataSourceImpl].
     * @return An instance of [ChatDataSource] representing the chat local data source.
     */
    @Binds
    @Singleton
    abstract fun bindChatDataSource(
        chatDataSourceImpl: ChatDataSourceImpl,
    ): ChatDataSource

    /**
     * Binds the [ExpenseDataSourceImpl] implementation to the [ExpenseDataSource] interface.
     *
     * @param expenseDataSourceImpl The concrete implementation of [ExpenseDataSourceImpl].
     * @return An instance of [ExpenseDataSource] representing the expense data source.
     */
    @Binds
    @Singleton
    abstract fun bindExpenseDataSource(
        expenseDataSourceImpl: ExpenseDataSourceImpl,
    ): ExpenseDataSource

    /**
     * Binds the [CategoryDataSourceImpl] implementation to the [CategoryDataSource] interface.
     *
     * @param categoryDataSourceImpl The concrete implementation of [CategoryDataSourceImpl].
     * @return An instance of [CategoryDataSource] representing the category data source.
     */
    @Binds
    @Singleton
    abstract fun bindCategoryDataSource(
        categoryDataSourceImpl: CategoryDataSourceImpl,
    ): CategoryDataSource

    /**
     * Binds the [BudgetDataSourceImpl] implementation to the [BudgetDataSource] interface.
     *
     * @param budgetDataSourceImpl The concrete implementation of [BudgetDataSourceImpl].
     * @return An instance of [BudgetDataSource] representing the budget data source.
     */
    @Binds
    @Singleton
    abstract fun bindBudgetDataSource(
        budgetDataSourceImpl: BudgetDataSourceImpl,
    ): BudgetDataSource
}
