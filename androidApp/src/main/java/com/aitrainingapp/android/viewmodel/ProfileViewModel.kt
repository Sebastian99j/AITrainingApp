package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.android.database.ProfileEntity
import com.aitrainingapp.database.ProfileQueries
import com.aitrainingapp.database.UserQueries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileQueries: ProfileQueries,
    private val userQueries: UserQueries
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<ProfileEntity>>(emptyList())
    val profiles: StateFlow<List<ProfileEntity>> = _profiles

    private val _username = MutableStateFlow<String>("")
    val username: StateFlow<String> = _username

    private val _aiIdentifier = MutableStateFlow<String>("")
    val aiIdentifier: StateFlow<String> = _aiIdentifier

    private val _selectedProfileId = MutableStateFlow<Int?>(null)
    val selectedProfileId: StateFlow<Int?> = _selectedProfileId

    fun loadData() {
        viewModelScope.launch {
            _profiles.value = profileQueries.getAllProfiles().executeAsList().map {
                ProfileEntity(
                    id = it.id.toInt(),
                    name = it.name,
                    weightChance = it.weightChance.toFloat(),
                    repsChance = it.repsChance?.toInt() ?: 0,
                    setsChance = it.setsChance?.toInt() ?: 0
                )
            }

            // Load user
            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                _username.value = it.username
                _aiIdentifier.value = it.aiIdentifier ?: ""
                _selectedProfileId.value = it.profileId?.toInt()
            }
        }
    }

    fun setProfileForUser(profileId: Int) {
        viewModelScope.launch {
            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                userQueries.insertUser(
                    username = it.username,
                    aiIdentifier = it.aiIdentifier,
                    profileId = profileId.toLong(),
                    active = it.active,
                    notificationOn = it.notificationOn
                )
                _selectedProfileId.value = profileId
            }
        }
    }
}
