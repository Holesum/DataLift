package com.example.datalift.screens.workout

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.datalift.data.repository.PostRepository
import com.example.datalift.data.repository.WorkoutRepository

//data models
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mworkout
import com.example.datalift.model.Mset
import com.example.datalift.model.Muser
import com.example.datalift.model.Mpost

//testing imports remove when done
import com.example.datalift.model.userRepo

//Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel

//Compose


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val userRepo: userRepo,
    private val postRepo: PostRepository,
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()

//    //testing repos remove when done
//    private val challengeRepo = challengeRepo()

    private val _dialogUiState = MutableStateFlow(WorkoutDialogUiState())
    val dialogUiState: StateFlow<WorkoutDialogUiState> = _dialogUiState.asStateFlow()

    fun updateDialogWorkoutName(newWorkoutName: String){
        _dialogUiState.update { currentState ->
            currentState.copy(
                workoutName = newWorkoutName,
                workoutNameError = false
            )
        }
    }

    fun updateDialogWorkoutMuscleGroup(newMuscleGroup: String){
        _dialogUiState.update { currentState ->
            currentState.copy(
                muscleGroup = newMuscleGroup,
                muscleGroupError = false
            )
        }
    }

    fun validateDialog(workoutName: String, muscleGroup: String): Boolean{
        val workoutNamePass = validateWorkoutName(workoutName)
        val muscleGroupPass = validateMuscleGroup(muscleGroup)

        return workoutNamePass && muscleGroupPass
    }

    private fun validateWorkoutName(workoutName: String) : Boolean{
        if (workoutName.isNotBlank()){
            return true
        } else {
            _dialogUiState.update { currentState ->
                currentState.copy(
                    workoutNameError = true
                )
            }
            return false
        }
    }

    private fun validateMuscleGroup(muscleGroup: String) : Boolean{
        if(muscleGroup.isNotBlank()){
            return true
        } else {
            _dialogUiState.update { currentState ->
                currentState.copy(
                    muscleGroupError = true
                )
            }
            return false
        }
    }


    private val _posts = MutableStateFlow<List<Mpost>?>(null)
    val posts: StateFlow<List<Mpost>?> get() = _posts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    // Workouts state
    private val _workouts = MutableStateFlow<List<Mworkout>>(emptyList())
    val workouts: StateFlow<List<Mworkout>> get() = _workouts

    // Single workout state
    private val _workout = MutableStateFlow<Mworkout?>(null)
    val workout: StateFlow<Mworkout?> get() = _workout

    // Single set state
    private val _set = MutableStateFlow<Mset?>(null)
    val set: StateFlow<Mset?> get() = _set

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

    private val _user = MutableStateFlow<Muser?>(null)

    private val _users = MutableStateFlow<List<Muser>>(emptyList())



    private val repRegex = Regex("^[0-9]*$")
    private val weightRegex = Regex("^[0-9]*[.]?[0-9]?$")
    var weight by mutableStateOf("")
    var reps by mutableStateOf("")

    var weightInvalid by mutableStateOf(false)
    var repsInvalid by mutableStateOf(false)

    var title by mutableStateOf("")
    var body by mutableStateOf("")

    var titleInvalid by mutableStateOf(false)
    var bodyInvalid by mutableStateOf(false)

    var addPost by mutableStateOf(false)

    /*init {
        if (!_workoutFetched.value) {
            getWorkouts()
            _workoutFetched.value = true
        }
    }*/

    val updateWeight: (String) -> Unit = { newWeight ->
        if(newWeight.matches(weightRegex)){
            if(newWeight.isNotEmpty()) {
                _set.value = _set.value?.copy(weight = newWeight.toDouble())
            }
            weight = newWeight
        }
    }

    val updateReps: (String) -> Unit = { newReps ->
        if(newReps.isEmpty() || newReps.matches(repRegex)){
            if(newReps.isNotEmpty()){
                _set.value = _set.value?.copy(rep = newReps.toLong())
            }
            reps = newReps
        }
    }

    val updateBody: (String) -> Unit = { newBody ->
        if(newBody.isNotEmpty()){
            body = newBody
        }
    }

    val updateTitle: (String) -> Unit = { newTitle ->
        if(newTitle.isNotEmpty()){
            title = newTitle
        }
    }



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
        workoutRepo.getExercises(query.lowercase()) { exerciseList ->
            _exercises.value = exerciseList
            Log.d("Firebase", "Exercises found: ${exerciseList.size}")
            _loading.value = false
        }
    }

    /**
     * Function to remove an exercise from the currently mutable workout
     *
     */


    /**
     * Function to create a new workout object
     *
     * takes a completed MWorkout object from the UI and then sends it to the database
     */
   fun createNewWorkout(workout: Mworkout) {
        if(!_loading.value) {
            _loading.value = true
            try{
                workoutRepo.createNewWorkout(oRM(workout), uid){ workout ->
                    if(addPost) {
                        userRepo.getUser(uid) { user ->
                            val post = Mpost("", Timestamp.now(), workout, user, title, body)
                            postRepo.addPost(uid, post)
                        }
                    }
                }
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
        workoutRepo.editWorkout(oRM(workout), uid) {

        }
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
        for(exercise in workout.exercises){
            exercise.setAvgORM()
        }
        return workout
    }

    /**
     * Function to delete an existing workout
     */
    fun deleteWorkout(workout: Mworkout) {
        _loading.value = true
        _workouts.value -= workout
        workoutRepo.deleteWorkout(workout, uid){}
        _loading.value = false
    }

    /**
     * Function to get workout list from database for specific user
     */
    fun getWorkouts() {
        postRepo.getPosts(uid){ posts ->
            _posts.value = posts
            Log.d("Firebase", "Posts found: ${posts.size}")
        }
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

    /**
     * function to remove a set from a workout
     * @param exercise: exercise to remove set from
     * @param set: set to remove
     */
    fun removeSet(exercise: Mexercise, setToRemove: Mset) {
        val updatedExercise = exercise.copy(
            sets = exercise.sets.filter { it != setToRemove }
        )

        // Find the exercise in the workout and update it
        _workout.value?.let {
            val updatedExercises = it.exercises.map { currentExercise ->
                if (currentExercise == exercise) updatedExercise else currentExercise
            }
            _workout.value = it.copy(exercises = updatedExercises)
        }
    }
}

data class WorkoutDialogUiState(
    val workoutName: String = "",
    val muscleGroup: String = "",
    val workoutNameError: Boolean = false,
    val muscleGroupError: Boolean = false,
)