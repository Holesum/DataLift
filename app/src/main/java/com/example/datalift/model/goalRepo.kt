package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.AnalysisRepository
import com.example.datalift.data.repository.GoalRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class goalRepo @Inject constructor(
    private val analysisRepo: AnalysisRepository
) : GoalRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createGoal(uid: String, goal: Mgoal, callback: (Mgoal?) -> Unit) {
        val exerciseList = MutableStateFlow<List<MexerAnalysis>>(emptyList())
        analysisRepo.getAnalyzedExercises(uid) { exercises ->
            exerciseList.value = exercises

            if (goal.type == GoalType.INCREASE_ORM_BY_PERCENTAGE) {
                goal.targetValue = 100 + goal.targetPercentage!!.toInt()
            }
            if (goal.type == GoalType.INCREASE_ORM_BY_VALUE) {
                val analysis = exerciseList.value.find {
                    it.exerciseName.equals(
                        goal.exerciseName,
                        ignoreCase = true
                    )
                }
                goal.targetValue = (analysis!!.initialAvgORM + goal.targetValue).toInt()
            }
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

    override fun getGoalsForUser(uid: String, callback: (List<Mgoal>) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Goals")
            .get()
            .addOnSuccessListener { snapshot ->
                val goals = snapshot.documents.mapNotNull { it.toObject(Mgoal::class.java) }
                callback(goals)
            }
            .addOnFailureListener { e ->
                Log.e("GoalRepo", "Failed to fetch goals: ${e.message}")
                callback(emptyList())
            }
    }
}
