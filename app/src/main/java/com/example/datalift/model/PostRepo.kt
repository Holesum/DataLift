package com.example.datalift.model

import android.util.Log
import com.example.datalift.data.repository.PostRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class PostRepo @Inject constructor(
    private val userRepo: userRepo
): PostRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val posts : MutableList<Mpost> = emptyList<Mpost>().toMutableList()

    override fun getPost(uid: String, id: String, callback: (Mpost?) -> Unit) {
//            db.collection("users").document(uid).collection("posts").document(docID).get()
        db.collection("Users")
            .document(uid)
            .collection("Posts")
            .document(id)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("Firebase", "Post found: ${snapshot.data}")
                callback(snapshot.toObject<Mpost>())
            }
            .addOnFailureListener{
                Log.d("Firebase", "Error getting post: ${it.message}")
                callback(null)
            }
        }

    override fun getPosts(uid: String, callback: (List<Mpost>) -> Unit) {
        userRepo.getFollowing(uid) {

            val followingList = it
            val posts = mutableListOf<Mpost>()
            for (user in followingList) {
                db.collection("Users")
                    .document(user)
                    .collection("Posts")
                    .get()
                    .addOnSuccessListener { snapShot ->
                        for (document in snapShot.documents) {
                            val post = document.toObject(Mpost::class.java)
                            if (post != null) {
                                posts.add(post)
                            }
                        }
                        posts.sortByDescending{ it.date }
                        callback(posts)
                    }.addOnFailureListener { e ->
                        Log.w("Firebase", "Error getting posts for user $user", e)
                    }
            }

            //callback(posts)
        }
    }

    override fun addPost(uid: String, post: Mpost) {
        db.collection("Users")
            .document(uid)
            .collection("Posts")
            .add(post)
            .addOnSuccessListener { docRef ->
                Log.d("Firebase", "Post added: ${docRef.id}")
                val documentID = docRef.id
                val updatedWorkout = post.copy(docID = documentID)
                db.collection("Users")
                    .document(uid)
                    .collection("Posts")
                    .document(documentID)
                    .set(updatedWorkout)
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error adding post", e)
            }
    }

    override fun addLike(uid: String, post: Mpost) {
        db.collection("Users")
            .document(post.poster!!.uid)
            .collection("Posts")
            .document(post.docID)
            .update("likes", FieldValue.arrayUnion(uid))
    }

    override fun removeLike(uid: String, post: Mpost) {
        db.collection("Users")
            .document(post.poster!!.uid)
            .collection("Posts")
            .document(post.docID)
            .update("likes", FieldValue.arrayRemove(uid))
    }

}

