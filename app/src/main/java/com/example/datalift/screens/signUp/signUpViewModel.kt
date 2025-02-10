package com.example.datalift.screens.signUp

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
import com.example.datalift.model.userWeights
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _user = MutableStateFlow<Muser?>(Muser())
    val user: StateFlow<Muser?> get() = _user

    private val _accountCreated = MutableStateFlow(false)
    val accountCreated: StateFlow<Boolean> get() = _accountCreated

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

    var nameInvalid by mutableStateOf(false)
    var genderInvalid by mutableStateOf(false)
    var weightInvalid by mutableStateOf(false)
    var heightInvalid by mutableStateOf(false)
    var usernameInvalid by mutableStateOf(false)
    var passwordInvalid by mutableStateOf(false)
    var emailInvalid by mutableStateOf(false)

    private val heightRegex = Regex("^[0-9]*$")
    private val weightRegex = Regex("^[0-9]*[.]?[0-9]?$")

    val updateUsername: (String) -> Unit = { newUsername ->
        _user.value = _user.value?.copy(uname = newUsername)
        username = newUsername
    }

    val updateEmail: (String) -> Unit = { newEmail ->
        _user.value = _user.value?.copy(email = newEmail)
        Log.d("Firebase", "Email: ${_user.value?.email}")
        email = newEmail
    }

    val updatePassword: (String) -> Unit = { newPassword ->
        Log.d("Firebase", "Password: $newPassword")
        password = newPassword
    }

    val updateName: (String) -> Unit = { newName ->
        _user.value = _user.value?.copy(name = newName)
        name = newName
    }
//----------------------------------------------------------------
    val updateWeight: (String) -> Unit = { newWeight ->
        if(newWeight.matches(weightRegex)){
            if(newWeight.isNotEmpty()) {
                _user.value = _user.value?.copy(weight = newWeight.toDouble())
            }
            weight = newWeight
        }
    }

    val updateHeight: (String) -> Unit = { newHeight ->
        if(newHeight.isEmpty() || newHeight.matches(heightRegex)){
            if(newHeight.isNotEmpty()){
                _user.value = _user.value?.copy(height = newHeight.toDouble())
            }
            height = newHeight
        }
    }

    val updateGender: (String) -> Unit = { newGender ->
        _user.value = _user.value?.copy(gender = newGender)
        gender = newGender
    }

    val updateDOB: (Long?) -> Unit = { newDOB ->

     //   _user.value = _user.value?.copy(dob = newDOB)
        dob = newDOB
    }

    fun nameValidated() : Boolean{
        if(name.isNotBlank()){
            nameInvalid = false
            return true
        } else {
            nameInvalid = true
            return false
        }
    }

    fun personalCredentialsValidated(): Boolean {
        var ret = true

        if(gender == ""){
            ret = false
            genderInvalid = true
        } else {
            genderInvalid = false
        }

        if(weight == ""){
            ret = false
            weightInvalid = true
        } else {
            weightInvalid = false
        }

        if(height == ""){
            ret = false
            heightInvalid = true
        } else {
            heightInvalid = false
        }

        return ret
    }

    fun accountInformationValidated(): Boolean {
        return true
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
    fun naving() {
        _accountCreated.value = false
    }

    fun createDBUser(/**email: String,
                     name: String,
                     gender: String,
                     height: Double,
                     weight: Double,
                     privacy: Boolean,
                     imperial: Boolean,
                     password: String,
                     dob: Timestamp**/
    ) {
        if (!_loading.value) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(user.value?.email!!, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //val uname = task.result?.user?.email?.split('@')?.get(0).toString()
                        _accountCreated.value = true
                        createUser()
                        sendEmailVerification()
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
        /**email: String,
        name: String,
        gender: String,
        height: Double,
        weight: Double,
        privacy: Boolean,
        imperial: Boolean,
        uname: String,
        dob: Instant**/
    ){

        val userId = auth.currentUser?.uid
        val weightList = mutableListOf<userWeights>()
        weightList.add(userWeights(Timestamp.now(), weight.toDouble()))
        /**val user = Muser(
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
        ).toMap()**/
        _user.value = user.value?.copy(uid = userId.toString())
        _user.value = user.value?.copy(weights = weightList)
        Log.d("Firebase", "$user")

        FirebaseFirestore.getInstance().collection("Users")
            .document(userId.toString())
            .set(user.value!!)
            .addOnSuccessListener {
                Log.d("Firebase", "Create user success $user.uname")
            }
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