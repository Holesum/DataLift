package com.example.datalift.screens.challenges

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.data.repository.ChallengeRepository
import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.data.repository.WorkoutRepository
import com.example.datalift.model.ChallengeProgress
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.userRepo
import com.example.datalift.navigation.getCurrentUserId
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ChallengeCreationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepo: userRepo,
    private val challengeRepo: ChallengeRepository,
    private val workoutRepo: WorkoutRepository,
    private val goalRepo: GoalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChallengeCreationUiState())
    val uiState: StateFlow<ChallengeCreationUiState> = _uiState.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseItem>>(emptyList())
    val exercises: StateFlow<List<ExerciseItem>> get() = _exercises

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchUiState: StateFlow<ChallengeCreationSearchUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.trim().length < SEARCH_QUERY_MIN_LENGTH){
                flowOf(ChallengeCreationSearchUiState.EmptyQuery)
            } else {
                getUsers(query = query)
                    .map<List<Muser>,ChallengeCreationSearchUiState> { usersList ->
                        ChallengeCreationSearchUiState.Success(
                            usersSearched = usersList
                        )
                    }
                    .catch {
                        emit(ChallengeCreationSearchUiState.LoadFailed)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChallengeCreationSearchUiState.Loading
        )

    fun onSearchQueryChange(query: String){
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun addUser(user: Muser){
        _uiState.update { currState ->
            currState.copy(
                participants = currState.participants.plus(user)
            )
        }
    }

    fun userAlreadyAdded(user: Muser) : Boolean{
        return _uiState.value.participants.contains(user)
    }

    private fun getUsers(query: String): MutableStateFlow<List<Muser>>{
        val _users = MutableStateFlow<List<Muser>>(emptyList())
        userRepo.getUsers(query){ userList ->
            _users.value = userList
        }
        return _users
    }

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

    fun createChallenge() : Boolean{
        if (_uiState.value.goal != null){
            createGoal(_uiState.value.goal!!)
        }

        val challenge = Mchallenge(
            challengeId = "",
            creatorUid = getCurrentUserId(),
            title = _uiState.value.title,
            description = _uiState.value.description,
            startDate = Timestamp(
                time = Instant.ofEpochMilli(_uiState.value.startDate!!)
            ),
            endDate = Timestamp(
                time = Instant.ofEpochMilli(_uiState.value.endDate!!)
            ),
            goal = _uiState.value.goal!!,
            participants = _uiState.value.participants,
            progress = _uiState.value.participants.associate { it.uid to ChallengeProgress() }
        )

        var wasSuccessful = false
        challengeRepo.createChallenge(getCurrentUserId(), challenge){ createdChallenge ->
            if (createdChallenge != null){
                wasSuccessful = true
            } else {
                wasSuccessful = false
            }
        }
        return wasSuccessful
    }

    private fun createGoal(goal: Mgoal){
        viewModelScope.launch {
            goalRepo.createGoal(
                uid = getCurrentUserId(),
                goal = goal
            ) { createdGoal ->
                if(createdGoal != null){
                    updateGoal(createdGoal)
                }
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
    val participants: List<Muser> = emptyList(),

    val titleHasError: Boolean = false,
    val dateError: Boolean = false,
    val canCreateChallenge: Boolean = false,
)

sealed interface ChallengeCreationSearchUiState{
    data object Loading : ChallengeCreationSearchUiState

    data object EmptyQuery : ChallengeCreationSearchUiState

    data object LoadFailed : ChallengeCreationSearchUiState

    data class Success(
        val usersSearched : List<Muser> = emptyList()
    ) : ChallengeCreationSearchUiState {
        fun isEmpty(): Boolean = usersSearched.isEmpty()
    }

    data object SearchNotReady : ChallengeCreationSearchUiState
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 1