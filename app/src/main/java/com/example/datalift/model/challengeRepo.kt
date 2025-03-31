package com.example.datalift.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class challengeRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    /*
    steps that need to be followed
    1: create a challenge object and add the curr user as the owner through a doc ref somehow, then add them to the user list
    2: Go back into user and add a doc ref to the challenge in the database
    3:
     */
    fun createChallenge(challenge: Mchallenge){
        Log.d("Firebase", "Attempting to create challenge 4")
        db.collection("Challenges")
            .add(challenge)
            .addOnSuccessListener {
                Log.d("Firebase", "Challenge created")
            }.addOnFailureListener {
                Log.d("Firebase", "Error creating challenge: ${it.message}")
            }
    }
}