package com.example.datalift.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.datalift.model.AnalysisExer
import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.ui.PreviewParamterData.mananalysis

class ManalysisPreviewParamaterProvider : PreviewParameterProvider<List<Manalysis>> {
    override val values: Sequence<List<Manalysis>> = sequenceOf(mananalysis)
}

object PreviewParamterData {
    val mananalysis = listOf(
        Manalysis(
            exerciseCount = 15,
            totalProgression = 15.0,
        )
    )
    val exeranalysis = listOf(
        MexerAnalysis(
            bodyPart = "",
            initialAvgORM = 1.0,
            progression = emptyList()
        )
    )
}