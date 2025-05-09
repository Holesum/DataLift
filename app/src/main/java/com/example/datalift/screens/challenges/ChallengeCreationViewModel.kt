package com.example.datalift.screens.challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.data.repository.WorkoutRepository
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.GoalRepo
import com.example.datalift.model.Mgoal
import com.example.datalift.model.userRepo
import com.example.datalift.navigation.getCurrentUserId
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ChallengeCreationViewModel @Inject constructor(
    private val userRepo: userRepo,
    private val workoutRepo: WorkoutRepository,
    private val goalRepo: GoalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChallengeCreationUiState())
    val uiState: StateFlow<ChallengeCreationUiState> = _uiState.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseItem>>(emptyList())
    val exercises: StateFlow<List<ExerciseItem>> get() = _exercises

    fun getUnitSystem(): Boolean {
        return userRepo.getCachedUnitType()
    }

    fun updateGoal(newGoal: Mgoal){
        _uiState.update { currentState ->
            currentState.copy(
                goal = newGoal,
                canCreateChallenge = canCreateChallenge(
                    title = currentState.title,
                    goal = newGoal,
                    startDate = currentState.startDate,
                    endDate = currentState.endDate
                )
            )
        }
    }


    fun getExercises(query: String = ""){
        workoutRepo.getExercises(query.lowercase()) { exerciseList ->
            _exercises.value = exerciseList
            Log.d("Firebase", "Exercises found: ${exerciseList.size}")
        }
    }

    fun updateTitle(newTitle: String){
        _uiState.update { currentState ->
            currentState.copy(
                title = newTitle,
                titleHasError = false,
                canCreateChallenge = canCreateChallenge(
                    title = newTitle,
                    goal = currentState.goal,
                    startDate = currentState.startDate,
                    endDate = currentState.endDate
                )
            )
        }
    }

    fun updateDescription(newDescription: String){
        _uiState.update { currentState ->
            currentState.copy(
                description = newDescription,
            )
        }
    }

    fun updateDates(
        startDate: Long?,
        endDate: Long?
    ){
        _uiState.update { currentState ->
            currentState.copy(
                startDate = startDate,
                endDate = endDate,
                canCreateChallenge = canCreateChallenge(
                    title = currentState.title,
                    goal = currentState.goal,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }
    }

    fun createChallenge(){
        if (_uiState.value.goal != null){

        }
    }

    private fun createGoal(goal: Mgoal){
        viewModelScope.launch {
            goalRepo.createGoal(
                uid = getCurrentUserId(),
                goal = goal
            ) { createdGoal ->

            }
        }
    }

    private fun canCreateChallenge(
        title: String,
        goal: Mgoal?,
        startDate: Long?,
        endDate: Long?,
    ) : Boolean{
        return title.isNotBlank() && (goal != null)
                && (startDate != null
                    && startDate > getStartOfTommorwTimetamp()
                    )
                && (endDate != null && endDate > getStartOfNextDay(startDate) )
    }
}

fun roundTimestampToStartOfDay(timestamp: Long): Long{
    val instant = Instant.ofEpochMilli(timestamp)

    val zonedDateTime = ZonedDateTime.ofInstant(
        instant,
        ZoneId.systemDefault()
    )

    val startOfDay = zonedDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault())

    return startOfDay.toInstant().toEpochMilli()
}
fun getStartOfNextDay(dayOfInterest: Long) : Long{
    val zoneID = ZoneId.systemDefault()
    return LocalDate.ofInstant(
            Instant.ofEpochMilli(dayOfInterest),
            zoneID
        ).plusDays(1)
        .atStartOfDay(zoneID)
        .toInstant()
        .toEpochMilli()

}

fun getStartOfTommorwTimetamp(): Long{
//    val today = LocalDate.now(ZoneId.systemDefault())
//
//    val tomorrow = today.plusDays(1)
//
//    return tomorrow
//        .atStartOfDay(ZoneId.systemDefault())
//        .toInstant()
//        .toEpochMilli()
    val zoneID = ZoneId.systemDefault()

    return LocalDate.now(zoneID)
        .plusDays(1)
        .atStartOfDay(zoneID)
        .toInstant()
        .toEpochMilli()
}

data class ChallengeCreationUiState(
    val title: String = "",
    val description: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val goal: Mgoal? = null,

    val titleHasError: Boolean = false,
    val dateError: Boolean = false,
    val canCreateChallenge: Boolean = false,
)