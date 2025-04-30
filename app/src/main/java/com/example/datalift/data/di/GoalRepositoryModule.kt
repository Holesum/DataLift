package com.example.datalift.data.di

import com.example.datalift.data.repository.GoalRepository
import com.example.datalift.model.GoalRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GoalRepositoryModule {
    @Binds
    abstract fun bindGoalRepository(
        goalRepo: GoalRepo
    ) : GoalRepository


}
