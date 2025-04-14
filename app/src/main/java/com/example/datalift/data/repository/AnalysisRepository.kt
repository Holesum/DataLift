package com.example.datalift.data.repository

import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis

interface AnalysisRepository {
    fun getWorkoutProgression(uid: String, callback: (List<Manalysis>) -> Unit)
    fun getAnalyzedExercises(uid: String, callback: (List<MexerAnalysis>) -> Unit)
}