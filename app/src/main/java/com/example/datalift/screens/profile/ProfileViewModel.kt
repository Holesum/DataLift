package com.example.datalift.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.model.Muser
import com.example.datalift.model.userRepo
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
    private val userRepo: userRepo
) : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
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

    private fun loadUserProfile(){
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            userRepo.getUser(uid){ user ->
                if(user!=null){
                    _uiState.value = ProfileUiState.Success(user)
                } else {
                    _uiState.value = ProfileUiState.Error
                }
            }
        }
    }

    fun getUser() : MutableStateFlow<Muser?> {
        val _user = MutableStateFlow<Muser?>(null)

        userRepo.getUser(uid){ user ->
            _user.value = user
        }

        return _user
    }

}

sealed interface ProfileUiState{
    data object Loading : ProfileUiState

    data class Success(
        val user: Muser
    ) : ProfileUiState

    data object Error : ProfileUiState
}