package com.example.datalift.data.repository

import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mworkout

interface WorkoutRepository {
    fun getWorkout(uid: String, id: String, callback: (Mworkout?) -> Unit)
    fun getWorkouts(uid: String, callback: (List<Mworkout>) -> Unit)
    fun editWorkout(workout: Mworkout, uid: String)
    fun createNewWorkout(workout: Mworkout, uid: String, callback: (Mworkout?) -> Unit)
    fun deleteWorkout(workout: Mworkout, uid: String)
    fun getExercises(query: String = "", callback: (List<ExerciseItem>) -> Unit)
}