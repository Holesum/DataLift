package com.example.datalift.data.di

import com.example.datalift.data.repository.AnalysisRepository
import com.example.datalift.model.analysisRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalysisRepositoryModule {
    @Binds
    abstract fun bindAnalysisRepository(
        analysisRepo: analysisRepo
    ) : AnalysisRepository
}