package com.aitrainingapp.controller

import com.aitrainingapp.database.ProfileQueries
import com.aitrainingapp.database.UserQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiModel(
    val id: Int,
    val name: String,
    val weightChance: Float,
    val repsChance: Int,
    val setsChance: Int
)

class ProfileController(
    private val profileQueries: ProfileQueries,
    private val userQueries: UserQueries,
    private val scope: CoroutineScope
) {

    private val _profiles = MutableStateFlow<List<ProfileUiModel>>(emptyList())
    private val _username = MutableStateFlow("")
    private val _aiIdentifier = MutableStateFlow("")
    private val _selectedProfileId = MutableStateFlow<Int?>(null)

    // ✅ ZAMIANA val na fun — ważne dla iOS interop
    fun profiles(): StateFlow<List<ProfileUiModel>> = _profiles
    fun username(): StateFlow<String> = _username
    fun aiIdentifier(): StateFlow<String> = _aiIdentifier
    fun selectedProfileId(): StateFlow<Int?> = _selectedProfileId

    fun loadData() {
        scope.launch {
            _profiles.value = profileQueries.getAllProfiles().executeAsList().map {
                ProfileUiModel(
                    id = it.id.toInt(),
                    name = it.name,
                    weightChance = it.weightChance.toFloat(),
                    repsChance = it.repsChance?.toInt() ?: 0,
                    setsChance = it.setsChance?.toInt() ?: 0
                )
            }

            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                _username.value = it.username
                _aiIdentifier.value = it.aiIdentifier ?: ""
                _selectedProfileId.value = it.profileId?.toInt()
            }
        }
    }

    fun setProfileForUser(profileId: Int) {
        scope.launch {
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