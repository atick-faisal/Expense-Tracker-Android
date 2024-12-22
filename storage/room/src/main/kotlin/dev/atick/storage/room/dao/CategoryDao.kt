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
//
// import androidx.room.Dao
// import androidx.room.Delete
// import androidx.room.Insert
// import androidx.room.OnConflictStrategy
// import androidx.room.Query
// import dev.atick.storage.room.models.CategoryEntity
// import kotlinx.coroutines.flow.Flow
//
// @Dao
// interface CategoryDao {
//    @Query("SELECT * FROM categories")
//    fun getAllCategories(): Flow<List<CategoryEntity>>
//
//    @Query("SELECT * FROM categories WHERE type = :type")
//    fun getCategoryByType(type: String): Flow<CategoryEntity?>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertCategory(category: CategoryEntity)
//
//    @Delete
//    suspend fun deleteCategory(category: CategoryEntity)
// }
