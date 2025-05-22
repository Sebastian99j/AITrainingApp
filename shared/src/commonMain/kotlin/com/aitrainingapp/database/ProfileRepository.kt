package com.aitrainingapp.database

class ProfileRepository(private val queries: ProfileQueries) {

    fun insertDefaultProfilesIfNeeded() {
        val existing = queries.getAllProfiles().executeAsList()
        if (existing.isEmpty()) {
            queries.insertProfile("Początkujący", 2.5, 1, 1)
            queries.insertProfile("Średniozaawansowany", 1.5, 1, 1)
            queries.insertProfile("Zaawansowany", 0.5, 1, 1)
        }
    }
}
