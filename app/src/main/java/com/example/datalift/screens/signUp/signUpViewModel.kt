package com.example.datalift.screens.signUp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.datalift.model.Muser
import com.example.datalift.model.userWeights
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import java.time.Instant
import java.time.LocalDate

class SignUpViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var name by mutableStateOf("")
        private set

    var weight by mutableStateOf("")
        private set

    var height by mutableStateOf("")
        private set

    var dob:Long? by mutableStateOf(null)
        private set

    var gender by mutableStateOf("")
        private set


    val updateUsername: (String) -> Unit = { newUsername ->
        username = newUsername
    }

    val updateEmail: (String) -> Unit = { newEmail ->
        email = newEmail
    }

    val updatePassword: (String) -> Unit = { newPassword ->
        password = newPassword
    }

    val updateName: (String) -> Unit = { newName ->
        name = newName
    }

    val updateWeight: (String) -> Unit = { newWeight ->
        weight = newWeight
    }

    val updateHeight: (String) -> Unit = { newHeight ->
        height = newHeight
    }

    val updateGender: (String) -> Unit = { newGender ->
        gender = newGender
    }

    val updateDOB: (Long?) -> Unit = { newDOB ->
        dob = newDOB
    }

    // a account create success, and email verification

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
    fun createDBUser(email: String,
                     name: String,
                     gender: String,
                     height: Number,
                     weight: Number,
                     privacy: Boolean,
                     imperial: Boolean,
                     password: String,
                     dob: Instant) {
        if (!_loading.value) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uname = task.result?.user?.email?.split('@')?.get(0).toString()
                        sendEmailVerification()
                        createUser(email, name, gender, height, weight, privacy, imperial, uname, dob)
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
        gender: String,
        height: Number,
        weight: Number,
        privacy: Boolean,
        imperial: Boolean,
        uname: String,
        dob: Instant
    ){

        val userId = auth.currentUser?.uid
        var weightList = mutableListOf<userWeights>()
        weightList.add(userWeights(Timestamp.now(), weight.toDouble()))
        val user = Muser(
            uid = userId.toString(),
            uname = uname,
            email = email,
            name = name,
            gender = gender,
            height = height,
            weight = weight,
            privacy = privacy,
            imperial = imperial,
            dob = Timestamp(dob),
            workouts = mutableListOf<String>(),
            friends = mutableListOf<String>(),
            weights = weightList
        ).toMap()

        Log.d("Firebase", "$user")

        FirebaseFirestore.getInstance().collection("Users")
            .document(userId.toString())
            .set(user.toMap())
            .addOnSuccessListener {Log.d("Firebase", "Create user success $uname")}
            .addOnFailureListener{ exception ->
                Log.d("Firebase", "Failed to create user ${exception.message}")}
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Verification email sent.")
            } else {
                _errorMessage.value = "Failed to send verification email: ${task.exception?.message}"
            }
            _loading.value = false
        }
    }

}