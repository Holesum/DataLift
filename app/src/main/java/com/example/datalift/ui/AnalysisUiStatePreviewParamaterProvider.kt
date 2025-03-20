package com.example.datalift.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.datalift.screens.analysis.AnalysisUiState
import com.example.datalift.ui.PreviewParamterData.mananalysis
import com.example.datalift.ui.PreviewParamterData.exeranalysis

class AnalysisUiStatePreviewParamaterProvider : PreviewParameterProvider<AnalysisUiState> {
    override val values: Sequence<AnalysisUiState> = sequenceOf(
        AnalysisUiState.Success(
            workoutProgression = mananalysis,
            exerciseAnalysis = exeranalysis,
        )
    )
}