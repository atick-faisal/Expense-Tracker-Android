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
import androidx.room.ForeignKey

@Entity(
    tableName = "expense_category_cross_ref",
    primaryKeys = ["expenseId", "categoryType"],
    foreignKeys = [
        ForeignKey(
            entity = ExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["type"],
            childColumns = ["categoryType"],
        ),
    ],
)
data class ExpenseCategoryEntity(
    val expenseId: Long,
    val categoryType: String,
)
