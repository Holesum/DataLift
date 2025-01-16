package com.example.datalift.screens.logIn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.datalift.model.Muser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import java.time.Instant
import java.time.LocalDate

class LogInViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    //add in things for a loading buffer,

    /**
     * Login user with email and password
     *
     * @param email: email of user
     * @param password: Password associated with user account
     */
    fun loginUser(email: String, password: String){
        try {
            _loading.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var uid = auth.currentUser?.uid
                        Log.d("Firebase", "Login success: ${task.result}")
                        FirebaseFirestore.getInstance().collection("Users")
                            .whereEqualTo("uid", uid)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                try {
                                    val user = Muser.fromDocument(snapshot.documents[0])
                                    Log.d("Firebase", "User found: ${user.name}")
                                    /** here user is collected, use nav controller to move forward with this
                                     *
                                     */
                                } catch (e: Exception) {
                                    Log.d("Firebase", "User not found: ${uid}")
                                }
                            }.addOnFailureListener { e ->
                                Log.d("Firebase", "reading failed: ${auth.currentUser?.uid}")
                                _errorMessage.value = "there is an issue logging you in"
                                _loading.value = false
                            }
                    } else {
                        _errorMessage.value = "incorrect email or password"
                        _loading.value = false
                        Log.d("Firebase", "Login failed: incorrect email or password")
                    }
                }
        } catch (e: Exception) {
            _errorMessage.value = e.message
            Log.d("Firebase", "Login failed: ${e.message}")
        }
    }
}
