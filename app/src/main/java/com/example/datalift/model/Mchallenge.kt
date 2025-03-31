package com.example.datalift.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class Mchallenge(
    val cname: String = "",
    val details: String = "",
    val members: List<DocumentReference> = emptyList(),
    val owner: DocumentReference = FirebaseFirestore.getInstance().collection("Users").document("temp"),
    val date: Timestamp = Timestamp.now(),//fix this to be a date object
    val requests: List<DocumentReference> = emptyList()//may need to change this to a specific request object
)
