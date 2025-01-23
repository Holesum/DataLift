package com.example.datalift.screens.logIn

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.datalift.model.Muser
import com.google.firebase.firestore.FirebaseFirestore

class LogInViewModel() : ViewModel() {
    private val db: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    val updateUsername: (String) -> Unit = { newUsername ->
        username = newUsername
    }

    val updatePassword: (String) -> Unit = { newPassword ->
        password = newPassword
    }

    //add in things for a loading buffer, a account create success, and email verification


    private fun createDBUser(uid: String, email: String, name: String,
                            height: Number, weight: Number, privacy: Boolean,
                            imperial: Boolean, password: String){
        db.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val displayName = task.result?.user?.email?.split('@')?.get(0).toString()
                    createUser(uid, email, name, height, weight, privacy, imperial, displayName)
                }
            }
    }

    private fun createUser(
        email: String,
        password: String,
        name: String,
        height: Number,
        weight: Number,
        privacy: Boolean,
        imperial: Boolean,
        uname: String
    ){
        val userId = db.currentUser?.uid
        val user = Muser(
            uid = userId.toString(),
            uname = uname,
            email = email,
            name = name,
            height = height,
            weight = weight,
            privacy = privacy,
            imperial = imperial,
            friends = mutableListOf<String>()
        ).toMap()

        Log.d("firebase", "$user")

        FirebaseFirestore.getInstance().collection("users")
            .document(uname)
            .set(user.toMap())
            .addOnSuccessListener {Log.d("Firebase", "Create user success $uname")}
            .addOnFailureListener{Log.d("Firebase", "Failed to create user")}
    }
}
