package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot

data class ExerciseItem(
    val title: String,
    val type: String,
    val description: String,
    val bodyPart: String,
    val equipment: String,
    val level: String,
) {
    companion object{
        fun fromDocument(document: DocumentSnapshot): ExerciseItem {
            return ExerciseItem(
                title = document.getString("Title") ?: "",
                type = document.getString("Type") ?: "",
                description = document.getString("Desc") ?: "",
                bodyPart = document.getString("BodyPart") ?: "",
                equipment = document.getString("Equipment") ?: "",
                level = document.getString("Level") ?: "",
            )
    }
        }
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "Title" to this.title,
            "Type" to this.type,
            "Desc" to this.description,
            "BodyPart" to this.bodyPart,
            "Equipment" to this.equipment,
        )
    }

}
