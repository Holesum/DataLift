package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.GoalRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class goalRepo @Inject constructor(

) : GoalRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createGoal(uid: String, goal: Mgoal, callback: (Mgoal?) -> Unit) {
        val goalRef = db.collection("Users")
            .document(uid)
            .collection("Goals")

        // Add the goal first to generate a document ID
        goalRef.add(goal)
            .addOnSuccessListener { docRef ->
                val goalWithID = goal.copy(docID = docRef.id)

                // Set the document again with the correct docID field
                goalRef.document(docRef.id).set(goalWithID)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Goal created with ID: ${docRef.id}")
                        callback(goalWithID)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Failed to update goal with ID: ${e.message}")
                        callback(null)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error creating goal: ${e.message}")
                callback(null)
            }
    }
}
