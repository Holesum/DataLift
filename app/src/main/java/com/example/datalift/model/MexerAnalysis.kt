package com.example.datalift.model

import com.google.firebase.Timestamp

data class AnalysisExer(
    val date: Timestamp = Timestamp.now(),
    val progressionMultiplier: Double = 0.0,
    val workoutId: String = ""
){

}

data class MexerAnalysis(
    val bodyPart: String = "",
    val initialAvgORM: Double = 0.0,
    val progression: List<AnalysisExer> = emptyList()
)
