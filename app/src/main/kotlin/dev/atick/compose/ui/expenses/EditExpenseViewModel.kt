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

package dev.atick.compose.ui.expenses

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.categories.UiCategoryType
import dev.atick.compose.data.expenses.UiExpense
import dev.atick.compose.data.expenses.UiPaymentStatus
import dev.atick.compose.data.expenses.UiRecurringType
import dev.atick.core.ui.utils.UiState
import dev.atick.core.ui.utils.updateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditExpenseViewModel @Inject constructor() : ViewModel() {
    private val _expenseUiState = MutableStateFlow(UiState(UiExpense()))
    val expenseUiState = _expenseUiState.asStateFlow()

    fun setAmount(amount: String) {
        val amountParsed = amount.toDoubleOrNull() ?: 0.0
        _expenseUiState.updateState { copy(amount = amountParsed) }
    }

    fun setCategory(category: UiCategoryType) {
        _expenseUiState.updateState { copy(category = category) }
    }

    fun setPaymentStatus(paymentStatus: UiPaymentStatus) {
        _expenseUiState.updateState { copy(paymentStatus = paymentStatus) }
    }

    fun setRecurringType(recurringType: UiRecurringType) {
        _expenseUiState.updateState { copy(recurringType = recurringType) }
    }

    fun setPaymentDate(paymentDate: Long) {
        _expenseUiState.updateState { copy(paymentDate = paymentDate) }
    }

    fun setDueDate(dueDate: Long) {
        _expenseUiState.updateState { copy(dueDate = dueDate) }
    }

    fun setDescription(description: String) {
        _expenseUiState.updateState { copy(merchant = description) }
    }

    fun setToBeCancelled(toBeCancelled: Boolean) {
        _expenseUiState.updateState { copy(toBeCancelled = toBeCancelled) }
    }

    fun saveExpense() {
        // Save the expense
    }
}
