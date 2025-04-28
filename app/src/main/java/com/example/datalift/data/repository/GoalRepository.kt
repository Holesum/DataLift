package com.example.datalift.data.repository

import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Mworkout

interface GoalRepository {
    fun createGoal(uid: String, goal: Mgoal, callback: (Mgoal?) -> Unit)
    fun getGoalsForUser(uid: String, callback: (List<Mgoal>) -> Unit)
    fun deleteGoal(uid: String, goal: Mgoal, callback: (Boolean) -> Unit)
    fun evaluateGoals(uid: String, exerciseAnalysis: List<MexerAnalysis>, workouts: List<Mworkout>, callback: (Boolean) -> Unit)
}