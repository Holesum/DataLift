package com.example.datalift.screens.workout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mworkout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class WorkoutViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val workoutRef = db.collection("Users").document(uid).collection("Workouts")

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

    init{
        if(_workoutFetched.value == false) {
            getWorkouts()
        }
    }

    /**
     * Function to get search exercise in existing list of exercises
     */
    fun getExercises(){
        if(_loading.value == false) {
            _loading.value = true

                val exerciseList = mutableListOf<ExerciseItem>()
                db.collection("ExerciseList").get()
                    .addOnSuccessListener { snapShot ->
                        for (document in snapShot.documents) {
                            val exercise = ExerciseItem.fromDocument(document)
                            exerciseList.add(exercise)
                        }
                        _exercises.value = exerciseList
                    }

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
                db.collection("Users")
                    .document(uid)
                    .collection("Workouts")
                    .add(workout)
                    .addOnSuccessListener {documentReference ->
                        val documentID = documentReference.id
                        val updatedWorkout = workout.copy(docID = documentID)
                        db.collection("Users")
                            .document(uid)
                            .collection("Workouts")
                            .document(documentID)
                            .set(updatedWorkout)
                            .addOnSuccessListener {
                                Log.d("Firebase", "Workout docID set: ${updatedWorkout.docID}")
                            }.addOnFailureListener{
                                Log.d("Firebase", "Error setting docID: ${it.message}")
                            }
                        Log.d("Firebase", "Workout created: ${workout.name}")
                    }.addOnFailureListener{e ->
                        Log.d("Firebase", "Error creating workout: ${e.message}")

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
        workoutRef.document(workout.docID).set(workout)
            .addOnSuccessListener {
                Log.d("Firebase", "Workout updated: ${workout.name}")
            }.addOnFailureListener { e ->
                Log.d("Firebase", "Error updating workout: ${e.message}")
            }
    }

    /**
     * Function to delete an existing workout
     */
    fun deleteWorkout(workout: Mworkout) {
        workoutRef.document(workout.docID).delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Workout deleted: ${workout.name}")
            }.addOnFailureListener { e ->
                Log.d("Firebase", "Error deleting workout: ${e.message}")
            }
    }

    /**
     * Function to get workout list from database for specific user
     */
    fun getWorkouts() {
        _loading.value = true
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .get()
            .addOnSuccessListener { snapShot ->
                val workoutList = mutableListOf<Mworkout>()
                for (document in snapShot.documents) {
                    val workout = document.toObject(Mworkout::class.java)
                    if (workout != null) {
                        workoutList.add(workout)
                    }
                }
                Log.d("Firebase", "Workouts found: ${workoutList[0]}")
                _workouts.value = workoutList
                _workoutFetched.value = true
                _loading.value = false
            }.addOnFailureListener { e ->
                Log.d("Firebase", "Error getting workouts: ${e.message}")
                _workouts.value = emptyList()
                _loading.value = false
            }
    }

    /**
     * Function to get workout details from database with ID
     * @param id: ID of workout to get
     *
     */
    fun getWorkout(id: String) {
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .document(id)
            .get()
            .addOnSuccessListener {
                Log.d("Firebase", "Workout found: ${it.data}")
            }
    }
}