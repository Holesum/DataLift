package com.example.datalift.screens.feed

import androidx.lifecycle.ViewModel
import com.example.datalift.data.repository.PostRepository
import com.example.datalift.model.Mpost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepo: PostRepository
): ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()

//    private val postRepo = PostRepo()

    private val _posts = MutableStateFlow<List<Mpost>>(emptyList())
    val posts: StateFlow<List<Mpost>> get() = _posts

    private val _currentPost = MutableStateFlow<Mpost?>(null)
    val currentPost: StateFlow<Mpost?> get() = _currentPost

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

    fun updateCurrentViewedPost(postID: String, uid: String){
        postRepo.getPost(uid,postID) { post ->
            _currentPost.value = post
        }
    }

    fun getPosts() {
        postRepo.getPosts(uid){ posts ->
            _posts.value = posts
        }
    }

}