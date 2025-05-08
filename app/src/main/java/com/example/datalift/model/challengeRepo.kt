package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.AnalysisRepository
import com.example.datalift.data.repository.ChallengeRepository
import com.example.datalift.navigation.getCurrentUserId
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class challengeRepo @Inject constructor(
    private val analysisRepo: AnalysisRepository,
    private val userRepo: userRepo
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
        val userChallengeRefs = db.collection("Users").document(uid).collection("Challenges")

        userChallengeRefs.get()
            .addOnSuccessListener { snapshot ->
                val challengeRefs = snapshot.documents.mapNotNull { it.getDocumentReference("challengeRef") }

                if (challengeRefs.isEmpty()) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                val challenges = mutableListOf<Mchallenge>()
                var completed = 0

                challengeRefs.forEach { ref ->
                    ref.get()
                        .addOnSuccessListener { doc ->
                            doc.toObject(Mchallenge::class.java)?.let { challenges.add(it) }
                            completed++
                            if (completed == challengeRefs.size) {
                                callback(challenges)
                            }
                        }
                        .addOnFailureListener {
                            completed++
                            if (completed == challengeRefs.size) {
                                callback(challenges)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to fetch challenge references: ${e.message}")
                callback(emptyList())
            }
    }

    override fun getChallengesForCurrentUser(): /*StateFlow<*/List<Mchallenge>/*>*/ {
        val uid = getCurrentUserId()
        //val challenges = MutableStateFlow<List<Mchallenge>>(emptyList())
        var challenges: List<Mchallenge> = emptyList()
        getChallengesForUser(uid) { list ->
            challenges/*.value*/ = list
        }
        return challenges
    }


    override fun getChallenge(challengeId: String, callback: (Mchallenge?) -> Unit) {
        db.collection("Challenges")
            .document(challengeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val challenge = document.toObject(Mchallenge::class.java)
                    callback(challenge)
                } else {
                    callback(null)
                }
            }

    }

    override fun deleteChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit) {
        getChallenge(challengeId) { challenge ->
            val participants = challenge?.participants ?: emptyList()

            val batch = db.batch()

            // Remove challenge ref from each participant's user collection
            for (user in participants) {
                val userChallengeRef = db.collection("Users")
                    .document(user.uid)
                    .collection("Challenges")
                    .document(challengeId)
                batch.delete(userChallengeRef)
            }

            // Remove the actual challenge document
            val challengeRef = db.collection("Challenges").document(challengeId)
            batch.delete(challengeRef)

            batch.commit()
                .addOnSuccessListener {
                    Log.d("ChallengeRepo", "Challenge $challengeId and user references deleted.")
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.e("ChallengeRepo", "Failed to delete challenge and references: ${e.message}")
                    callback(false)
                }
        }
    }

    override fun updateChallenge(uid: String, challengeId: String, challenge: Mchallenge, callback: (Boolean) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Challenges")
            .document(challengeId)
            .set(challenge)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to update challenge: ${e.message}")
                callback(false)
            }
    }


    //Add user to challenge
    override fun addUserToChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit) {
        var challenge: Mchallenge = Mchallenge()
        getChallenge(challengeId){
            challenge = it!! //non-null assertion find better response
        }
        val goal = challenge.goal
        val challengeDocRef = db.collection("Challenges").document(challengeId)

        // Step 1: Add user to the participants array
        challengeDocRef.update("participants", FieldValue.arrayUnion(uid))
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to add user to participants: ${e.message}")
                callback(false)
                return@addOnFailureListener
            }

        // Step 2: Add an entry for the user's progress
        val progressField = "progress.$uid"


        // Step 3: Get users current val aka starting val
        var startingValue = 0
        when (goal.type){
            GoalType.INCREASE_ORM_BY_VALUE -> {
                analysisRepo.getAnalyzedExercises(uid){ exercises ->
                    val analysis = exercises.find {
                        it.exerciseName.equals(goal.exerciseName, ignoreCase = true)
                    }
                    if(analysis != null){
                        startingValue = analysis.progression.maxByOrNull { it.progressionMultiplier }
                            ?.progressionMultiplier?.times(analysis.initialAvgORM)?.toInt() ?: 0
                    }
                }
            }
            GoalType.INCREASE_ORM_BY_PERCENTAGE -> {
                startingValue = 0
            }
            GoalType.COMPLETE_X_REPS_OF_EXERCISE -> {
                analysisRepo.getAnalyzedExercises(uid) { exercises ->
                    val analysis = exercises.find {
                        it.exerciseName.equals(goal.exerciseName, ignoreCase = true)
                    }
                    if (analysis != null) {
                        startingValue = analysis.repCount.toInt()
                    }
                }
            }
            GoalType.COMPLETE_X_WORKOUTS -> {
                startingValue = 0

            }
            GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART -> {
                startingValue = 0
            }
            else ->  {
                startingValue = 0
            }

        }


        val newProgress = ChallengeProgress(startingValue, startingValue, false)

        challengeDocRef.update(progressField, newProgress)
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to add user to progress map: ${e.message}")
                callback(false)
                return@addOnFailureListener
            }

        // Step 3: Add challenge reference to the user's Challenges subcollection
        val userChallengeRef = db.collection("Users").document(uid)
            .collection("Challenges").document(challengeId)

        userChallengeRef.set(mapOf("challengeRef" to challengeDocRef))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to set user challenge ref: ${e.message}")
                callback(false)
            }
    }

    //Remove user from challenge

    //

    override fun evaluateChallenges(
        uid: String,
        workouts: List<Mworkout>,
        onComplete: () -> Unit
    ) {
        var exerciseAnalysis: List<MexerAnalysis> = emptyList()
        analysisRepo.getAnalyzedExercises(uid){ analysis ->
            exerciseAnalysis = analysis
        }

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
                            val analysis = exerciseAnalysis.find {
                                it.exerciseName.equals(goal.exerciseName, ignoreCase = true)
                            }
                            if (analysis != null) {
                                val latest = analysis.progression.maxByOrNull { it.progressionMultiplier }
                                    ?.progressionMultiplier?.times(analysis.initialAvgORM) ?: 0.0
                                val required = analysis.initialAvgORM + goal.targetValue
                                updatedProgress.currentValue = latest.toInt()
                                if (latest >= required) {
                                    updatedProgress.isComplete = true
                                }
                            }
                        }

                        GoalType.INCREASE_ORM_BY_PERCENTAGE -> {
                            val analysis = exerciseAnalysis.find {
                                it.exerciseName.equals(goal.exerciseName, ignoreCase = true)
                            }
                            if (analysis != null && goal.targetPercentage != null) {
                                val latest = analysis.progression.maxByOrNull { it.progressionMultiplier }
                                    ?.progressionMultiplier?.times(analysis.initialAvgORM) ?: 0.0
                                val required = analysis.initialAvgORM * (1 + goal.targetPercentage!! / 100.0)
                                val currentPercent = ((latest / analysis.initialAvgORM - 1) * 100).toInt().coerceAtLeast(0)
                                updatedProgress.currentValue = currentPercent
                                if (latest >= required) {
                                    updatedProgress.isComplete = true
                                }
                            }
                        }

                        GoalType.COMPLETE_X_WORKOUTS -> {
                            val count = workouts.count { it.date > goal.createdAt }
                            updatedProgress.currentValue = count
                            if (count >= (goal.targetValue)) {
                                updatedProgress.isComplete = true
                            }
                        }

                        GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART -> {
                            val count = workouts.count {
                                it.muscleGroup.equals(goal.bodyPart, ignoreCase = true) &&
                                        it.date > goal.createdAt
                            }
                            updatedProgress.currentValue = count
                            if (count >= (goal.targetValue)) {
                                updatedProgress.isComplete = true
                            }
                        }

                        GoalType.COMPLETE_X_REPS_OF_EXERCISE -> {
                            val analysis = exerciseAnalysis.find {
                                it.exerciseName.equals(goal.exerciseName, ignoreCase = true)
                            }
                            if (analysis != null) {
                                val currReps = analysis.repCount.toInt()
                                updatedProgress.currentValue = currReps
                                if (currReps >= goal.targetValue) {
                                    updatedProgress.isComplete = true
                                }
                            }
                        }

                        else -> Unit
                    }

                    // If just now completed, add timestamp
                    if (updatedProgress.isComplete && !userProgress.isComplete) {
                        updatedProgress = updatedProgress.copy(completionTimestamp = Timestamp.now())
                        Log.d("ChallengeUpdate", "User $uid just completed challenge ${challenge.title}")
                    }

                    // Only update if progress changed
                    if (updatedProgress != userProgress) {
                        Log.d(
                            "ChallengeUpdate",
                            "Updating progress for user $uid in challenge ${challenge.title}. " +
                                    "Old: $userProgress | New: $updatedProgress"
                        )
                        val updatedProgressMap = challenge.progress.toMutableMap()
                        updatedProgressMap[uid] = updatedProgress
                        batch.update(document.reference, "progress", updatedProgressMap)
                    } else {
                        Log.d(
                            "ChallengeUpdate",
                            "No change for user $uid in challenge ${challenge.title}, skipping update."
                        )
                    }
                }

                batch.commit()
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { e ->
                        Log.e("ChallengeRepo", "Failed to update challenges: ${e.message}")
                        onComplete()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to fetch challenges: ${e.message}")
                onComplete()
            }
    }


}
