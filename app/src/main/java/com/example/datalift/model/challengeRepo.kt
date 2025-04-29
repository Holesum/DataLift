package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.AnalysisRepository
import com.example.datalift.data.repository.ChallengeRepository
import com.example.datalift.navigation.getCurrentUserId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class challengeRepo @Inject constructor(
    private val analysisRepo: AnalysisRepository,
    private val userRepo: userRepo,
) : ChallengeRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createChallenge(uid: String, challenge: Mchallenge, callback: (Mchallenge?) -> Unit) {
        val challengeRef = db.collection("Challenges")

        // Add the challenge to generate a document ID
        challengeRef.add(challenge)
            .addOnSuccessListener { docRef ->
                val challengeWithID = challenge.copy(challengeId = docRef.id)

                // Set the document again with the correct challengeId field
                challengeRef.document(docRef.id).set(challengeWithID)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Challenge created with ID: ${docRef.id}")
                        addUserToChallenge(uid, docRef.id) {}
                        callback(challengeWithID)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Failed to update challenge with ID: ${e.message}")
                        callback(null)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error creating challenge: ${e.message}")
                callback(null)
            }
    }

    override fun getChallengesForUser(uid: String, callback: (List<Mchallenge>) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Challenges")
            .get()
            .addOnSuccessListener { snapshot ->
                val challenges = snapshot.documents.mapNotNull { it.toObject(Mchallenge::class.java) }
                callback(challenges)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to fetch challenges: ${e.message}")
                callback(emptyList())
            }
    }

    override fun getChallengesForCurrentUser(): StateFlow<List<Mchallenge>> {
        val uid = getCurrentUserId()
        val _challenges = MutableStateFlow<List<Mchallenge>>(emptyList())
        getChallengesForUser(uid){ list ->
            _challenges.value = list
        }
        return _challenges
    }


    fun getChallenge(challengeId: String, callback: (Mchallenge?) -> Unit) {
        db.collection("Challenges")
            .document(challengeId)
            .get()

    }

    override fun deleteChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit) {
        getChallenge(challengeId){
            for(user in it?.participants ?: emptyList()){
                removeRef(uid, challengeId){}
            }
            db.collection("Challenges")
                .document(challengeId)
                .delete()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.e("ChallengeRepo", "Failed to delete challenge: ${e.message}")
                    callback(false)
                }
        }
    }

    private fun removeRef(uid: String, challengeId: String, callback: (Boolean) -> Unit){
        db.collection("Users").document(uid).collection("Challenges").document(challengeId).delete()
    }

    override fun updateChallenge(uid: String, challengeId: String, challenge: Mchallenge, callback: (Boolean) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Challenges")
            .document(challengeId)
            .set(challenge)
    }


    //Add user to challenge
    fun addUserToChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit) {
        db.collection("Challenges").document(challengeId).update("users", FieldValue.arrayUnion(uid))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to add user to challenge: ${e.message}")
                callback(false)
            }

        val challengeRef = db.collection("Challenges").document(challengeId)

        db.collection("Users").document(uid)
            .collection("Challenges")
            .document(challengeId)
            .set(mapOf("challengeRef" to challengeRef))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to add user to challenge: ${e.message}")
                callback(false)
            }
    }

    //Remove user from challenge

    //

    fun evaluateChallenges(uid: String, exerciseAnalysis: List<MexerAnalysis>, workouts: List<Mworkout>, onComplete: () -> Unit) {
        val challengeRef = db.collection("Challenges")

        challengeRef.whereArrayContains("participants", uid).get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()

                snapshot.documents.forEach { document ->
                    val challenge = document.toObject(Mchallenge::class.java) ?: return@forEach

                    val goal = challenge.goal
                    val userProgress = challenge.progress[uid] ?: ChallengeProgress()

                    var updatedProgress = userProgress.copy()

                    when (goal.type) {
                        GoalType.INCREASE_ORM_BY_VALUE -> {
                            val analysis = exerciseAnalysis.find { it.exerciseName.equals(goal.exerciseName, ignoreCase = true) }
                            if (analysis != null && goal.targetValue != null) {
                                val latest = analysis.progression.maxByOrNull { it.progressionMultiplier }?.progressionMultiplier?.times(analysis.initialAvgORM) ?: 0.0
                                val required = analysis.initialAvgORM + goal.targetValue
                                updatedProgress.isComplete = latest >= required
                                updatedProgress.currentValue = latest.toInt()
                            }
                        }

                        GoalType.INCREASE_ORM_BY_PERCENTAGE -> {
                            val analysis = exerciseAnalysis.find { it.exerciseName.equals(goal.exerciseName, ignoreCase = true) }
                            if (analysis != null && goal.targetPercentage != null) {
                                val latest = analysis.progression.maxByOrNull { it.progressionMultiplier }?.progressionMultiplier?.times(analysis.initialAvgORM) ?: 0.0
                                val required = analysis.initialAvgORM * (1 + goal.targetPercentage!! / 100.0)
                                updatedProgress.isComplete = latest >= required
                                updatedProgress.currentValue = ((latest / analysis.initialAvgORM - 1) * 100).toInt().coerceAtLeast(0)
                            }
                        }

                        GoalType.COMPLETE_X_WORKOUTS -> {
                            val goalCreatedAt = goal.createdAt
                            val count = workouts.count { it.date > goalCreatedAt }
                            updatedProgress.isComplete = count >= (goal.targetValue ?: 0)
                            updatedProgress.currentValue = count
                        }

                        GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART -> {
                            val goalCreatedAt = goal.createdAt
                            val count = workouts.count {
                                it.muscleGroup.equals(goal.bodyPart, ignoreCase = true) &&
                                        it.date > goalCreatedAt
                            }
                            updatedProgress.isComplete = count >= (goal.targetValue ?: 0)
                            updatedProgress.currentValue = count
                        }

                        GoalType.COMPLETE_X_REPS_OF_EXERCISE -> {
                            val analysis = exerciseAnalysis.find { it.exerciseName.equals(goal.exerciseName, ignoreCase = true) }
                            if (analysis != null) {
                                updatedProgress.isComplete = (analysis.repCount >= (goal.targetValue ?: 0))
                                updatedProgress.currentValue = analysis.repCount.toInt()
                            }
                        }

                        else -> {
                            // No update needed
                        }
                    }

                    val updatedProgressMap = challenge.progress.toMutableMap()
                    updatedProgressMap[uid] = updatedProgress

                    val challengeDocRef = document.reference
                    batch.update(challengeDocRef, "progress", updatedProgressMap)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onComplete()
                    }
                    .addOnFailureListener { e ->
                        Log.e("ChallengeRepo", "Failed to update challenges: ${e.message}")
                        onComplete() // still call onComplete to avoid hanging
                    }
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to fetch challenges: ${e.message}")
                onComplete() // still call onComplete to avoid hanging
            }
    }


}
