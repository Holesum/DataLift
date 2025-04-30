package com.example.datalift.screens.profile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.datalift.data.repository.AnalysisRepository
import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.data.repository.WorkoutRepository
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.GoalType
import com.example.datalift.model.MexerAnalysis
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.Mworkout
import com.example.datalift.model.userRepo
import com.example.datalift.navigation.ProfileDetail
import com.example.datalift.navigation.getCurrentUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepo: userRepo,
    private val goalRepo: GoalRepository,
    private val workoutRepo: WorkoutRepository,
    private val analysisRepo: AnalysisRepository
) : ViewModel() {
//    private var auth: FirebaseAuth = Firebase.auth
//    private val uid: String = auth.currentUser?.uid.toString()

    private val profile = savedStateHandle.toRoute<ProfileDetail>()

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _goals = MutableStateFlow<List<Mgoal>>(emptyList())
    val goals: StateFlow<List<Mgoal>> = _goals

    // State for goal creation dialog visibility
    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible.asStateFlow()

    // Track selected goal type
    private val _selectedGoalType = MutableStateFlow(GoalType.UNKNOWN)
    val selectedGoalType: StateFlow<GoalType> = _selectedGoalType.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseItem>>(emptyList())
    val exercises: StateFlow<List<ExerciseItem>> get() = _exercises

    private val _workouts = MutableStateFlow<List<Mworkout>>(emptyList())
    val workouts: StateFlow<List<Mworkout>> get() = _workouts

    private val _exerciseAnalysis = MutableStateFlow<List<MexerAnalysis>>(emptyList())

    init {
        loadUserProfile(profile.profileId)
//        loadGoals(profile.profileId)
    }

    fun isCurrUser(): Boolean{
        return profile.profileId == getCurrentUserId()
    }

    fun getUnitSystem(): Boolean {
        return userRepo.getCachedUnitType()
    }

    private fun getWorkouts() {
        workoutRepo.getWorkouts(profile.profileId) { workoutList ->
            _workouts.value = workoutList
        }
    }

    private fun getExerciseAnalysis() : MutableStateFlow<List<MexerAnalysis>>{
        val exerciseAnalysis = MutableStateFlow<List<MexerAnalysis>>(emptyList())
        analysisRepo.getAnalyzedExercises(profile.profileId) { analysisList ->
            exerciseAnalysis.value = analysisList
            _exerciseAnalysis.value = analysisList
            Log.d("Firebase", "Exercise analysis found: ${analysisList.size}")
        }
        return exerciseAnalysis
    }

    fun analyzeWorkouts() {
        getWorkouts()
        getExerciseAnalysis()
        analysisRepo.analyzeWorkouts(
            uid = profile.profileId,
            onComplete = {
                Log.d("WorkoutAnalyzer", "Analysis complete, now evaluating goals")
                analysisRepo.evaluateGoals(
                    uid = profile.profileId,
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

    fun getExercises(query: String = ""){
        workoutRepo.getExercises(query.lowercase()) { exerciseList ->
            _exercises.value = exerciseList
            Log.d("Firebase", "Exercises found: ${exerciseList.size}")
        }
    }


    private fun loadUserProfile(id: String){
        viewModelScope.launch {
            //tryGoals()
            _uiState.value = ProfileUiState.Loading
            userRepo.getUser(id){ user ->
                if(user!=null){
                    if(id == getCurrentUserId()){
                        getWorkouts()
                        getExerciseAnalysis()
                        goalRepo.evaluateGoals(id, _exerciseAnalysis.value, _workouts.value) {
                            goalRepo.getGoalsForUser(id) { loadedGoals ->
                                _uiState.value = ProfileUiState.Success(user,loadedGoals)
                            }
                        }
                    } else {
                        _uiState.value = ProfileUiState.Success(user, emptyList())
                    }

                } else {
                    _uiState.value = ProfileUiState.Error
                }
            }
        }
    }

    fun loadGoals(uid: String) {
        viewModelScope.launch {
            getWorkouts()
            getExerciseAnalysis()
            goalRepo.evaluateGoals(uid, _exerciseAnalysis.value, _workouts.value) {
                goalRepo.getGoalsForUser(uid) { loadedGoals ->
                    _goals.value = loadedGoals
                }
            }
        }
    }

    fun updateGoals(id: String){
        viewModelScope.launch {
            getWorkouts()
            getExerciseAnalysis()
            goalRepo.evaluateGoals(id, _exerciseAnalysis.value, _workouts.value) {
                goalRepo.getGoalsForUser(id) { loadedGoals ->
                    _uiState.update { currentState ->
                        if(currentState is ProfileUiState.Success){
                            currentState.copy(
                                goals = loadedGoals
                            )
                        } else currentState
                    }
                }
            }
        }
    }

    fun createGoal(goal: Mgoal) {
        viewModelScope.launch {
            goalRepo.createGoal(profile.profileId, goal) { createdGoal ->
                if (createdGoal != null) {
                    analyzeWorkouts()
                    updateGoals(profile.profileId)
                    Log.d("Goal", "Goal created: $createdGoal")
                } else {
                    Log.e("Goal", "Failed to create goal")
                }
            }
        }
    }

    fun deleteGoal(goal: Mgoal) {
        goalRepo.deleteGoal(profile.profileId, goal) {}
    }

    fun toggleDialogVisibility() {
        _isDialogVisible.value = !_isDialogVisible.value
    }
    fun showDialog() {
        _isDialogVisible.value = true
    }

    fun hideDialog() {
        _isDialogVisible.value = false
    }


    fun updateSelectedGoalType(goalType: GoalType) {
        _selectedGoalType.value = goalType
    }


//    fun getUser() : MutableStateFlow<Muser?> {
//        val _user = MutableStateFlow<Muser?>(null)
//
//        userRepo.getUser(uid){ user ->
//            _user.value = user
//        }
//
//        return _user
//    }

}

sealed interface ProfileUiState{
    data object Loading : ProfileUiState

    data class Success(
        val user: Muser,
        val goals: List<Mgoal>,
    ) : ProfileUiState

    data object Error : ProfileUiState
}

