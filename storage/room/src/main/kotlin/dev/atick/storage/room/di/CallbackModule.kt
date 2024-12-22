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

package dev.atick.storage.room.di

// import androidx.room.RoomDatabase
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.components.SingletonComponent
// import dev.atick.core.di.ApplicationScope
// import dev.atick.core.di.CoroutineScopeModule
// import dev.atick.storage.room.data.CategoryDataSource
// import dev.atick.storage.room.data.CategoryDatabaseCallback
// import kotlinx.coroutines.CoroutineScope
// import javax.inject.Singleton
//
// /**
// * Dagger module for database callbacks.
// */
// @Module(
//    includes = [
//        CoroutineScopeModule::class,
//        DataSourceModule::class,
//    ],
// )
// @InstallIn(SingletonComponent::class)
// object CallbackModule {
//
//    /**
//     * Provides [CategoryDatabaseCallback].
//     *
//     * @param coroutineScope [CoroutineScope].
//     * @param categoryDataSource [CategoryDataSource].
//     * @return [CategoryDatabaseCallback].
//     */
//    @Provides
//    @Singleton
//    fun provideCategoryDatabaseCallback(
//        @ApplicationScope coroutineScope: CoroutineScope,
//        categoryDataSource: CategoryDataSource,
//    ): RoomDatabase.Callback {
//        return CategoryDatabaseCallback(categoryDataSource, coroutineScope)
//    }
// }
