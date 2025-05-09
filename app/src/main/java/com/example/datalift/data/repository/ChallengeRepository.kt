package com.example.datalift.data.repository

import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mworkout
import kotlinx.coroutines.flow.StateFlow

interface ChallengeRepository {
    fun createChallenge(uid: String, challenge: Mchallenge, callback: (Mchallenge?) -> Unit)
    fun getChallengesForUser(uid: String, callback: (List<Mchallenge>) -> Unit)
    fun getChallengesForCurrentUser(): StateFlow<List<Mchallenge>>
    fun getChallenge(challengeId: String) : StateFlow<Mchallenge>
    fun deleteChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit)
    fun updateChallenge(uid: String, challengeId: String, challenge: Mchallenge, callback: (Boolean) -> Unit)
    fun addUserToChallenge(uid: String, challengeId: String, callback: (Boolean) -> Unit)
    fun getChallenge(challengeId: String, callback: (Mchallenge?) -> Unit)
    fun evaluateChallenges(uid: String, workouts: List<Mworkout> = emptyList(), onComplete: () -> Unit)
}