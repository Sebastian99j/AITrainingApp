package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.android.room.ProfileEntity
import com.aitrainingapp.android.room.dao.ProfileDao
import com.aitrainingapp.android.room.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileDao: ProfileDao,
    private val userDao: UserDao
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
            _profiles.value = profileDao.getAll()
            userDao.getFirstUser()?.let {
                _username.value = it.username
                _aiIdentifier.value = it.aiIdentifier
                _selectedProfileId.value = it.profileId
            }
        }
    }

    fun setProfileForUser(profileId: Int) {
        viewModelScope.launch {
            userDao.getFirstUser()?.let { user ->
                val updated = user.copy(profileId = profileId)
                userDao.insertUser(updated)
                _selectedProfileId.value = profileId
            }
        }
    }
}
