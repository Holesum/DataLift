package com.example.datalift.screens.workout

import android.util.Log
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


class WorkoutViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _workouts = MutableLiveData<List<Mworkout>>()
    val workouts: LiveData<List<Mworkout>> = _workouts

    private val _workout = MutableLiveData<Mworkout>()
    val workout: LiveData<Mworkout> = _workout

    private val _exercises = MutableLiveData<List<ExerciseItem>>()
    val exercises: LiveData<List<ExerciseItem>> = _exercises

    private val _workoutFetched = MutableLiveData<Boolean>(false)
    val workoutFetched: LiveData<Boolean> = _workoutFetched

    private val workoutRepo = workoutRepo()

    init{
        if(_workoutFetched.value == false) {
            getWorkouts()
        }
    }

    /**
     * Function to get search exercise in existing list of exercises
     */
    fun getExercises(){
        _loading.value = true
        workoutRepo.getExercises { exerciseList ->
            _exercises.value = exerciseList
            _loading.value = false
        }
    }
    /**
     * Function to create a new workout object
     *
     * takes a completed MWorkout object from the UI and then sends it to the database
     */
   fun createNewWorkout(workout: Mworkout) {
        if(_loading.value == false) {
            _loading.value = true
            try{
                workoutRepo.createNewWorkout(workout, uid)
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
        workoutRepo.editWorkout(workout, uid)
        _loading.value = false
    }

    /**
     * Function to delete an existing workout
     */
    fun deleteWorkout(workout: Mworkout) {
        _loading.value = true
        workoutRepo.deleteWorkout(workout, uid)
        _loading.value = false
    }

    /**
     * Function to get workout list from database for specific user
     */
    fun getWorkouts() {
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