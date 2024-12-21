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
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.atick.storage.room.models.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE createdAt BETWEEN :startDate AND :endDate")
    fun getAllBudgets(startDate: Long, endDate: Long): Flow<List<BudgetEntity>>

    @Query(
        """
        SELECT * FROM budgets 
        WHERE isMerchant = 0 
        AND createdAt BETWEEN :startDate AND :endDate
        """,
    )
    fun getCategoryBudgets(startDate: Long, endDate: Long): Flow<List<BudgetEntity>>

    @Query(
        """
        SELECT * FROM budgets 
        WHERE isMerchant = 1 
        AND createdAt BETWEEN :startDate AND :endDate
        """,
    )
    fun getMerchantBudgets(startDate: Long, endDate: Long): Flow<List<BudgetEntity>>

    @Query(
        """
        SELECT * FROM budgets 
        WHERE categoryOrMerchantName = :categoryOrMerchantName 
        AND isMerchant = :isMerchant
        AND createdAt BETWEEN :startDate AND :endDate
    """,
    )
    fun getBudgetFor(
        categoryOrMerchantName: String,
        isMerchant: Boolean,
        startDate: Long,
        endDate: Long,
    ): Flow<BudgetEntity?>

    @Query("SELECT SUM(amount) FROM budgets WHERE createdAt BETWEEN :startDate AND :endDate")
    fun getTotalBudget(startDate: Long, endDate: Long): Flow<Double?>

    @Query(
        """
        SELECT SUM(amount) FROM budgets 
        WHERE isMerchant = 0 
        AND createdAt BETWEEN :startDate AND :endDate
        """,
    )
    fun getTotalCategoryBudget(startDate: Long, endDate: Long): Flow<Double?>

    @Query(
        """
        SELECT SUM(amount) FROM budgets 
        WHERE isMerchant = 1 
        AND createdAt BETWEEN :startDate AND :endDate
        """,
    )
    fun getTotalMerchantBudget(startDate: Long, endDate: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBudgets(budgets: List<BudgetEntity>)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)
}
