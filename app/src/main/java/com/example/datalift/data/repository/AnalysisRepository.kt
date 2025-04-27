package com.example.datalift.data.repository

import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.Mworkout

interface AnalysisRepository {
    fun getWorkoutProgression(uid: String, callback: (List<Manalysis>) -> Unit)
    fun getAnalyzedExercises(uid: String, callback: (List<MexerAnalysis>) -> Unit)
    fun analyzeWorkouts(
        uid: String,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun evaluateGoals(uid: String, exerciseAnalysis: List<MexerAnalysis>, workouts: List<Mworkout>, onComplete: () -> Unit)
}