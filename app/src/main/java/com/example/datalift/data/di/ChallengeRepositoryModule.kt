package com.example.datalift.data.di

import com.example.datalift.data.repository.ChallengeRepository
import com.example.datalift.model.challengeRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ChallengeRepositoryModule {
    @Binds
    abstract fun bindChallengeRepository(
        challengeRepo: challengeRepo
    ) : ChallengeRepository


}