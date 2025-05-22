package com.aitrainingapp.database

class DatabaseHelper(factory: DatabaseDriverFactory) {
    private val database = AppDatabase(factory.createDriver())

    val userQueries = database.userQueries
    val profileQueries = database.profileQueries

    val profileRepository = ProfileRepository(profileQueries)

    fun seedDefaultData() {
        profileRepository.insertDefaultProfilesIfNeeded()
    }
}
