package com.example.datalift.data.repository

import com.example.datalift.model.Mpost
import kotlinx.coroutines.flow.StateFlow

interface PostRepository {
    fun getPost(uid: String, id: String, callback: (Mpost?) -> Unit)
    fun getPosts(uid: String, callback: (List<Mpost>) -> Unit)
    fun addPost(uid: String, post: Mpost)
    fun addLike(uid: String, post: Mpost)
    fun removeLike(uid: String, post: Mpost)
}