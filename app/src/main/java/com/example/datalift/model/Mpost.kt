package com.example.datalift.model

import android.graphics.Picture
import com.google.firebase.Timestamp

data class Mpost(
    val docID: String = "",
    val date: Timestamp = Timestamp.now(),
    var workout: Mworkout? = null,
    //var goal: Mugoal? = null,
    var poster: Muser? = null,
    var title: String = "",
    var body: String = "",
    //var pictures: List<Picture> = emptyList(),
    //Picture is a filler cuz idk the datatype
    //Be sure to update Muser.kt with the same datatype
    var likes: List<String> = emptyList(),
    //var draft: Boolean = false
){
    fun getLikesCount(): Int {
        return likes.size
    }
}
