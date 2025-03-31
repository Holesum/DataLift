package com.example.datalift.data.di

import com.example.datalift.model.userRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule{
    @Provides
    @Singleton
    fun provideUserRepo(

    ): userRepo {
        return userRepo(

        )
    }
}