package dev.atick.compose.ui.expenses

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.expenses.ExpensesScreenData
import dev.atick.compose.repository.expenses.ExpensesRepository
import dev.atick.core.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
) : ViewModel() {
    private val _expensesUiState = MutableStateFlow(UiState(ExpensesScreenData()))
    val expensesUiState = _expensesUiState.asStateFlow()
}