package com.example.datalift.screens.analysis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.model.Manalysis
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.analysisRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class analysisViewModel(
//    private val analysisRepo: analysisRepo,
) : ViewModel()  {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val analysisRepo = analysisRepo()

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

    private val _bodyPart = MutableStateFlow("") //If user wants to see specific body part analysis
    val bodyPart: StateFlow<String> get() = _bodyPart

    private val _bodyParts = MutableStateFlow<List<String>>(emptyList()) //List of all body parts
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