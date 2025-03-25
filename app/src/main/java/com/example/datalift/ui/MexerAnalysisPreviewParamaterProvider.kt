package com.example.datalift.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.ui.PreviewParamterData.exeranalysis

class MexerAnalysisPreviewParamaterProvider  : PreviewParameterProvider<List<MexerAnalysis>> {
    override val values: Sequence<List<MexerAnalysis>> = sequenceOf(exeranalysis)
}