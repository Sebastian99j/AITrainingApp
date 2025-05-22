package com.aitrainingapp.android.data.repository

import com.aitrainingapp.database.ProfileQueries
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val queries: ProfileQueries) : ProfileRepository {
    override suspend fun getUserProfile(): Profile? {
        return queries.getAllProfiles().executeAsList()
            .firstOrNull()
            ?.let {
                it.repsChance?.let { it1 ->
                    it.setsChance?.let { it2 ->
                        Profile(
                            id = it.id.toInt(),
                            name = it.name,
                            weightStep = it.weightChance.toFloat(),
                            repsStep = it1.toInt(),
                            setsStep = it2.toInt()
                        )
                    }
                }
            }
    }
}
