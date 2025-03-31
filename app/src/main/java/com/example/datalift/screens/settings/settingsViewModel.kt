package com.example.datalift.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datalift.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
//    private val userRepo: userRepo,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun updateUnit(newUnit: String){
        viewModelScope.launch{
            settingsRepository.saveUnitType(newUnit)
        }
    }

    fun updatePrivacy(newPrivacy: String){
        viewModelScope.launch{
            settingsRepository.savePrivacySetting(newPrivacy)
        }
    }

    val getCurrentChoiceUiState: (SettingsType, SettingUiState.Success) -> String = { settingType, uiState ->
        when(settingType){
            SettingsType.UNITS -> uiState.units
            SettingsType.PRIVACY -> uiState.privacy
//            else -> ""
        }
    }

    val updateFunction: (SettingsType) -> ((String) -> Unit) = { settingType ->
        when(settingType){
            SettingsType.UNITS -> this::updateUnit
            SettingsType.PRIVACY -> this::updatePrivacy
//            else -> {_ -> }
        }
    }

    val uiState: StateFlow<SettingUiState> = combine(
        settingsRepository.getUnitType(),
        settingsRepository.getPrivacyType(),
        SettingUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingUiState.Loading
    )

}

enum class SettingsType {
    UNITS,
    PRIVACY
}

sealed interface SettingUiState{
    data object Loading : SettingUiState

    data class Success(
        val units: String,
        val privacy: String,
    ) : SettingUiState
}