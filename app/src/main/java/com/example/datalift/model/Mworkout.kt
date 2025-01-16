package com.example.datalift.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.util.Date

data class Mworkout(
    var name: String = "",
    val date: Timestamp = Timestamp.now(),
    var muscleGroup: String = "",
    var docID: String = "",
    var exercises: List<Mexercise> = emptyList()
) {

}
