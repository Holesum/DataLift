package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot

data class Mexercise(
    var id: String = "",
    var name:String = "",
    var sets: List<Mset> = emptyList()
) {
    fun getFormattedName(): String {
        return "$name (${totalSets()} sets)"
    }

    fun totalSets(): Int {
        return sets.size
    }
    fun isValid(): Boolean {
        return name.isNotBlank() && totalSets() > 0
    }
}
