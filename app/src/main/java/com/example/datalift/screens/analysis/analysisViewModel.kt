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
import com.example.datalift.model.GoalType
import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.analysisRepo
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Mworkout
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
    private val analysisRepo: analysisRepo,
    private val workoutRepo: WorkoutRepository
) : ViewModel()  {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
//    private val analysisRepo = analysisRepo()

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

    private val _workoutProgressions = MutableStateFlow<List<Manalysis>>(emptyList())
    val workoutProgressions: StateFlow<List<Manalysis>> get() = _workoutProgressions

    private val _workouts = MutableStateFlow<List<Mworkout>>(emptyList())
    val workouts: StateFlow<List<Mworkout>> get() = _workouts

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

    fun getWorkouts() {
        workoutRepo.getWorkouts(uid) { workoutList ->
            _workouts.value = workoutList
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

    fun getWorkoutProgressions() : MutableStateFlow<List<Manalysis>>{
        val workoutProgressions = MutableStateFlow<List<Manalysis>>(emptyList())
        analysisRepo.getWorkoutProgression(uid) { progressionList ->
            if (progressionList.isEmpty()) {
                Log.d("Firebase", "No workout progressions found")
            } else {
                Log.d("Firebase", "Workout progression found: ${progressionList.last()}")
            }
            workoutProgressions.value = progressionList
            _workoutProgressions.value = progressionList
            _tempFlag.value = true
        }
        return workoutProgressions
    }

    //
    fun getExerciseAnalysis() : MutableStateFlow<List<MexerAnalysis>>{
        val exerciseAnalysis = MutableStateFlow<List<MexerAnalysis>>(emptyList())
        analysisRepo.getAnalyzedExercises(uid) { analysisList ->
            exerciseAnalysis.value = analysisList
            _exerciseAnalysis.value = analysisList
            Log.d("Firebase", "Exercise analysis found: ${analysisList.size}")
            getBodyParts()
        }
        return exerciseAnalysis
    }

    private val _exerciseAnalysis = MutableStateFlow<List<MexerAnalysis>>(emptyList())

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
        getWorkouts()
        analysisRepo.analyzeWorkouts(
            uid = uid,
            onComplete = {
                Log.d("WorkoutAnalyzer", "Analysis complete, now evaluating goals")
                analysisRepo.evaluateGoals(
                    uid = uid,
                    exerciseAnalysis = _exerciseAnalysis.value,
                    workouts = _workouts.value,
                    onComplete = {
                        Log.d("GoalEval", "Goal evaluation complete")
                    }
                )
            },
            onFailure = {
                Log.e("WorkoutAnalyzer", "Failed to analyze workouts: ${it.message}")
            }
        )
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