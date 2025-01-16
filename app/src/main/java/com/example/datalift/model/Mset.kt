package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Mset(
    var rep: Long = 0,
    var weight: Double = 0.0
) {
}
