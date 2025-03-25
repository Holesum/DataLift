package com.example.datalift.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.datalift.model.userRepo
import com.example.datalift.model.Muser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore

class accountViewModel: ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String = auth.currentUser?.uid.toString()
    private val userRepo = userRepo()

    //user state
    private val _user = MutableStateFlow<Muser?>(null)
    val user: StateFlow<Muser?> = _user

    //user live search state
    private val _users = MutableStateFlow<List<Muser>>(emptyList())
    val users: StateFlow<List<Muser>> = _users

    //alt user aka friend user state
    private val _altUser = MutableStateFlow<Muser?>(null)
    val altUser: StateFlow<Muser?> = _altUser


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun getUser(){
        userRepo.getUser(uid){
            _user.value = it
        }
    }

    fun changePrivacy(privacy: Boolean){
        userRepo.changePrivacy(uid, privacy)
    }

    fun changeImperial(imperial: Boolean){
        userRepo.changeImperial(uid, imperial)
    }

    fun changeName(name: String){
        userRepo.changeName(uid, name)
    }

    fun searchUsers(query: String = ""){
        userRepo.getUsers(query){
            _users.value = it
        }
    }

    fun getUserByUsername(username: String){
        userRepo.getUserByUsername(username){
            _altUser.value = it
        }
    }

    fun addFollower(){
        userRepo.addFollower(user.value!!, altUser.value!!)
    }

    fun removeFollower(){
        userRepo.removeFollower(user.value!!, altUser.value!!)
    }


    fun logWeight(weight: Double) {
        _loading.value = true
        db.collection("Users").document(uid).update("weight", weight)
            .addOnSuccessListener{
                _loading.value = false
            }
            .addOnFailureListener{
                _errorMessage.value = "Failed to update weight"
                _loading.value = false
            }

    }
}