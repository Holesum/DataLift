package com.example.datalift.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class userRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var unitPreference: Boolean = true

    fun getCachedUnitType(): Boolean {
        return unitPreference ?: true // default fallback
    }

    fun getUser(uid: String, callback: (Muser?) -> Unit) {
        db.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(Muser::class.java)
                Log.d("Firebase", "User found: ${user?.name}")
                callback(user)
            }.addOnFailureListener {
                Log.d("Firebase", "Error getting user: ${it.message}")
                callback(null)
            }
    }

    fun getUnitType(uid: String, callback: (String) -> Unit) {
        db.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = Muser.fromDocument(snapshot)
                if(user.imperial){
                    unitPreference = true
                    callback("Imperial")
                } else {
                    unitPreference = false
                    callback("Metric")
                }
            }.addOnFailureListener {
                Log.d("Firebase", "Error updating privacy: ${it.message}")
                unitPreference = true
                callback("Imperial")
            }
    }

    fun getPrivacy(uid: String, callback: (String) -> Unit) {
        db.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = Muser.fromDocument(snapshot)
                if(user.privacy){
                    callback("Private")
                } else {
                    callback("Public")
                }
            }.addOnFailureListener {
                Log.d("Firebase", "Error updating privacy: ${it.message}")
                callback("Private")
            }
    }

    fun changePrivacy(uid: String, privacy: Boolean) {
        db.collection("Users")
            .document(uid)
            .update("privacy", privacy)
            .addOnSuccessListener {
                Log.d("Firebase", "Privacy updated")
            }.addOnFailureListener {
                Log.d("Firebase", "Error updating privacy: ${it.message}")
            }
    }

    fun changeImperial(uid: String, imperial: Boolean) {
        db.collection("Users")
            .document(uid)
            .update("imperial", imperial)
            .addOnSuccessListener {
                Log.d("Firebase", "Imperial updated")
            }.addOnFailureListener {
                Log.d("Firebase", "Error updating imperial: ${it.message}")
            }
    }

    fun changeName(uid: String, name: String) {
        db.collection("Users")
            .document(uid)
            .update("name", name)
            .addOnSuccessListener {
                Log.d("Firebase", "Name updated")
            }.addOnFailureListener {
                Log.d("Firebase", "Error updating name: ${it.message}")
            }
    }

    fun getUserByUsername(username: String, callback: (Muser?) -> Unit) {
        db.collection("Users")
            .whereEqualTo("uname", username)
            .get()
            .addOnSuccessListener {
                val user = it.documents.firstOrNull()?.toObject(Muser::class.java)
                Log.d("Firebase", "User found: ${user?.name}")
                callback(user)
            }.addOnFailureListener {
                Log.d("Firebase", "Error getting user: ${it.message}")
                callback(null)
            }
    }

    fun addFollower(user: Muser, friend: Muser) {
        db.collection("Users")
            .document(friend.uid)
            .collection("Followers")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firebase", "Follower added")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error adding follower: ${it.message}")

            }

        db.collection("Users")
            .document(friend.uid)
            .update("followers", FieldValue.arrayUnion(user.uid))
            .addOnSuccessListener {
                Log.d("Firebase", "Follower field added")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error adding field follower: ${it.message}")

            }


        db.collection("Users")
            .document(user.uid)
            .collection("Following")
            .document(friend.uid)
            .set(friend)
            .addOnSuccessListener {
                Log.d("Firebase", "Following added")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error adding following: ${it.message}")
            }

        db.collection("Users")
            .document(user.uid)
            .update("following", FieldValue.arrayUnion(friend.uid))
            .addOnSuccessListener {
                Log.d("Firebase", "Following field added")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error adding field following: ${it.message}")
            }
    }

    fun removeFollower(user: Muser, friend: Muser) {
        db.collection("Users")
            .document(friend.uid)
            .collection("Followers")
            .document(user.uid)
            .delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Follower from altUser removed")
            }.addOnFailureListener {
                Log.d("Firebase", "Error removing follower from altUser: ${it.message}")
            }

        db.collection("Users")
            .document(friend.uid)
            .update("followers", FieldValue.arrayRemove(user.uid))
            .addOnSuccessListener {
                Log.d("Firebase", "Follower field removed")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error removing field follower: ${it.message}")

            }

        db.collection("Users")
            .document(user.uid)
            .collection("Following")
            .document(friend.uid)
            .delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Following removed")
            }.addOnFailureListener {
                Log.d("Firebase", "Error removing following: ${it.message}")
            }

        db.collection("Users")
            .document(user.uid)
            .update("following", FieldValue.arrayRemove(friend.uid))
            .addOnSuccessListener {
                Log.d("Firebase", "Following field removed")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error removing field following: ${it.message}")
            }

    }

    fun getFollowers(uid: String, callback: (List<String>) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Followers")
            .get()
            .addOnSuccessListener {
                val followersList = it.documents.mapNotNull { document ->
                    document.getString("uid")
                }
                Log.d("Firebase", "Followers list: $followersList")
                callback(followersList)
            }.addOnFailureListener {
                Log.d("Firebase", "Error getting followers: ${it.message}")
            }

    }

    fun getFollowing(uid: String, callback: (List<String>) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Following")
            .get()
            .addOnSuccessListener {
                val followingList = it.documents.mapNotNull { document ->
                    document.getString("uid")
                }
                Log.d("Firebase", "Following list: $followingList")
                callback(followingList)
            }.addOnFailureListener {
                Log.d("Firebase", "Error getting following: ${it.message}")
            }
    }

    fun getUsers(query: String = "", callback: (List<Muser>) -> Unit) {
        if (query.isBlank()) {
            // If the query is empty or just whitespace, don't make a Firebase call
            callback(emptyList())
            return
        }

        val userList = mutableListOf<Muser>()

        db.collection("Users")
            .whereGreaterThanOrEqualTo("uname", query)
            .whereLessThanOrEqualTo("uname", query + "\uf8ff")
            .limit(10)
            .addSnapshotListener { snapShot, exception ->
                if (exception != null) {
                    // Handle the error (e.g., log or show a message to the user)
                    Log.e("FirestoreError", "Error fetching users", exception)
                    callback(emptyList()) // Return an empty list if there's an error
                    return@addSnapshotListener
                }
                snapShot?.documents?.forEach { document ->
                    val user = document.toObject<Muser>()
                    user?.let {
                        userList.add(user)
                    }
                }

                // Return the fetched exercises to the callback
                callback(userList)
            }
    }

    fun userLiveSearch(query: String = "", callback: (List<Muser>) -> Unit){
        if (query.isBlank()) {
            // If the query is empty or just whitespace, don't make a Firebase call
            callback(emptyList())
            return
        }

        val userList = mutableListOf<Muser>()

        db.collection("Users")
            .whereGreaterThanOrEqualTo("uname", query)
            .whereLessThanOrEqualTo("uname", query + "\uf8ff")
            .limit(10)
            .addSnapshotListener { snapShot, exception ->
                if (exception != null) {
                    // Handle the error (e.g., log or show a message to the user)
                    Log.e("FirestoreError", "Error fetching exercises", exception)
                    callback(emptyList()) // Return an empty list if there's an error
                    return@addSnapshotListener
                }
                snapShot?.documents?.forEach { document ->
                    val user = Muser.fromDocument(document)
                    userList.add(user)
                }

                // Return the fetched exercises to the callback
                callback(userList)
            }
    }

    fun logUserWeight(uid: String, weight: Double){
        val userWeight = userWeights(weight = weight, date = Timestamp.now())
        db.collection("Users")
            .document(uid)
            .update("weights", FieldValue.arrayUnion(userWeight))
            .addOnSuccessListener {
                Log.d("Firebase", "Weight logged")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error logging weight: ${it.message}")
            }

        db.collection("Users")
            .document(uid)
            .update("weight", weight)
            .addOnSuccessListener {
                Log.d("Firebase", "Weight updated")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error updating weight: ${it.message}")
            }

    }

}