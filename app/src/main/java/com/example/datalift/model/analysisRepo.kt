package com.example.datalift.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class analysisRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getWorkoutProgression(uid: String, callback: (List<Manalysis>) -> Unit){
        db.collection("Users")
            .document(uid)
            .collection("WorkoutProgressions")
            .get()
            .addOnSuccessListener { snapShot ->
                val progressionList = mutableListOf<Manalysis>()
                for(document in snapShot.documents){
                    val progression = document.toObject(Manalysis::class.java)
                    if(progression != null){
                        progressionList.add(progression)
                    }
                }
                Log.d("Firebase", "Workout progression found: ${progressionList.size}")
                callback(progressionList)
            }.addOnFailureListener { e ->
            Log.d("Firebase", "Error getting progression returning empty list: ${e.message}")
            callback(emptyList()) // Call the callback with an empty list on error
            }
    }
}