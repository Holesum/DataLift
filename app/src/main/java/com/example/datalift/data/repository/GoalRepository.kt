package com.example.datalift.data.repository

import com.example.datalift.model.Mgoal

interface GoalRepository {
    fun createGoal(uid: String, goal: Mgoal, callback: (Mgoal?) -> Unit)
    fun getGoalsForUser(uid: String, callback: (List<Mgoal>) -> Unit)
}