package com.example.datalift.screens.analysis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.datalift.data.repository.WorkoutRepository
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.analysisRepo
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class analysisViewModel @Inject constructor(
//    private val analysisRepo: analysisRepo,
    private val workoutRepo: WorkoutRepository
) : ViewModel()  {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val analysisRepo = analysisRepo()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Exercise List stuffs
    private val _exercises = MutableStateFlow<List<ExerciseItem>>(emptyList())
    val exercises: StateFlow<List<ExerciseItem>> get() = _exercises

    private val _exerciseFetched = MutableStateFlow(false)
    val exerciseFetched: StateFlow<Boolean> get() = _exerciseFetched

    private val _exercise = MutableStateFlow<String>("")
    val exercise: StateFlow<String> get() = _exercise

    private val _apiResponseName = MutableStateFlow<String>("")
    val apiResponseName: StateFlow<String> get() = _apiResponseName

    private val _apiResponseInput = MutableStateFlow<String>("")
    val apiResponseInput: StateFlow<String> get() = _apiResponseInput

    private val _searchExerciseUiState = MutableStateFlow(SearchExerciseUiState())
    val searchExerciseUiState: StateFlow<SearchExerciseUiState> = _searchExerciseUiState.asStateFlow()

    private val _chosenBodyPart = MutableStateFlow<String>("Full Body")
    val chosenBodyPart: StateFlow<String> get() = _chosenBodyPart

    fun updateBodyPart(string: String){
        _chosenBodyPart.value = string
    }

    fun updateQuery(newQuery: String){
        _searchExerciseUiState.update { currentState ->
            currentState.copy(
                query = newQuery
            )
        }
    }

    fun updateDisplays(dialogDisplayed: Boolean, recommendationDisplayed: Boolean? = null){
        _searchExerciseUiState.update { currentState ->
            currentState.copy(
                dialogDisplayed = dialogDisplayed,
                recommendationDisplayed = recommendationDisplayed ?: currentState.recommendationDisplayed
            )
        }
    }

    /**
     * Function to get search exercise in existing list of exercises
     */
    fun getExercises(query: String = ""){
        workoutRepo.getExercises(query.lowercase()) { exerciseList ->
            _exercises.value = exerciseList
            Log.d("Firebase", "Exercises found: ${exerciseList.size}")
        }
    }

    fun setExercise(exercise: String){
        _exercise.value = exercise
    }

    //Volley API Call
    private val requestQueue = Volley.newRequestQueue(FirebaseApp.getInstance().applicationContext)

    fun fetchExternalData() {
        val url = "https://akrishnadas1.pythonanywhere.com/getrec?exercise=${_exercise.value}" // Replace with your URL
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response: JSONObject ->
                Log.d("Volley", "Response: $response.")
                val output = response.optJSONObject("output")
                if (output != null) {
                    val rec1 = output.optJSONObject("1")
                    val rec2 = output.optJSONObject("2")
                    val rec3 = output.optJSONObject("3")
                    val rec4 = output.optJSONObject("4")
                    val rec5 = output.optJSONObject("5")
                    if(
                        rec1 != null &&
                        rec2 != null &&
                        rec3 != null &&
                        rec4 != null &&
                        rec5 != null
                    ){
                        val rec1_str = rec1.optString("title")
                        val rec2_str = rec2.optString("title")
                        val rec3_str = rec3.optString("title")
                        val rec4_str = rec4.optString("title")
                        val rec5_str = rec5.optString("title")
                        _apiResponseName.value = rec1_str + ", " + rec2_str + ", " + rec3_str + ", " + rec4_str + ", " + rec5_str
                    }
                    //Log.d("Volley", "Title: $_apiResponseName")
                }
                _apiResponseInput.value = response.optString("input")
                // Handle the response
                // For example, parse the response and update UI state
            },
            { error: VolleyError ->
                Log.e("Volley", "Error: ${error.message}")
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

//    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Loading)
//    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()
//
//    init {
//        getWorkoutProgressions2()
//        getExerciseAnalysis()
////        fetchData()
//
//    }
////
//    private fun fetchData(){
//        viewModelScope.launch {
//            getWorkoutProgressions()
//            val workoutProgression = _workoutProgressions.value
//            val exerciseAnalysis = getExerciseAnalysis()
//            _uiState.value = AnalysisUiState.Success(
//                workoutProgression = _workoutProgressions.value,
//                exerciseAnalysis = _exerciseAnalysis.value,
//            )
//        }
//    }

    // Overall Workouts
    // Gets everyworkout completed in 30-90 amount of days
//    fun getWorkoutProgressions() : MutableStateFlow<List<Manalysis>> {
//        analysisRepo.getWorkoutProgression(uid) { progressionList ->
//            Log.d("Firebase", "Progression List Null")
//
//            _workoutProgressions.value = progressionList
//            Log.d("Firebase", "Workout progression found: ${progressionList.size}")
//        }
//        Log.d("Firebase", "Finished Running Progression")
//        Log.d("Debug", "Post-crash")
//        return _workoutProgressions
//    }

//     fun getWorkoutProgressions() : MutableStateFlow<List<Manalysis>> {
//         Log.d("Debug", "Pre-crash")
//         analysisRepo.getWorkoutProgression(uid) { progressionList ->
//             if (progressionList.isEmpty()) {
//                 Log.d("Firebase", "No workout progressions found")
//                 // Notify user about no progressions
//             } else {
//                 Log.d("Firebase", "Workout progression found: ${progressionList.size}")
//             }
//             _workoutProgressions.value = progressionList
//         }
//         Log.d("Debug", "Post-crash")
//         return _workoutProgressions
//
//    }

    fun getWorkoutProgressions() : MutableStateFlow<List<Manalysis>>{
        analysisRepo.getWorkoutProgression(uid) { progressionList ->
            if (progressionList.isEmpty()) {
                Log.d("Firebase", "No workout progressions found")
            } else {
                Log.d("Firebase", "Workout progression found: ${progressionList.last()}")
            }
            _workoutProgressions.value = progressionList
            _tempFlag.value = true
        }
        analysisRepo.getAnalyzedExercises(uid) { analysisList ->
            _exerciseAnalysis.value = analysisList
            Log.d("Firebase", "Exercise analysis found: ${analysisList.last()}")
        }
        getBodyParts()
        return _workoutProgressions
    }

    //
    fun getExerciseAnalysis() : MutableStateFlow<List<MexerAnalysis>>{

        analysisRepo.getAnalyzedExercises(uid) { analysisList ->
            _exerciseAnalysis.value = analysisList
            Log.d("Firebase", "Exercise analysis found: ${analysisList.size}")
            getBodyParts()
        }
        return _exerciseAnalysis
    }

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

    private val _bodyPart = MutableStateFlow("") //If user wants to see specific body part workouts
    val bodyPart: StateFlow<String> get() = _bodyPart

    private val _bodyParts = MutableStateFlow<List<String>>(emptyList())
    val bodyParts: StateFlow<List<String>> get() = _bodyParts



    private val _tempFlag = MutableStateFlow(false)
    val tempFlag: StateFlow<Boolean> get() = _tempFlag



    val uiState: StateFlow<AnalysisUiState> = combine(
        getWorkoutProgressions(),
        getExerciseAnalysis(),
        AnalysisUiState::Success,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AnalysisUiState.Loading,
    )

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
                                it["exerciseCount"] = (it["exerciseCount"] as Int + 1)
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
data class SearchExerciseUiState(
    val query: String = "",
    val dialogDisplayed: Boolean = false,
    val recommendationDisplayed: Boolean = false
)

sealed interface AnalysisUiState{
    data object Loading : AnalysisUiState

    data class Success(
        val workoutProgression: List<Manalysis>,
        val exerciseAnalysis: List<MexerAnalysis>,
    ) : AnalysisUiState

    data object Error : AnalysisUiState

}