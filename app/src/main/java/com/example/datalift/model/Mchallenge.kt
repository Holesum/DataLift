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
    //updating this so we can have more complex challenges
    var goal: List<Mgoal> = listOf(),
    var participants: List<String> = listOf(),
    var progress: Map<String, ChallengeProgress> = mapOf()
)

data class ChallengeProgress(
    var currentValue: Int = 0,
    var isComplete: Boolean = false
)

/*
package com.example.datalift.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class Mchallenge(
    val cname: String = "",
    val details: String = "",
    val members: List<DocumentReference> = emptyList(),
    val owner: DocumentReference = FirebaseFirestore.getInstance().collection("Users").document("temp"),
    val date: Timestamp = Timestamp.now(),//fix this to be a date object
    val requests: List<DocumentReference> = emptyList()//may need to change this to a specific request object
)
*/

/*
package com.example.datalift.model

//import androidx.preference.contains
//import androidx.preference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import java.util.Date

enum class ChallengeStatus {
    CREATED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class MChallenge(
    val challengeId: String,
    val creatorId: String,
    val title: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val exerciseType: String,
    val targetValue: Int,
    val unit: String,
    val participants: MutableList<String> = mutableListOf(),
    val progress: MutableMap<String, Int> = mutableMapOf(),
    var status: ChallengeStatus = ChallengeStatus.CREATED
) {

    fun addParticipant(userId: String) {
        if (!participants.contains(userId)) {
            participants.add(userId)
            progress[userId] = 0 // Initialize progress to 0
        }
    }

    fun removeParticipant(userId: String) {
        if (participants.contains(userId)) {
            participants.remove(userId)
            progress.remove(userId)
        }
    }

    fun updateProgress(userId: String, newProgress: Int) {
        if (participants.contains(userId)) {
            progress[userId] = newProgress
        }
    }

    fun getParticipantProgress(userId: String): Int? {
        return progress[userId]
    }

    fun isChallengeComplete(): Boolean {
        // Check if the end date has passed and all participants have reached the target
        val now = Date()
        val isComplete = if (now.after(endDate)) {
            participants.all { progress[it] ?: 0 >= targetValue }
        } else {
            false
        }
        // Adjust the status based on the result of the check
        if (status == ChallengeStatus.IN_PROGRESS) {
            status = if (isComplete) ChallengeStatus.COMPLETED else ChallengeStatus.IN_PROGRESS
        }
        return isComplete
    }

    fun getChallengeStatus(): ChallengeStatus {
        return status
    }
}

data class Mchallenge(
    val cname: String = "",
    val description: String = "",
    val members: List<DocumentReference> = emptyList(),
    val owner: DocumentReference = FirebaseFirestore.getInstance().collection("Users").document("temp"),
    val creationDate: Timestamp = Timestamp.now(),//fix this to be a date object
    val requests: List<DocumentReference> = emptyList(), //may need to change this to a specific request object
    val isCompleted: Boolean = false,
    val completionDate: Timestamp = Timestamp.now()
)

data class Mchallenge(
    //val docID: String = "",
    val cname: String = "",
    val description: String = "",
    val members: List<DocumentReference> = emptyList(), // not an Muser list?
    //val completed: List<DocumentReference> = emptyList(), // not an Muser list?
    val owner:  Muser? = Muser(),
    val dateCreated: Timestamp = Timestamp.now(),
    //val dateEnding: Timestamp = Timestamp.now(), // not sure how to do this
    val requests: List<DocumentReference> = emptyList(), //may need to change this to a specific request object
    //val exerciseTarget: ExerciseItem? = ExerciseItem()
) {

}
 */
