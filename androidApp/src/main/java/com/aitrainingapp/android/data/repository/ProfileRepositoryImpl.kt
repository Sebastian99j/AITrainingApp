package com.aitrainingapp.android.data.repository

import com.aitrainingapp.android.room.dao.ProfileDao
import com.aitrainingapp.android.room.toDomain
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val dao: ProfileDao) : ProfileRepository {
    override suspend fun getUserProfile(): Profile? {
        return dao.getAll().firstOrNull()?.toDomain()
    }
}
