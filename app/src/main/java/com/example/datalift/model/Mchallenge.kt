package com.example.datalift.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class Mchallenge(
    var challengeId: String = "",
    var creatorUid: String = "",
    var title: String = "",
    var description: String = "",
    var startDate: Timestamp = Timestamp.now(),
    var endDate: Timestamp = Timestamp.now(),
    var goal: Mgoal = Mgoal(),
    var participants: List<String> = listOf(),
    var progress: Map<String, ChallengeProgress> = mapOf()
)

data class ChallengeProgress(
    var currentValue: Int = 0,
    var isComplete: Boolean = false
)