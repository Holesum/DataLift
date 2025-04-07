package com.example.datalift.data.di

import com.example.datalift.data.repository.PostRepository
import com.example.datalift.model.PostRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PostRepositoryModule {
    @Binds
    abstract fun bindPostRepository(
        postRepo: PostRepo
    ) : PostRepository
}