package dev.atick.compose.ui.analysis

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.data.analysis.AnalysisScreenData
import dev.atick.compose.repository.analysis.AnalysisRepository
import dev.atick.core.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository,
) : ViewModel() {
    private val _analysisUiState = MutableStateFlow(UiState(AnalysisScreenData()))
    val analysisUiState = _analysisUiState.asStateFlow()
}