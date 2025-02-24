package com.example.datalift.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class analysisRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getWorkoutProgression(uid: String, callback: (List<Manalysis>) -> Unit){
        Log.d("Firebase", "Running Progression")
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .get()
            .addOnSuccessListener { snapShot ->
                Log.d("Firebase", "Workout progression not working")
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

    fun getAnalyzedExercises(uid: String, callback: (List<MexerAnalysis>) -> Unit){
        db.collection("Users")
            .document(uid)
            .collection("AnalyzedExercises")
            .get()
            .addOnSuccessListener { snapShot ->
                val analysisList = mutableListOf<MexerAnalysis>()
                for(document in snapShot.documents){
                    val analysis = document.toObject(MexerAnalysis::class.java)
                    if(analysis != null){
                        analysisList.add(analysis)
                    }
                }
            }.addOnFailureListener{
                Log.d("Firebase", "Error getting analysis returning empty list")
                callback(emptyList()) // Call the callback with an empty list on error
            }
    }
}