package dev.atick.compose.ui.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.categories.CategoriesScreenData
import dev.atick.compose.repository.categories.CategoriesRepository
import dev.atick.core.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {
    private val _categoriesUiState = MutableStateFlow(UiState(CategoriesScreenData()))
    val categoriesUiState = _categoriesUiState.asStateFlow()
}