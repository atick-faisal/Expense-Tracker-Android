///*
// * Copyright 2024 Atick Faisal
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package dev.atick.storage.room.data
//
//import dev.atick.core.di.IoDispatcher
//import dev.atick.storage.room.dao.BudgetDao
//import dev.atick.storage.room.dao.CategoryDao
//import dev.atick.storage.room.dao.ExpenseDao
//import dev.atick.storage.room.models.ExpenseAnalysis
//import dev.atick.storage.room.models.VendorAnalysis
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.emitAll
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.map
//import javax.inject.Inject
//
//class AnalysisDataSourceImpl @Inject constructor(
//    private val expenseDao: ExpenseDao,
//    private val budgetDao: BudgetDao,
//    private val categoryDao: CategoryDao,
//    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
//) : AnalysisDataSource {
//
//    override fun getCategoryAnalyses(startDate: Long, endDate: Long): Flow<List<ExpenseAnalysis>> =
//        flow {
//            categoryDao.getAllCategories().collect { categories ->
//                val analyses = categories.map { category ->
//                    combine(
//                        expenseDao.getCategorySpending(category.type, startDate, endDate),
//                        budgetDao.getBudgetForCategory(category.type),
//                    ) { spending, budget ->
//                        ExpenseAnalysis(
//                            spending = spending ?: 0.0,
//                            budget = budget?.amount ?: 0.0,
//                            category = category,
//                            description = budget?.description,
//                        )
//                    }
//                }
//                emitAll(combine(analyses) { it.toList() })
//            }
//        }.flowOn(ioDispatcher)
//
//    override fun getAnalysisByCategory(
//        categoryType: String,
//        startDate: Long,
//        endDate: Long,
//    ): Flow<ExpenseAnalysis?> = combine(
//        expenseDao.getCategorySpending(categoryType, startDate, endDate),
//        budgetDao.getBudgetForCategory(categoryType),
//        categoryDao.getCategoryByType(categoryType),
//    ) { spending, budget, category ->
//        category?.let {
//            ExpenseAnalysis(
//                spending = spending ?: 0.0,
//                budget = budget?.amount ?: 0.0,
//                category = it,
//                description = budget?.description,
//            )
//        }
//    }.flowOn(ioDispatcher)
//
//    override fun getVendorAnalyses(
//        startDate: Long,
//        endDate: Long,
//    ): Flow<List<VendorAnalysis>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAnalysisByVendor(
//        vendor: String,
//        startDate: Long,
//        endDate: Long,
//    ): Flow<VendorAnalysis?> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double> {
//        return expenseDao.getAllExpenses()
//            .map { expenses ->
//                expenses.filter {
//                    it.paymentDate in startDate..endDate
//                }.sumOf { it.amount }
//            }
//            .flowOn(ioDispatcher)
//    }
//
//    override fun getTotalBudget(startDate: Long, endDate: Long): Flow<Double> =
//        budgetDao.getAllBudgets()
//            .map { budgets ->
//                budgets.filter {
//                    it.startDate <= endDate && it.endDate >= startDate
//                }.sumOf { it.amount }
//            }
//            .flowOn(ioDispatcher)
//}
