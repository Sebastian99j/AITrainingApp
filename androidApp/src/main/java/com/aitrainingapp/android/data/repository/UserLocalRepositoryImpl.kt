package com.aitrainingapp.android.data.repository

import com.aitrainingapp.database.ProfileQueries
import com.aitrainingapp.database.UserQueries
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.model.toDomain
import com.aitrainingapp.domain.repository.UserLocalRepository

class UserLocalRepositoryImpl(
    private val userQueries: UserQueries,
    private val profileQueries: ProfileQueries
) : UserLocalRepository {

    override suspend fun insertUser(user: User) {
        userQueries.insertUser(
            username = user.username,
            aiIdentifier = user.aiIdentifier ?: "",
            profileId = user.profileId?.toLong(),
            active = if (user.active) 1 else 0,
            notificationOn = if (user.notificationOn) 1 else 0
        )
    }

    override suspend fun getUserById(): User? {
        return userQueries.getFirstUser().executeAsOneOrNull()?.toDomain()
    }

    override suspend fun getUserId(): Int? {
        return getUserById()?.id
    }

    override suspend fun getUserProfile(): Profile? {
        val user = getUserById() ?: return null
        val profileId = user.profileId ?: return null
        return profileQueries.getProfileById(profileId.toLong()).executeAsOneOrNull()?.toDomain()
    }
}
