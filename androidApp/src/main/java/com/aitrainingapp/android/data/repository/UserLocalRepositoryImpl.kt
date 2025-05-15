package com.aitrainingapp.android.data.repository

import com.aitrainingapp.android.room.dao.ProfileDao
import com.aitrainingapp.android.room.dao.UserDao
import com.aitrainingapp.android.room.toDomain
import com.aitrainingapp.android.room.toEntity
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.repository.UserLocalRepository

class UserLocalRepositoryImpl(
    private val userDao: UserDao,
    private val profileDao: ProfileDao
) : UserLocalRepository {

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun getUserById(): User? {
        return userDao.getFirstUser()?.toDomain()
    }

    override suspend fun getUserId(): Int? {
        return userDao.getFirstUser()?.toDomain()?.id
    }

    override suspend fun getUserProfile(): Profile? {
        val user = userDao.getFirstUser()?.toDomain() ?: return null
        val profileId = user.profileId ?: return null
        return profileDao.getProfileById(profileId)?.toDomain()
    }
}
