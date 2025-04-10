package com.example.datalift.screens.friends

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.model.Muser
import com.example.datalift.model.userRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepo: userRepo
) : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    val uid: String = auth.currentUser?.uid.toString()
    private val _currentUser = MutableStateFlow<Muser?>(null)
    val currentUser: StateFlow<Muser?> = _currentUser


    fun getCurrUser() {
            userRepo.getUser(uid) { user ->
                _currentUser.value = user
                Log.d("Firebase", "Current user: ${user?.name}")
            }
        }


    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchFriendsUiState: StateFlow<FriendsUiState> =
        searchQuery.flatMapLatest { query ->
            if(query.trim().length < SEARCH_QUERY_MIN_LENGTH){
                flowOf(FriendsUiState.EmptyQuery)
            } else {
                getUsers(query = query)
                    .map<List<Muser>,FriendsUiState> { usersList ->
                        FriendsUiState.Success(
                            usersSearched = usersList
                        )
                    }
                    .catch {
                        emit(FriendsUiState.LoadFailed)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FriendsUiState.Loading
        )


    fun onSearchQueryChange(query: String){
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun currentlyFollowingUser(user: Muser) : Boolean{
//        var currentUser: Muser? = null

//        userRepo.getUser(uid){ myUserObject ->
//            currentUser = myUserObject
//        }

        var ret = false


//        currentUser?.let {
//            ret = user.following.contains(currentUser)
//        }

        return user.followers.contains(uid)
    }

    fun getUsers(query: String): MutableStateFlow<List<Muser>>{
        val _users = MutableStateFlow<List<Muser>>(emptyList())
        userRepo.getUsers(query){ userList ->
            _users.value = userList
        }
        return _users
    }

    fun followUser(user: Muser) {
        Log.d("Firebase", "Attempting to Following user: ${user.name}")
        val currentUser = _currentUser.value
        if(currentUser == null){
            Log.d("Firebase", "Current user is null")
            return
        }
        Log.d("Firebase", "Current user: ${currentUser.name}")
        userRepo.addFollower(currentUser, user)
    }

    fun unfollowUser(user: Muser){
        val currentUser = _currentUser.value
        if(currentUser == null){
            Log.d("Firebase", "Current user is null")
            return
        }
        Log.d("Firebase", "Current user: ${currentUser.name}")
        userRepo.removeFollower(currentUser, user)
    }

}

sealed interface FriendsUiState{
    data object Loading : FriendsUiState

    data object EmptyQuery : FriendsUiState

    data object LoadFailed : FriendsUiState

    data class Success(
        val usersSearched : List<Muser> = emptyList()
    ) : FriendsUiState {
        fun isEmpty(): Boolean = usersSearched.isEmpty()
    }

    data object SearchNotReady : FriendsUiState

}


private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 1