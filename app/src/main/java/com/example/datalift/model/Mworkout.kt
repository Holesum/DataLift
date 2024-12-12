package com.example.datalift.model

data class Mworkout(
    private val name: String,
    private val muscleGroup: String,
    private val exercises: List<Mexercise>
)
