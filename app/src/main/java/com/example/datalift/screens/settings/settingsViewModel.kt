package com.example.datalift.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(

) : ViewModel() {
    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    fun updateUnit(newUnit: String){
        _settingsUiState.update { currentState ->
            currentState.copy(
                units = newUnit
            )
        }
    }

    fun updatePrivacy(newPrivacy: String){
        _settingsUiState.update { currentState ->
            currentState.copy(
                privacy = newPrivacy
            )
        }
    }

}

data class SettingsUiState(
    val units: String = "Imperial",
    val privacy: String = "Private"
)
