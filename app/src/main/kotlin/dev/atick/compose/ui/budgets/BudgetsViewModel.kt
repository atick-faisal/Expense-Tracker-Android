package dev.atick.compose.ui.budgets

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.budgets.BudgetsScreenData
import dev.atick.compose.repository.budgets.BudgetsRepository
import dev.atick.core.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val budgetsRepository: BudgetsRepository,
) : ViewModel() {
    private val _budgetsUiState = MutableStateFlow(UiState(BudgetsScreenData()))
    val budgetsUiState = _budgetsUiState.asStateFlow()
}