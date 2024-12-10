package com.example.datalift.model

data class Mchallenge(
    private val cname: String,
    private val details: String,
    private val members: List<Muser>,
    private val owner: Muser,
    private val date: Number,//fix this to be a date object
    private val requests: List<Muser>//may need to change this to a specific request object
)
