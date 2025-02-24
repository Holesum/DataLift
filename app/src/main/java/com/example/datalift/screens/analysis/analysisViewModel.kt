package com.example.datalift.screens.analysis

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.Mworkout
import com.example.datalift.model.analysisRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.log

class analysisViewModel(
//    private val analysisRepo: analysisRepo,
) : ViewModel()  {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val analysisRepo = analysisRepo()

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Loading)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        getWorkoutProgressions()
        getExerciseAnalysis()
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
    fun getWorkoutProgressions() {
//        _loading.value = true
        analysisRepo.getWorkoutProgression(uid) { progressionList ->

                Log.d("Firebase", "Progression List Null")

            _workoutProgressions.value = progressionList
            Log.d("Firebase", "Workout progression found: ${progressionList.size}")
//            _loading.value = false
        }
        Log.d("Firebase", "Finished Running Progression")
//        _loading.value = false
        Log.d("Debug", "Post-crash")
//        return _workoutProgressions
    }

    //
    fun getExerciseAnalysis() : MutableStateFlow<List<MexerAnalysis>>{
//        _loading.value = true
        analysisRepo.getAnalyzedExercises(uid) { analysisList ->
            _exerciseAnalysis.value = analysisList
            Log.d("Firebase", "Exercise analysis found: ${analysisList.size}")
            getBodyParts()
//            _loading.value = false
        }
//        _loading.value = false
        return _exerciseAnalysis
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