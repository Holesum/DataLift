package com.example.datalift.model

import com.google.firebase.Timestamp

data class Mgoal(
    var docID: String = "",
    var type: GoalType = GoalType.UNKNOWN,  // App uses this
    var targetValue: Int = 0,
    var targetPercentage: Double? = null,
    var bodyPart: String? = null,
    var exerciseName: String? = null,
    var currentValue: Int = 0,
    var isComplete: Boolean = false,
    var createdAt: Timestamp = Timestamp.now()
)

enum class GoalType {
    INCREASE_ORM_BY_VALUE,
    INCREASE_ORM_BY_PERCENTAGE,
    COMPLETE_X_WORKOUTS,
    COMPLETE_X_WORKOUTS_OF_BODY_PART,
    COMPLETE_X_REPS_OF_EXERCISE,
    UNKNOWN
}
