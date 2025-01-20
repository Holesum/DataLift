package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Mset(
    var rep: Long = 0,
    var weight: Double = 0.0
) {

    fun getFormattedSet(): String {
        return "$rep reps at ${weight}kg"
    }

    fun isValid(): Boolean {
        return rep > 0 && weight >= 0
    }
}
