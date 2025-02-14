package com.example.datalift.screens.logIn

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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LogInViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val updateUsername: (String) -> Unit = { newUsername ->
        _uiState.update { currentState ->
            currentState.copy(
                username = newUsername,
                hasErrors = false,
                canLogin = canLogIn(newUsername,currentState.password)
            )
        }
    }

    val updatePassword: (String) -> Unit = { newPassword ->
        _uiState.update { currentState ->
            currentState.copy(
                password = newPassword,
                hasErrors = false,
                canLogin = canLogIn(currentState.username,newPassword)
            )
        }
    }

    fun canLogIn(username: String, password: String) : Boolean {
        return username.isNotBlank() && password.isNotBlank()
    }

    private val _verSent = MutableLiveData(false)
    val verSent: LiveData<Boolean> = _verSent

    private val _verPopup = MutableLiveData(false)
    val verPopup: LiveData<Boolean> = _verPopup

    private val _passwordReset = MutableLiveData(false)
    val passwordReset: LiveData<Boolean> = _passwordReset

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    var snackbarDisplayed by mutableStateOf(false)

    fun displaySnackbar(){
        snackbarDisplayed = true
    }

    fun closeSnackbar(){
        snackbarDisplayed = false
    }

    //add in things for a loading buffer,

    /**
     * Login user with email and password
     *
     * @param email: email of user
     * @param password: Password associated with user account
     */
    fun loginUser(email: String, password: String){
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var uid = auth.currentUser?.uid
                        Log.d("Firebase", "Login success: ${task.result}")
                        if (auth.currentUser?.isEmailVerified == true) {
                            Log.d("Firebase", "curr user ${auth.currentUser?.uid}")
                            if (uid != null) {
                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        try {
                                            val user = Muser.fromDocument(snapshot)
                                            Log.d("Firebase", "User found: ${user.name}")
                                            _loggedIn.value = true
                                            _verPopup.value = false
                                        } catch (e: Exception) {
                                            Log.d("Firebase", "User not found: ${uid}")
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.d("Firebase", "reading failed: ${e.message}")
                                        _errorMessage.value = e.message
                                        _actionMessage.value = null
                                        displaySnackbar()
                                        _uiState.update {newState -> newState.copy(hasErrors = true)}
                                    }
                            }
                        } else {
                            _errorMessage.value = "Please verify your email before logging in."
                            _actionMessage.value = "Resend"
                            displaySnackbar()

                            _uiState.update {newState -> newState.copy(hasErrors = true)}

                            Log.d("Firebase", "Login failed: Email not verified.")
                        }
                    } else {
                        _uiState.update {newState ->
                            newState.copy(
                                errorMessage = "Incorrect email or password",
                                hasErrors = true
                            )
                        }

                        Log.d("Firebase", "Login failed: incorrect email or password")
                    }
                }
        } catch (e: Exception) {
            _errorMessage.value = e.message
            _actionMessage.value = null
            displaySnackbar()
            Log.d("Firebase", "Login failed: ${e.message}")
        }
    }

    fun resendVerificationEmail() {
        _verSent.value = false
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener {
            _verSent.value = true
                Log.d("Firebase", "Verification email sent.")
        }
            ?.addOnFailureListener{
            _verSent.value = false
                _errorMessage.value = "Failed to send verification email: ${it.message}"
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        _passwordReset.value = false
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordReset.value = true
                } else {
                    _passwordReset.value = false
                }
            }
    }

    fun userLogged(){
        _loggedIn.value = false
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val hasErrors: Boolean = false,
    val canLogin: Boolean = false,
)