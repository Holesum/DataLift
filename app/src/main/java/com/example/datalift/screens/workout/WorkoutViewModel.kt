package com.example.datalift.screens.workout

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.datalift.model.Mworkout

class WorkoutViewModel() : ViewModel() {
    private val _workouts = getWorkoutList().toMutableStateList()

    val workouts: List<Mworkout>
        get() = _workouts

    fun remove(item: Mworkout){
        _workouts.remove(item)
    }

    fun add(item: Mworkout){
        _workouts.add(item)
    }
}

private fun getWorkoutList() = List(size = 10) {
        i -> Mworkout("Workout #$i","Back",emptyList())
}