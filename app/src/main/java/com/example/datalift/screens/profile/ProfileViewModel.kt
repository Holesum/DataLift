package com.example.datalift.screens.profile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.datalift.model.GoalType
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.goalRepo
import com.example.datalift.model.userRepo
import com.example.datalift.navigation.ProfileDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepo: userRepo,
    private val goalRepo: goalRepo
) : ViewModel() {
//    private var auth: FirebaseAuth = Firebase.auth
//    private val uid: String = auth.currentUser?.uid.toString()

    private val profile = savedStateHandle.toRoute<ProfileDetail>()

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()



    init {
        loadUserProfile(profile.profileId)
//        viewModelScope.launch {
//            _uiState.value = ProfileUiState.Loading
//
//            val user = getUser().value
//
//            if(user != null){
//                _uiState.value = ProfileUiState.Success(user)
//            } else {
//                _uiState.value = ProfileUiState.Error
//            }
//        }
    }

    private fun loadUserProfile(id: String){
        viewModelScope.launch {
            tryGoals()
            _uiState.value = ProfileUiState.Loading
            userRepo.getUser(id){ user ->
                if(user!=null){
                    _uiState.value = ProfileUiState.Success(user)
                } else {
                    _uiState.value = ProfileUiState.Error
                }
            }
        }
    }

    fun tryGoals(){
        val newGoal = Mgoal(
            type = GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART,
            targetValue = 5,
            bodyPart = "Push"
        )

        goalRepo.createGoal(profile.profileId, newGoal) { createdGoal ->
            if (createdGoal != null) {
                Log.d("Goal", "Goal created: $createdGoal")
            } else {
                Log.e("Goal", "Failed to create goal")
            }
        }
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
        val user: Muser
    ) : ProfileUiState

    data object Error : ProfileUiState
}