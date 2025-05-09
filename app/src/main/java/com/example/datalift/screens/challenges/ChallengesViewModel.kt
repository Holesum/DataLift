package com.example.datalift.screens.challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.data.repository.ChallengeRepository
import com.example.datalift.model.ChallengeProgress
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.userRepo
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepo: userRepo
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChallengesUiState> = MutableStateFlow(ChallengesUiState.Loading)
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    val uid: MutableStateFlow<String?> = MutableStateFlow("")
    val uidState: StateFlow<String?> = uid.asStateFlow()

    private val _currentChallenge: MutableStateFlow<Mchallenge?> = MutableStateFlow(null)
    val currentChallenge: StateFlow<Mchallenge?> = _currentChallenge.asStateFlow()

    init {
        loadChallenges()
        getCurrentUserId()
    }
//    val uiState: StateFlow<ChallengesUiState> = combine(
//        challengeRepository.getChallengesForCurrentUser(),
//        ChallengesUiState::Success,
//    ).

    private fun loadChallenges(){
        viewModelScope.launch {
            _uiState.value = ChallengesUiState.Loading
            challengeRepository.getChallengesForCurrentUser().collect{ challenges ->
                _uiState.value = ChallengesUiState.Success(challenges = challenges)

            }
//            _uiState.value = ChallengesUiState.Success(
//                challenges = challengeRepository.getChallengesForCurrentUser().value
//            )
        }
//        challengeRepository.getChallengesForCurrentUser()
    }
    
    private fun getCurrentUserId(){
        viewModelScope.launch {
            val currentUserId = userRepo.getCurrentUserId()
            uid.value = currentUserId
        }
    }



    fun createChallenge(
        title: String,
        description: String,
        goal: Mgoal,
        startDate: Date,
        endDate: Date,
        participants: List<Muser>
    ) {
        val uid = getCurrentUserId()
        if (uid == null) {
            _uiState.value = ChallengesUiState.Error
            return
        }

        val challenge = Mchallenge(
            challengeId = "", // Will be overwritten
            creatorUid = uidState.value?: "",
            title = title,
            description = description,
            startDate = Timestamp(startDate),
            endDate = Timestamp(endDate),
            goal = goal,
            participants = participants,
            progress = participants.associate { it.uid to ChallengeProgress() }
        )

        _uiState.value = ChallengesUiState.Loading

//        challengeRepository.createChallenge(uidState.value?: "", challenge) { result ->
//            _uiState.value = if (result != null) {
//                ChallengesUiState.CreationSuccess(result)
//            } else {
//                ChallengesUiState.Error
//            }
//        }
    }

    fun loadChallenge(id: String){
        challengeRepository.getChallenge(id){ challenge ->
            _currentChallenge.value = challenge
            Log.d("ChallengesViewModel","Challenge Assigned")
        }
    }

    fun RetrieveChallenge(id: String) : StateFlow<Mchallenge> =
        challengeRepository.getChallenge(id)


}

sealed interface ChallengesUiState{
    data object Loading : ChallengesUiState

    data class Success(
        val challenges: List<Mchallenge>
    ) : ChallengesUiState

//    data class CreationSuccess(val challenge: Mchallenge) : ChallengesUiState

    data object Error: ChallengesUiState
}

/*
//Firebase
import android.util.Log
import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.model.GoalType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.DocumentReference

//Data models
import com.example.datalift.model.challengeRepo
import com.example.datalift.model.userRepo
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.goalRepo
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class challengesViewModel {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val challengeRepo = challengeRepo()
    private val userRepo = userRepo()
    //private val goalRepo = GoalRepository()

    //State Flows
    private val _user = MutableStateFlow<DocumentReference?>(null)
    val user: StateFlow<DocumentReference?> get() = _user

    private val _users = MutableStateFlow<List<DocumentReference>>(emptyList())
    val users: StateFlow<List<DocumentReference>> get() = _users

    private val _challengeName = MutableStateFlow<String>("")
    val challengeName: StateFlow<String> get() = _challengeName


    fun createChallengeObject(name: String, details: String, members: List<DocumentReference>){

    }

    fun createChallenge() {
        try {
            Log.d("Firebase", "now attempting to add challenge")
//            challengeRepo.createChallenge(Mchallenge( //Fix to use the new challenge model
//                "test",
//                "test",
//                _users.value,
//                db.collection("Users").document(uid),
//                Timestamp.now(),
//                emptyList()
//                )
//            )

        } catch (e: Exception) {
            Log.d("Firebase", "Error creating challenge: ${e.message}")
        }
    }
}
*/
