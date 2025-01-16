package com.example.datalift.model

import com.google.firebase.firestore.DocumentSnapshot

data class Mexercise(
    var id: String = "",
    var name:String = "",
    var sets: List<Mset> = emptyList()
) {
}
