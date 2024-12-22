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
//
// import dev.atick.core.di.IoDispatcher
// import dev.atick.storage.room.dao.CategoryDao
// import dev.atick.storage.room.models.CategoryEntity
// import kotlinx.coroutines.CoroutineDispatcher
// import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.flowOn
// import kotlinx.coroutines.withContext
// import javax.inject.Inject
//
// class CategoryDataSourceImpl @Inject constructor(
//    private val categoryDao: CategoryDao,
//    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
// ) : CategoryDataSource {
//    override fun getAllCategories(): Flow<List<CategoryEntity>> {
//        return categoryDao.getAllCategories().flowOn(ioDispatcher)
//    }
//
//    override fun getCategoryByType(type: String): Flow<CategoryEntity?> {
//        return categoryDao.getCategoryByType(type).flowOn(ioDispatcher)
//    }
//
//    override suspend fun insertCategory(category: CategoryEntity) {
//        withContext(ioDispatcher) {
//            categoryDao.insertCategory(category)
//        }
//    }
//
//    override suspend fun deleteCategory(category: CategoryEntity) {
//        withContext(ioDispatcher) {
//            categoryDao.deleteCategory(category)
//        }
//    }
//
//    override suspend fun insertDefaultCategories() {
//        withContext(ioDispatcher) {
//            categoryDao.insertCategory(CategoryEntity(type = "FOOD", name = "Food"))
//            categoryDao.insertCategory(CategoryEntity(type = "ESSENTIAL", name = "Essential"))
//            categoryDao.insertCategory(CategoryEntity(type = "LIFESTYLE", name = "Lifestyle"))
//            categoryDao.insertCategory(
//                CategoryEntity(
//                    type = "TRANSPORTATION",
//                    name = "Transportation",
//                ),
//            )
//            categoryDao.insertCategory(CategoryEntity(type = "HEALTHCARE", name = "Healthcare"))
//            categoryDao.insertCategory(CategoryEntity(type = "SAVINGS", name = "Savings"))
//            categoryDao.insertCategory(CategoryEntity(type = "DEBT", name = "Debt"))
//            categoryDao.insertCategory(CategoryEntity(type = "EDUCATION", name = "Education"))
//            categoryDao.insertCategory(CategoryEntity(type = "CUSTOM", name = "Custom"))
//        }
//    }
// }
