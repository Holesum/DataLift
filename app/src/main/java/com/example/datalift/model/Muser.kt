package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot

data class Muser(
    private val uid: String,
    private val uname: String,
    private val email: String,
    private val name: String,
    private val height: Number,
    private val weight: Number,
    private val privacy: Boolean,
    private val imperial: Boolean,
    private val friends: List<String>
) {

    companion object {
        fun fromDocument(document: DocumentSnapshot): Muser {
            return Muser(
                uid = document.id,
                uname = document.getString("uname") ?: "",
                email = document.getString("email") ?: "",
                name = document.getString("name") ?: "",
                height = document.getString("height")?.toInt() ?: 0,
                weight = document.getString("weight")?.toInt() ?: 0,
                privacy = document.getBoolean("privacy") ?: false,
                imperial = document.getBoolean("imperial") ?: false,
                friends = document.get("friends") as? List<String> ?: emptyList()//fix warning

            )
        }
    }
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "uid" to this.uid,
            "uname" to this.uname,
            "email" to this.email,
            "name" to this.name,
            "height" to this.height,
            "weight" to this.weight,
            "privacy" to this.privacy,
            "imperial" to this.imperial,
            "friends" to this.friends
        )
    }
}
