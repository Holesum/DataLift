package com.example.datalift.screens.signUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SignUpViewModel() : ViewModel() {
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

}