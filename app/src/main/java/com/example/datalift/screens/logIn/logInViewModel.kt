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
import com.google.firebase.firestore.FirebaseFirestore

class LogInViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

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
                            imperial: Boolean, password: String) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uname = task.result?.user?.email?.split('@')?.get(0).toString()
                        createUser(email, name, height, weight, privacy, imperial, uname)
                    } else {
                        _errorMessage.value = "failed to create user"
                    }
                    _loading.value = false
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

        val userId = auth.currentUser?.uid
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
                                    Log.d("Firebase", "User found: ${user.getName()}")
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
