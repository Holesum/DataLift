package com.example.datalift.model

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class postRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userRepo = userRepo()

    private val posts : MutableList<Mpost> = emptyList<Mpost>().toMutableList()



        fun getPosts(uid: String, callback: (List<Mpost>) -> Unit) {
            userRepo.getFollowing(uid) {
                val followingList = it
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

                        }.addOnFailureListener { e ->
                            Log.w("Firebase", "Error getting posts for user $user", e)
                        }

                }
                callback(posts)

            }
        }

            fun addPost(uid: String, post: Mpost) {
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

            fun addLike(uid: String, post: Mpost) {
                db.collection("Users")
                    .document(post.poster!!.uid)
                    .collection("Posts")
                    .document(post.docID)
                    .update("likes", FieldValue.arrayUnion(uid))
            }

            fun removeLike(uid: String, post: Mpost) {
                db.collection("Users")
                    .document(post.poster!!.uid)
                    .collection("Posts")
                    .document(post.docID)
                    .update("likes", FieldValue.arrayRemove(uid))
            }

        }

