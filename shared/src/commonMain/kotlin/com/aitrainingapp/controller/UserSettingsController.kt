package com.aitrainingapp.controller

import com.aitrainingapp.database.UserQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSettingsController(
    private val userQueries: UserQueries,
    private val scope: CoroutineScope
) {
    private val _username = MutableStateFlow("")
    private val _darkMode = MutableStateFlow(true)
    private val _notificationOn = MutableStateFlow(false)

    fun username(): StateFlow<String> = _username
    fun darkMode(): StateFlow<Boolean> = _darkMode
    fun notificationOn(): StateFlow<Boolean> = _notificationOn

    init {
        loadUserData()
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
    }

    private fun loadUserData() {
        scope.launch {
            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                _username.value = it.username
                _notificationOn.value = it.notificationOn.toInt() == 1
            }
        }
    }

    fun updateUsername(newName: String) {
        scope.launch {
            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                userQueries.insertUser(
                    username = newName,
                    aiIdentifier = it.aiIdentifier,
                    profileId = it.profileId,
                    active = it.active,
                    notificationOn = it.notificationOn
                )
                _username.value = newName
            }
        }
    }

    fun updateNotificationFlag(enabled: Boolean) {
        scope.launch {
            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                userQueries.insertUser(
                    username = it.username,
                    aiIdentifier = it.aiIdentifier,
                    profileId = it.profileId,
                    active = it.active,
                    notificationOn = if (enabled) 1 else 0
                )
                _notificationOn.value = enabled
            }
        }
    }
}
