package com.aitrainingapp.database

import com.aitrainingapp.domain.model.User

class DatabaseHelper(factory: DatabaseDriverFactory) {
    private val database = AppDatabase(factory.createDriver())

    val userQueries = database.userQueries
    val profileQueries = database.profileQueries

    val profileRepository = ProfileRepository(profileQueries)
    val userRepository = UserRepository(userQueries)

    fun seedDefaultData() {
        profileRepository.insertDefaultProfilesIfNeeded()
        if (userRepository.getFirstUser() == null) {
            userRepository.insertUser(
                User(
                    id = 1,
                    username = "admin",
                    aiIdentifier = "admin",
                    profileId = profileRepository.getAllProfiles().firstOrNull()?.id?.toInt() ?: 1,
                    active = true,
                    notificationOn = false
                )
            )
        }
    }
}
