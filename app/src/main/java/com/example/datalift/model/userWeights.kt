package com.example.datalift.model

import java.time.Instant
import com.google.firebase.Timestamp

data class userWeights(
    val date: Timestamp = Timestamp.now(),
    val weight: Double = 0.0
    ){

}
