package com.aitrainingapp.android.viewmodel

import android.content.Context
import android.media.AudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.database.UserQueries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSettingsViewModel(
    private val userQueries: UserQueries
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _darkMode = MutableStateFlow(true)
    val darkMode: StateFlow<Boolean> = _darkMode

    fun toggleDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
    }

    init {
        loadUsername()
    }

    private fun loadUsername() {
        viewModelScope.launch {
            val user = userQueries.getFirstUser().executeAsOneOrNull()
            user?.let {
                _username.value = it.username
            }
        }
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch {
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

    fun setVolume(context: Context, volume: Float) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newVolume = (volume * maxVolume).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
    }
}
