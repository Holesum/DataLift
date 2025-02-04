package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Mset(
    var rep: Long = 0,
    var weight: Double = 0.0,
    var orm: Double = 0.0
) {

    fun getFormattedSet(): String {
        return "$rep reps at ${weight}kg"
    }

    fun isValid(): Boolean {
        return rep > 0 && weight >= 0
    }

    fun setORM(){
        if(this.rep > 0){
            this.orm = this.weight / (1.0278 - (0.0278 * this.rep))
        }
    }
}
