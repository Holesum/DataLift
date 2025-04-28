package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.ChallengeRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class challengeRepo @Inject constructor() : ChallengeRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createChallenge(uid: String, challenge: Mchallenge, callback: (Mchallenge?) -> Unit) {
        val challengeRef = db.collection("Challenges")

        // Add the challenge to generate a document ID
        challengeRef.add(challenge)
            .addOnSuccessListener { docRef ->
                val challengeWithID = challenge.copy(challengeId = docRef.id)

                // Set the document again with the correct challengeId field
                challengeRef.document(docRef.id).set(challengeWithID)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Challenge created with ID: ${docRef.id}")
                        addUserToChallenge(uid, docRef.id) {}
                        callback(challengeWithID)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Failed to update challenge with ID: ${e.message}")
                        callback(null)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error creating challenge: ${e.message}")
                callback(null)
            }
    }

    override fun getChallengesForUser(uid: String, callback: (List<Mchallenge>) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Challenges")
            .get()
            .addOnSuccessListener { snapshot ->
                val challenges = snapshot.documents.mapNotNull { it.toObject(Mchallenge::class.java) }
                callback(challenges)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to fetch challenges: ${e.message}")
                callback(emptyList())
            }
    }

    fun getChallenge(challengeId: String, callback: (Mchallenge?) -> Unit) {
        db.collection("Challenges")
            .document(challengeId)
            .get()

    }

    override fun deleteChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit) {
        getChallenge(challengeId){
            for(user in it?.participants ?: emptyList()){
                removeRef(uid, challengeId){}
            }
            db.collection("Challenges")
                .document(challengeId)
                .delete()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.e("ChallengeRepo", "Failed to delete challenge: ${e.message}")
                    callback(false)
                }
        }
    }

    private fun removeRef(uid: String, challengeId: String, callback: (Boolean) -> Unit){
        db.collection("Users").document(uid).collection("Challenges").document(challengeId).delete()
    }

    override fun updateChallenge(uid: String, challengeId: String, challenge: Mchallenge, callback: (Boolean) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Challenges")
            .document(challengeId)
            .set(challenge)
    }


    //Add user to challenge
    fun addUserToChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit) {
        db.collection("Challenges").document(challengeId).update("users", FieldValue.arrayUnion(uid))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to add user to challenge: ${e.message}")
                callback(false)
            }

        val challengeRef = db.collection("Challenges").document(challengeId)

        db.collection("Users").document(uid)
            .collection("Challenges")
            .document(challengeId)
            .set(mapOf("challengeRef" to challengeRef))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("ChallengeRepo", "Failed to add user to challenge: ${e.message}")
                callback(false)
            }
    }

    //Remove user from challenge

    //


}
