package com.aitrainingapp.android.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.AudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.android.services.NotificationReceiver
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

    private val _notificationOn = MutableStateFlow(false)
    val notificationOn: StateFlow<Boolean> = _notificationOn

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

    fun toggleNotification(context: Context, enabled: Boolean) {
        viewModelScope.launch {
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

                if (enabled) {
                    scheduleDailyNotification(context)
                } else {
                    cancelNotification(context)
                }
            }
        }
    }

    private fun scheduleDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
