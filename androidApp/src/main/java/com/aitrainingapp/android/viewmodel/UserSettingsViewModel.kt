package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.android.room.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSettingsViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    init {
        loadUsername()
    }

    private fun loadUsername() {
        viewModelScope.launch {
            val user = userDao.getFirstUser()
            user?.let {
                _username.value = it.username
            }
        }
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch {
            val user = userDao.getFirstUser()
            if (user != null) {
                userDao.insertUser(user.copy(username = newName))
                _username.value = newName
            }
        }
    }
}
