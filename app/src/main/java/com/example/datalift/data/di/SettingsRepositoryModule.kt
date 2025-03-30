package com.example.datalift.data.di

import com.example.datalift.data.repository.SettingsRepo
import com.example.datalift.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryModule {
    @Binds
    abstract fun bindSettingsRepository(
        settingsRepo: SettingsRepo
    ): SettingsRepository
}