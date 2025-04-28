package com.example.datalift.data.repository

import com.example.datalift.model.Mchallenge

interface ChallengeRepository {
    fun createChallenge(uid: String, challenge: Mchallenge, callback: (Mchallenge?) -> Unit)
    fun getChallengesForUser(uid: String, callback: (List<Mchallenge>) -> Unit)
}