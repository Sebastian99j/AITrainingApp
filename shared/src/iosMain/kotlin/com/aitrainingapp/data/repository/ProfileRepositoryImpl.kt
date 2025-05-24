package com.aitrainingapp.data.repository

import com.aitrainingapp.database.ProfileQueries
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val queries: ProfileQueries) : ProfileRepository {

    override suspend fun getUserProfile(): Profile? {
        return queries.getAllProfiles().executeAsList()
            .firstOrNull()
            ?.let {
                val reps = it.repsChance
                val sets = it.setsChance

                if (reps != null && sets != null) {
                    Profile(
                        id = it.id.toInt(),
                        name = it.name,
                        weightStep = it.weightChance.toFloat(),
                        repsStep = reps.toInt(),
                        setsStep = sets.toInt()
                    )
                } else {
                    null
                }
            }
    }
}
