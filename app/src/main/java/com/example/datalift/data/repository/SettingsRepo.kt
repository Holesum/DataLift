package com.example.datalift.data.repository

import com.example.datalift.model.userRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingsRepo @Inject constructor(
    private val userRepo: userRepo
) : SettingsRepository{
    private var auth: FirebaseAuth = Firebase.auth
    private val uid: String = auth.currentUser?.uid.toString()

    private val _unitType = MutableStateFlow("Metric")
    private val _privacyType = MutableStateFlow("Private")

    init{
        userRepo.getUnitType(uid){ unitType ->
            _unitType.value = unitType
        }

        userRepo.getPrivacy(uid){ privacyType ->
            _privacyType.value = privacyType
        }
    }

    override fun getUnitType(): StateFlow<String> = _unitType.asStateFlow()

    override fun getPrivacyType(): StateFlow<String> = _privacyType.asStateFlow()

    override suspend fun savePrivacySetting(privacy: String) {
        if(privacy.contentEquals("Private")){
            _privacyType.value = privacy
            userRepo.changePrivacy(uid,true)
        } else if (privacy.contentEquals("Public")){
            _privacyType.value = privacy
            userRepo.changePrivacy(uid,false)
        } else if(privacy.contentEquals("Followers Only")) {
            _privacyType.value = privacy
//            TODO()
        }
    }

    override suspend fun saveUnitType(unitType: String) {
        if(unitType.contentEquals("Imperial")){
            _unitType.value = unitType
            userRepo.changeImperial(uid,true)
            userRepo.getUnitType(uid){ unitType ->
                _unitType.value = unitType
            }
        } else if (unitType.contentEquals("Metric")){
            _unitType.value = unitType
            userRepo.changeImperial(uid,false)
            userRepo.getUnitType(uid){ unitType ->
                _unitType.value = unitType
            }
        }
    }
}