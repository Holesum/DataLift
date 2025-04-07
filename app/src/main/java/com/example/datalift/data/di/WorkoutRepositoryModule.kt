package com.example.datalift.data.di

import com.example.datalift.data.repository.WorkoutRepository
import com.example.datalift.model.WorkoutRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkoutRepositoryModule {
    @Binds
    abstract fun bindWorkoutRepository(
        workoutRepo: WorkoutRepo
    ) : WorkoutRepository
}