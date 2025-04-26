package com.example.datalift.screens.challenges

//Firebase
import android.util.Log
import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.model.GoalType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.DocumentReference

//Data models
import com.example.datalift.model.challengeRepo
import com.example.datalift.model.userRepo
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.goalRepo
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class challengesViewModel {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()
    private val challengeRepo = challengeRepo()
    private val userRepo = userRepo()
    //private val goalRepo = GoalRepository()

    //State Flows
    private val _user = MutableStateFlow<DocumentReference?>(null)
    val user: StateFlow<DocumentReference?> get() = _user

    private val _users = MutableStateFlow<List<DocumentReference>>(emptyList())
    val users: StateFlow<List<DocumentReference>> get() = _users

    private val _challengeName = MutableStateFlow<String>("")
    val challengeName: StateFlow<String> get() = _challengeName


    fun createChallengeObject(name: String, details: String, members: List<DocumentReference>){

    }

    fun createChallenge() {
        try {
            Log.d("Firebase", "now attempting to add challenge")
//            challengeRepo.createChallenge(Mchallenge( //Fix to use the new challenge model
//                "test",
//                "test",
//                _users.value,
//                db.collection("Users").document(uid),
//                Timestamp.now(),
//                emptyList()
//                )
//            )

        } catch (e: Exception) {
            Log.d("Firebase", "Error creating challenge: ${e.message}")
        }
    }
}