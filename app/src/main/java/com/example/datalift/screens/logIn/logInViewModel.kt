package com.example.datalift.screens.logIn

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.datalift.model.Muser
import com.google.firebase.firestore.FirebaseFirestore

class LogInViewModel : ViewModel() {
    private var db: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    //add in things for a loading buffer, a account create success, and email verification

    /**
     * Create user account with email and password
     *
     * @param email: email of user
     * @param name: preferred name of user
     * @param height: user height in inches
     * @param weight: user initial weight in lbs
     * @param privacy: boolean does user want their workouts to be public true=public
     * @param imperial: boolean, does the user want weight measurements in imperial or metric
     * @param password: Password associated with user account
     *
     * @return null
     *
     * @see FirebaseAuth.createUserWithEmailAndPassword
     * @see createUser
     */
    fun createDBUser(email: String, name: String,
                            height: Number, weight: Number, privacy: Boolean,
                            imperial: Boolean, password: String){
        Log.d("fun runs", "is the function running")
        db.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val displayName = task.result?.user?.email?.split('@')?.get(0).toString()
                    createUser(email, name, height, weight, privacy, imperial, displayName)
                }
            }
    }


    /**
     * Create a user document in the database rather than the authentication object that was added before
     *
     * @param email: email of user
     * @param name: preferred name of user
     * @param height: user height in inches
     * @param weight: user initial weight in lbs
     * @param privacy: boolean does user want their workouts to be public true=public
     * @param imperial: boolean, does the user want weight measurements in imperial or metric
     * @param uname: username of user, only ever one account with this username
     *
     * @see createDBUser
     */
    private fun createUser(
        email: String,
        name: String,
        height: Number,
        weight: Number,
        privacy: Boolean,
        imperial: Boolean,
        uname: String
    ){
        Log.d("Firebase", "the second call is working")
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

        Log.d("Firebase", "$user")

        FirebaseFirestore.getInstance().collection("Users")
            .document(uname)
            .set(user.toMap())
            .addOnSuccessListener {Log.d("Firebase", "Create user success $uname")}
            .addOnFailureListener{ exception ->
                Log.d("Firebase", "Failed to create user ${exception.message}")}
    }

    /**
     * Login user with email and password
     *
     * @param email: email of user
     * @param password: Password associated with user account
     */
    fun loginUser(email: String, password: String){
        db.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d("Firebase", "Login success: ${task.result}")
                    FirebaseFirestore.getInstance().collection("Users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener {
                            try {
                                val user = Muser.fromDocument(it.documents[0])
                            } catch (e: Exception) {
                                Log.d("Firebase", "User not found")
                            }
                        }
                }
            }


    }
}
