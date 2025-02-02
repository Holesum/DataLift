package com.example.datalift.screens.workout

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mworkout
import com.example.datalift.model.workoutRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.datalift.model.Mset
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class WorkoutViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val workoutRepo = workoutRepo()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    // Workouts state
    private val _workouts = MutableStateFlow<List<Mworkout>>(emptyList())
    val workouts: StateFlow<List<Mworkout>> get() = _workouts

    // Single workout state
    private val _workout = MutableStateFlow<Mworkout?>(null)
    val workout: StateFlow<Mworkout?> get() = _workout

    // Exercises state
    private val _exercises = MutableStateFlow<List<ExerciseItem>>(emptyList())
    val exercises: StateFlow<List<ExerciseItem>> get() = _exercises

    // Workout fetched state
    private val _workoutFetched = MutableStateFlow(false)
    val workoutFetched: StateFlow<Boolean> get() = _workoutFetched

    //Exercise fetched state
    private val _exerciseFetched = MutableStateFlow(false)
    val exerciseFetched: StateFlow<Boolean> get() = _exerciseFetched

    private val _workoutsFetched = MutableStateFlow(false)
    val workoutsFetched: StateFlow<Boolean> get() = _workoutsFetched

    /*init {
        if (!_workoutFetched.value) {
            getWorkouts()
            _workoutFetched.value = true
        }
    }*/

    fun addSet(exercise: Mexercise, set: Mset) {

    }

    fun passWorkout(workout: Mworkout){
        _workout.value = workout
    }

    fun add(item: Mworkout){
        _workouts.value += item
    }


    fun getWorkoutList() = List(size = 10) {
        i -> Mworkout("Workout #$i",
        date = Timestamp.now(),
        "Back",
        "temp$i",
        emptyList())
    }


    /**
     * Function to get search exercise in existing list of exercises
     */
    fun getExercises(query: String = ""){
        _loading.value = true
        workoutRepo.getExercises(query) { exerciseList ->
            _exercises.value = exerciseList
            Log.d("Firebase", "Exercises found: ${exerciseList.size}")
            _loading.value = false
        }
    }
    /**
     * Function to create a new workout object
     *
     * takes a completed MWorkout object from the UI and then sends it to the database
     */
   fun createNewWorkout(workout: Mworkout) {
        if(!_loading.value) {
            _loading.value = true
            try{
                workoutRepo.createNewWorkout(oRM(workout), uid)
                _loading.value = false

            } catch (e: Exception) {
                Log.d("Firebase", "Error creating workout: ${e.message}")
                _loading.value = false
            }
        }
    }


    /**
     * Function to edit an existing workout
     */
    fun editWorkout(workout: Mworkout) {
        _loading.value = true
        workoutRepo.editWorkout(oRM(workout), uid)
        _loading.value = false
    }

    /**
     * Function to provide ORM analysis for each set in workout
     */
    private fun oRM(workout: Mworkout) : Mworkout {
        for(exercise in workout.exercises){
            for(set in exercise.sets){
                set.setORM()
            }
        }
        return workout
    }

    /**
     * Function to delete an existing workout
     */
    fun deleteWorkout(workout: Mworkout) {
        _loading.value = true
        _workouts.value -= workout
        workoutRepo.deleteWorkout(workout, uid)
        _loading.value = false
    }

    /**
     * Function to get workout list from database for specific user
     */
    fun getWorkouts() {
        _workoutsFetched.value = true
        _loading.value = true
        workoutRepo.getWorkouts(uid) { workoutList ->
            _workouts.value = workoutList
            _workoutFetched.value = true
            _loading.value = false
        }
    }

    /**
     * Function to get workout details from database with ID
     * @param id: ID of workout to get
     *
     */
    fun getWorkout(id: String) {
        _loading.value = true
        workoutRepo.getWorkout(uid, id) { workout ->
            _workout.value = workout
            _loading.value = false
        }
    }
}