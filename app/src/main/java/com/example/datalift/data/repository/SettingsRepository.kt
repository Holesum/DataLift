package com.example.datalift.data.repository

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun getUnitType(): StateFlow<String>
    fun getPrivacyType(): StateFlow<String>
    suspend fun savePrivacySetting(privacy: String)
    suspend fun saveUnitType(unitType: String)
}