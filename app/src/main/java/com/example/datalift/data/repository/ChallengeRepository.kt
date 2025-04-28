package com.example.datalift.data.repository

import com.example.datalift.model.Mchallenge

interface ChallengeRepository {
    fun createChallenge(uid: String, challenge: Mchallenge, callback: (Mchallenge?) -> Unit)
    fun getChallengesForUser(uid: String, callback: (List<Mchallenge>) -> Unit)
    fun deleteChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit)
    fun updateChallenge(uid: String, challengeId: String, challenge: Mchallenge, callback: (Boolean) -> Unit)
}