package com.example.datalift.model

data class Mexercise(
    var id: String = "",
    var name:String = "",
    var exercise: ExerciseItem? = null,
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
