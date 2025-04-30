package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.AnalysisRepository
import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.data.repository.WorkoutRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GoalRepo @Inject constructor(
    private val analysisRepo: AnalysisRepository,
    private val workoutRepository: WorkoutRepository
) : GoalRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createGoal(uid: String, goal: Mgoal, callback: (Mgoal?) -> Unit) {
        val exerciseList = MutableStateFlow<List<MexerAnalysis>>(emptyList())
        val workoutList = MutableStateFlow<List<Mworkout>>(emptyList())
        workoutRepository.getWorkouts(uid) { workouts ->
            workoutList.value = workouts
        }
        analysisRepo.getAnalyzedExercises(uid) { exercises ->
            exerciseList.value = exercises

            if (goal.type == GoalType.INCREASE_ORM_BY_PERCENTAGE) {
                goal.targetValue = 100 + goal.targetPercentage!!.toInt()
                goal.currentValue = 100
            }
            if (goal.type == GoalType.INCREASE_ORM_BY_VALUE) {
                val analysis = exerciseList.value.find {
                    it.exerciseName.equals(
                        goal.exerciseName,
                        ignoreCase = true
                    )
                }
                val progression = analysis!!.progression.sortedByDescending { it.progressionMultiplier }
                val multiplier = progression.first().progressionMultiplier
                goal.targetValue = (analysis.initialAvgORM * multiplier + goal.targetValue).toInt()
            }
            if (goal.type == GoalType.COMPLETE_X_REPS_OF_EXERCISE) {
                val analysis = exerciseList.value.find {
                    it.exerciseName.equals(
                        goal.exerciseName,
                        ignoreCase = true
                    )
                }
                goal.trueTargetValue = goal.targetValue
                goal.targetValue += analysis!!.repCount.toInt()
            }
            if (goal.type == GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART) {
                val analysisList = workoutList.value.filter {
                    it.muscleGroup.equals(
                        goal.bodyPart,
                        ignoreCase = true
                    )
                }
                val numWorkouts = analysisList.size
                goal.trueTargetValue = goal.targetValue
                goal.targetValue += numWorkouts
            }

            if (goal.type == GoalType.COMPLETE_X_WORKOUTS) {
                val numWorkouts = workoutList.value.size
                goal.trueTargetValue = goal.targetValue
                goal.targetValue += numWorkouts
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

    override fun deleteGoal(uid: String, goal: Mgoal, callback: (Boolean) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Goals")
            .document(goal.docID)
            .delete()
    }

    override fun evaluateGoals(uid: String, exerciseAnalysis: List<MexerAnalysis>, workouts: List<Mworkout>, callback: (Boolean) -> Unit) {
        analysisRepo.evaluateGoals(uid, exerciseAnalysis, workouts) {
            Log.d("GoalRepo", "Goals evaluated")
            callback(true)
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
