package com.example.datalift.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore

class accountViewModel: ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String = auth.currentUser?.uid.toString()


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

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