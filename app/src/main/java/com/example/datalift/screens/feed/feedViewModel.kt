package com.example.datalift.screens.feed

import androidx.lifecycle.ViewModel
import com.example.datalift.model.Mpost
import com.example.datalift.model.postRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class feedViewModel: ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()

    private val postRepo = postRepo()

    private val _posts = MutableStateFlow<List<Mpost>?>(null)
    val posts: StateFlow<List<Mpost>?> get() = _posts

    init{
        postRepo.getPosts(uid){ posts ->
            _posts.value = posts
        }
    }

    fun addLike(post: Mpost){
        postRepo.addLike(uid, post)
    }

    fun removeLike(post: Mpost){
        postRepo.removeLike(uid, post)
    }



}