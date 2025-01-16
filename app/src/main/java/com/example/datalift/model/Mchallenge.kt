package com.example.datalift.model

data class Mchallenge(
    val cname: String,
    val details: String,
    val members: List<Muser>,
    val owner: Muser,
    val date: Number,//fix this to be a date object
    val requests: List<Muser>//may need to change this to a specific request object
)
