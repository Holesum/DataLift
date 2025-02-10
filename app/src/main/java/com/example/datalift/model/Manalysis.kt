package com.example.datalift.model

import com.google.firebase.Timestamp

data class Manalysis(
    var date: Timestamp = Timestamp.now(),
    var exerciseCount: Int = 0,
    var totalProgression: Double = 0.0
) {

}
