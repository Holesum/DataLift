package com.example.datalift.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDate

data class Muser(
    val uid: String = "",
    val uname: String = "",
    var email: String = "",
    var gender: String = "",
    var name: String = "",
    val height: Double = 0.0,
    var weight: Double = 0.0,
    var privacy: Boolean = true,
    var imperial: Boolean = true,
    var dob: Timestamp = Timestamp.now(),
    var weights: List<userWeights> = emptyList(),
    var followers: List<String> = emptyList(),
    var following: List<String> = emptyList()
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): Muser {
            return Muser(
                uid = document.getString("uid") ?: "",
                uname = document.getString("uname") ?: "",
                email = document.getString("email") ?: "",
                gender = document.getString("gender") ?: "",
                name = document.getString("name") ?: "",
                height = document.getDouble("height") ?: 0.0,
                weight = document.getDouble("weight") ?: 0.0,
                privacy = document.getBoolean("privacy") ?: false,
                imperial = document.getBoolean("imperial") ?: false,
                dob = document.getTimestamp("dob") ?: Timestamp.now(), //fix the date to work better
                weights = document.get("weights") as List<userWeights>,
                followers = document.get("followers") as List<String>,
                following = document.get("following") as List<String>
            )
        }
    }
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "uid" to this.uid,
            "uname" to this.uname,
            "email" to this.email,
            "name" to this.name,
            "gender" to this.gender,
            "height" to this.height,
            "weight" to this.weight,
            "privacy" to this.privacy,
            "imperial" to this.imperial,
            "dob" to this.dob,
            "weights" to this.weights,
            "followers" to this.followers,
            "following" to this.following
        )
    }
}
