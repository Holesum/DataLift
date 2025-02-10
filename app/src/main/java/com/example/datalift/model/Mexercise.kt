package com.example.datalift.model

data class Mexercise(
    var id: String = "",
    var name:String = "",
    var exercise: ExerciseItem? = null,
    var sets: List<Mset> = emptyList(),
    var avgORM: Double = 0.0,
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
    fun setAvgORM() {
        var foo = 0.0
        for (set in this.sets) {
             foo += set.orm
        }
        this.avgORM = foo / this.sets.size
    }
}
