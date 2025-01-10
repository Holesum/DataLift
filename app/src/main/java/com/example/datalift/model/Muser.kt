package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot

data class Muser(
    private val uid: String,
    private val uname: String,
    private var email: String,
    private var name: String,
    private val height: Number,
    private var weight: Number,
    private var privacy: Boolean,
    private var imperial: Boolean,
    private var friends: List<String>
) {

    companion object {
        fun fromDocument(document: DocumentSnapshot): Muser {
            return Muser(
                uid = document.getString("uid") ?: "",
                uname = document.getString("uname") ?: "",
                email = document.getString("email") ?: "",
                name = document.getString("name") ?: "",
                height = document.get("height") as? Number ?: 0,
                weight = document.get("weight") as? Number ?: 0,
                privacy = document.getBoolean("privacy") ?: false,
                imperial = document.getBoolean("imperial") ?: false,
                friends = document.get("friends") as List<String>,
            )
        }
    }
    fun getUid(): String {
        return this.uid
    }
    fun getUname(): String {
        return this.uname
    }
    fun getEmail(): String {
        return this.email
    }
    fun getName(): String {
        return this.name
    }
    fun getHeight(): Number {
        return this.height
    }
    fun getWeight(): Number {
        return this.weight
    }
    fun getPrivacy(): Boolean {
        return this.privacy
    }
    fun getImperial(): Boolean {
        return this.imperial
    }
    fun getFriends(): List<String> {
        return this.friends
    }
    fun setPrivacy(privacy: Boolean) {
        this.privacy = privacy
    }
    fun setImperial(imperial: Boolean) {
        this.imperial = imperial
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
