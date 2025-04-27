package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.AnalysisRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

class analysisRepo @Inject constructor(

) : AnalysisRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getWorkoutProgression(uid: String, callback: (List<Manalysis>) -> Unit){
        Log.d("Firebase", "Running Progression")
        db.collection("Users")
            .document(uid)
            .collection("WorkoutProgressions")
            .get()
            .addOnSuccessListener { snapShot ->
                Log.d("Firebase", "Workout progression working")
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

    override fun getAnalyzedExercises(uid: String, callback: (List<MexerAnalysis>) -> Unit){
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
                callback(analysisList)
            }.addOnFailureListener{
                Log.d("Firebase", "Error getting analysis returning empty list")
                callback(emptyList()) // Call the callback with an empty list on error
            }
    }

    override fun analyzeWorkouts(
        uid: String,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val dateRange = 30
        val currentDate = LocalDateTime.now()
        val startDate = currentDate.minusDays(dateRange.toLong())
        val startDateTimestamp = Timestamp(startDate.atZone(ZoneId.systemDefault()).toInstant())

        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .whereGreaterThan("date", startDateTimestamp)
            .get()
            .addOnSuccessListener { workouts ->

                val exerciseData = mutableMapOf<String, MutableMap<String, Any>>()
                val workoutProgressions = mutableMapOf<String, MutableMap<String, Any>>()

                for (workout in workouts.documents) {
                    val workoutData = workout.data ?: continue
                    val workoutTimestamp = workoutData["date"] as? Timestamp
                    val workoutDate = workoutTimestamp?.toDate() ?: run {
                        Log.w("WorkoutDateMissing", "Workout ${workout.id} missing date, using default")
                        Date()
                    }
                    val workoutType = workoutData["muscleGroup"] as? String ?: "Unknown Type"

                    workoutProgressions[workout.id] = mutableMapOf(
                        "totalProgression" to 0.0,
                        "exerciseCount" to 0,
                        "date" to workoutDate,
                        "muscleGroup" to workoutType
                    )

                    val exercises = workoutData["exercises"] as? List<Map<String, Any>> ?: continue
                    for (exercise in exercises) {
                        val exerciseName = exercise["name"] as? String ?: "Unknown Exercise"
                        val avgORM = exercise["avgORM"] as? Double ?: 0.0
                        val exerObject = exercise["exercise"] as? Map<String, Any> ?: emptyMap()
                        val bodyPart = exerObject["bodyPart"] as? String ?: "unknown"
                        var repCount = 0.0
                        Log.d("Firebase", "sets: ${exercise["sets"]}")
                        for(set in exercise["sets"] as List<Map<String, Any>>){
                            val rep = set["rep"] as? Long ?: 0;
                            repCount += rep
                        }

                        if (avgORM > 0) {
                            if (!exerciseData.containsKey(exerciseName)) {
                                exerciseData[exerciseName] = mutableMapOf(
                                    "initialAvgORM" to avgORM,
                                    "progression" to mutableListOf<Map<String, Any>>(),
                                    "bodyPart" to bodyPart,
                                    "repCount" to repCount
                                )
                            }

                            val initialAvgORM = exerciseData[exerciseName]?.get("initialAvgORM") as? Double ?: avgORM
                            val progressionMultiplier = avgORM / initialAvgORM

                            (exerciseData[exerciseName]?.get("progression") as? MutableList<Map<String, Any>>)?.add(
                                mapOf(
                                    "workoutId" to workout.id,
                                    "date" to workoutDate,
                                    "progressionMultiplier" to progressionMultiplier
                                )
                            )
                            Log.d("ProgressionDateCheck", "Adding progression for $exerciseName on $workoutDate")

                            workoutProgressions[workout.id]?.let {
                                it["totalProgression"] = (it["totalProgression"] as Double) + progressionMultiplier
                                it["exerciseCount"] = (it["exerciseCount"] as Int + 1)
                            }
                        }
                    }
                }

                // Save workout progressions
                val batch = db.batch()
                workoutProgressions.forEach { (workoutId, data) ->
                    val exerciseCount = data["exerciseCount"] as Int
                    if (exerciseCount > 0) {
                        data["totalProgression"] = (data["totalProgression"] as Double) / exerciseCount
                    }
                    val docRef = db.collection("Users").document(uid)
                        .collection("WorkoutProgressions").document(workoutId)
                    batch.set(docRef, data)
                }

                // Save exercise progression
                val exerciseRef = db.collection("Users").document(uid).collection("AnalyzedExercises")
                exerciseData.forEach { (name, data) ->
                    val docRef = exerciseRef.document(name)
                    data["exerciseName"] = name // 👈 Add this
                    batch.set(docRef, data)
                }

                batch.commit()
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { onFailure(it) }

            }.addOnFailureListener { onFailure(it) }
    }

    override fun evaluateGoals(uid: String, exerciseAnalysis: List<MexerAnalysis>, workouts: List<Mworkout>, onComplete: () -> Unit) {
        val goalRef = db.collection("Users").document(uid).collection("Goals")

        goalRef.get().addOnSuccessListener { snapshot ->
            val goals = snapshot.documents.mapNotNull { it.toObject(Mgoal::class.java) }

            goals.forEach { goal ->
                var updatedGoal = goal.copy()


                when (goal.type) {
                    GoalType.INCREASE_ORM_BY_VALUE -> {
                        val analysis = exerciseAnalysis.find { it.exerciseName.equals(goal.exerciseName, ignoreCase = true) }
                        if (analysis != null && goal.targetValue != null) {
                            val progression = analysis.progression.sortedByDescending { it.progressionMultiplier }
                            val latest = progression.first().progressionMultiplier.times(analysis.initialAvgORM)
                            val required = analysis.initialAvgORM + goal.targetValue
                            if (latest >= required) {
                                updatedGoal.isComplete = true
                            } else {
                                updatedGoal.currentValue = latest.toInt()
                            }
                        }
                    }

                    GoalType.INCREASE_ORM_BY_PERCENTAGE -> {
                        val analysis = exerciseAnalysis.find { it.exerciseName.equals(goal.exerciseName, ignoreCase = true) }
                        if (analysis != null && goal.targetPercentage != null) {
                            val progression = analysis.progression.sortedByDescending { it.progressionMultiplier }
                            val latest = progression.first().progressionMultiplier.times(analysis.initialAvgORM)
                            val required = analysis.initialAvgORM * (1 + goal.targetPercentage!! / 100.0)
                            if (latest >= required) {
                                updatedGoal.isComplete = true
                            } else {
                                val currVal = (progression.first().progressionMultiplier.toInt() * 100) - 100
                                if(currVal >= 0){updatedGoal.currentValue}
                                else{updatedGoal.currentValue = 0}

                            }
                        }
                    }

                    GoalType.COMPLETE_X_WORKOUTS -> {
                        val goalCreatedAt = goal.createdAt // assuming this is a Firebase Timestamp
                        val count = workouts.count {
                            it.date > goalCreatedAt // assuming 'date' is also a Firebase Timestamp
                        }
                        if (count >= goal.targetValue) {
                            updatedGoal.isComplete = true
                        } else {
                            updatedGoal.currentValue = workouts.size
                        }
                    }

                    GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART -> {
                        Log.d("Goal", "goal: $goal")
                        Log.d("Goal", "workoutProgressions: $workouts")
                        val goalCreatedAt = goal.createdAt // assuming this is a Firebase Timestamp
                        val count = workouts.count {
                            it.muscleGroup.equals(goal.bodyPart, ignoreCase = true) &&
                                    it.date > goalCreatedAt // assuming 'date' is also a Firebase Timestamp
                        }
                        Log.d("Goal", "count: $count")
                        if (count >= goal.targetValue) {
                            updatedGoal.isComplete = true
                            updatedGoal.currentValue = count
                        } else {
                            updatedGoal.currentValue = count
                        }
                    }

                    GoalType.COMPLETE_X_REPS_OF_EXERCISE -> {
                        val analysis = exerciseAnalysis.find { it.exerciseName.equals(goal.exerciseName, ignoreCase = true) }
                        if (analysis != null) {
                            val curr = analysis.repCount
                            if (curr >= goal.targetValue) {
                                updatedGoal.isComplete = true
                            } else {
                                updatedGoal.currentValue = curr.toInt()
                            }
                        }
                    }

                    else -> Unit
                }

                // Update the goal in Firestore
                goalRef.document(updatedGoal.docID).set(updatedGoal)
            }

            onComplete()
        }
    } //todo fix analysis of goals tied to exercises
}

