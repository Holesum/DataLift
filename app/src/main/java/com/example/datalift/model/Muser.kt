package com.example.datalift.model

data class Muser(
    private val uid: Number,
    private val uemail: String,
    private val upass: String,
    private val udata: String, //needs to become it's own object
    private val upref: List<Mupref>,
    private val ufriend: List<Muser>
)
