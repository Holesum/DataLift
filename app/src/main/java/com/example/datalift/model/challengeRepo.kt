package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.ChallengeRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class challengeRepo @Inject constructor() : ChallengeRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createChallenge(uid: String, challenge: Mchallenge, callback: (Mchallenge?) -> Unit) {
        val challengeRef = db.collection("Users")
            .document(uid)
            .collection("Challenges")

        // Add the challenge to generate a document ID
        challengeRef.add(challenge)
            .addOnSuccessListener { docRef ->
                val challengeWithID = challenge.copy(challengeId = docRef.id)

                // Set the document again with the correct challengeId field
                challengeRef.document(docRef.id).set(challengeWithID)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Challenge created with ID: ${docRef.id}")
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
}
