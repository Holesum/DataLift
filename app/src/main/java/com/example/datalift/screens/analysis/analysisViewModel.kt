package com.example.datalift.screens.analysis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.Mworkout
import com.example.datalift.model.analysisRepo
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.math.log

class analysisViewModel(
//    private val analysisRepo: analysisRepo,
) : ViewModel()  {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val analysisRepo = analysisRepo()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Loading)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    // Workouts state
    private val _workoutProgressions = MutableStateFlow<List<Manalysis>>(emptyList())
    val workoutProgressions: StateFlow<List<Manalysis>> get() = _workoutProgressions

    private val _exerciseAnalysis = MutableStateFlow<List<MexerAnalysis>>(emptyList())
    val exerciseAnalysis: StateFlow<List<MexerAnalysis>> get() = _exerciseAnalysis

    private val _type = MutableStateFlow("") // What data should be displayed
    val type: StateFlow<String> get() = _type

    private val _muscleGroup = MutableStateFlow("") //If user wants to see specific muscle group workouts
    val muscleGroup: StateFlow<String> get() = _muscleGroup

    val muscleGroups: List<String> = listOf("Push", "Pull", "Legs", "Chest", "Shoulder", "Arms", "Core", "Full Body")

    private val _bodyPart = MutableStateFlow("") //If user wants to see specific body part analysis
    val bodyPart: StateFlow<String> get() = _bodyPart

    private val _bodyParts = MutableStateFlow<List<String>>(emptyList()) //List of all body parts
    val bodyParts: StateFlow<List<String>> get() = _bodyParts

    init {
        analyzeWorkouts()
        fetchData()

    }

    private fun fetchData(){
        viewModelScope.launch {
            getWorkoutProgressions()
            val workoutProgression = _workoutProgressions.value
            val exerciseAnalysis = getExerciseAnalysis()
            _uiState.value = AnalysisUiState.Success(
                workoutProgression = _workoutProgressions.value,
                exerciseAnalysis = _exerciseAnalysis.value,
            )
        }
    }

    // Overall Workouts
    // Gets everyworkout completed in 30-90 amount of days
    private fun getWorkoutProgressions(){
//        _loading.value = true
        Log.d("Debug", "Pre-crash")
        analysisRepo.getWorkoutProgression(uid) { progressionList ->
            if (progressionList.isEmpty()) {
                Log.d("Firebase", "No workout progressions found")
                // Notify user about no progressions
            } else {
                Log.d("Firebase", "Workout progression found: ${progressionList.size}")
            }
            _workoutProgressions.value = progressionList
            //_loading.value = false
        }
//        _loading.value = false
        Log.d("Debug", "Post-crash")
    }

    //
    private fun getExerciseAnalysis(){
//        _loading.value = true
        analysisRepo.getAnalyzedExercises(uid) { analysisList ->
            _exerciseAnalysis.value = analysisList
            Log.d("Firebase", "Exercise analysis found: ${analysisList.size}")
            getBodyParts()
//            _loading.value = false
        }
//        _loading.value = false
    }

//    val uiState: StateFlow<AnalysisUiState> = combine(
//        getWorkoutProgressions(),
//        getExerciseAnalysis(),
//        AnalysisUiState::Success,
//    ).stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = AnalysisUiState.Loading,
//    )



    private fun getBodyParts() {
        for(analysis in _exerciseAnalysis.value){
            if(!_bodyParts.value.contains(analysis.bodyPart)) {
                _bodyParts.value += analysis.bodyPart
            }
        }
    }

    fun setType(type: String) {
        _type.value = type
    }

    fun setMuscleGroup(muscleGroup: String) {
        _muscleGroup.value = muscleGroup
    }

    fun setBodyPart(bodyPart: String) {
        _bodyPart.value = bodyPart
    }




    fun analyzeWorkouts() {
        val tempUID = uid // Replace with dynamic user ID if needed
        val dateRange = 30
        val currentDate = LocalDateTime.now()
        val startDate = currentDate.minusDays(dateRange.toLong())
        val startDateTimestamp = Timestamp(startDate.atZone(ZoneId.systemDefault()).toInstant())

        Log.d("WorkoutAnalyzer", "Analyzing workouts from $startDateTimestamp to $currentDate")

        db.collection("Users")
            .document(tempUID)
            .collection("Workouts")
            .whereGreaterThan("date", startDateTimestamp)
            .get()
            .addOnSuccessListener { workouts ->
                Log.d("WorkoutAnalyzer", "Analyzing workouts from $startDate to $currentDate")
                val exerciseData = mutableMapOf<String, MutableMap<String, Any>>()
                val workoutProgressions = mutableMapOf<String, MutableMap<String, Any>>()



                for (workout in workouts.documents) {
                    val workoutData = workout.data ?: continue
                    val workoutDate = workoutData["date"] as? Date ?: Date()
                    val workoutType = workoutData["muscleGroup"] as? String ?: "Unknown Type"



                    workoutProgressions[workout.id] = mutableMapOf(
                        "totalProgression" to 0.0,
                        "exerciseCount" to 0,
                        "date" to workoutDate,
                        "muscleGroup" to workoutType
                    )

                    val exercises = workoutData["exercises"] as? List<Map<String, Any>> ?: continue
                    for (exercise in exercises) {
                        val exerciseName = exercise["name"] as? String ?: "Unknown Exercise"
                        val avgORM = exercise["avgORM"] as? Double ?: 0.0
                        val exerObject = exercise["exercise"] as? Map<String, Any> ?: emptyMap()
                        val bodyPart = exerObject["bodyPart"] as? String ?: "unknown"

                        if (avgORM > 0) {
                            if (!exerciseData.containsKey(exerciseName)) {
                                exerciseData[exerciseName] = mutableMapOf(
                                    "initialAvgORM" to avgORM,
                                    "progression" to mutableListOf<Map<String, Any>>(),
                                    "bodyPart" to bodyPart
                                )
                            }

                            val initialAvgORM = exerciseData[exerciseName]?.get("initialAvgORM") as? Double ?: avgORM
                            val progressionMultiplier = avgORM / initialAvgORM

                            // Add progression data
                            (exerciseData[exerciseName]?.get("progression") as? MutableList<Map<String, Any>>)?.add(
                                mapOf(
                                    "workoutId" to workout.id,
                                    "date" to workoutDate,
                                    "progressionMultiplier" to progressionMultiplier
                                )
                            )

                            // Update workout progressions
                            workoutProgressions[workout.id]?.let {
                                it["totalProgression"] = (it["totalProgression"] as Double) + progressionMultiplier
                                it["exerciseCount"] = (it["exerciseCount"] as Int + 1).toInt()
                            }
                        }
                    }
                }

                // Calculate average progression and save to Firestore
                workoutProgressions.forEach { (workoutId, progressionData) ->
                    val exerciseCount = progressionData["exerciseCount"] as Int
                    if (exerciseCount > 0) {
                        progressionData["totalProgression"] = (progressionData["totalProgression"] as Double) / exerciseCount
                    }
                    db.collection("Users").document(tempUID)
                        .collection("WorkoutProgressions").document(workoutId)
                        .set(progressionData)
                }

                // Analyze exercise progression
                val exerciseRef = db.collection("Users").document(tempUID).collection("AnalyzedExercises")
                exerciseData.forEach { (exerciseName, progressionData) ->
                    exerciseRef.document(exerciseName).set(progressionData).addOnSuccessListener { Log.d("WorkoutAnalyzer", "progression added") }
                        .addOnFailureListener { e -> Log.d("WorkoutAnalyzer", "Error adding progression: ${e.message}") }
                }

            }.addOnFailureListener { e ->
                Log.d("WorkoutAnalyzer", "Error fetching workouts: ${e.message}")
            }
    }



}

//private fun analysisUiState() : Flow<AnalysisUiState> {
//
//}

sealed interface AnalysisUiState{
    data object Loading : AnalysisUiState

    data class Success(
        val workoutProgression: List<Manalysis>,
        val exerciseAnalysis: List<MexerAnalysis>,
    ) : AnalysisUiState

    data object Error : AnalysisUiState

}