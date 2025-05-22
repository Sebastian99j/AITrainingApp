package com.aitrainingapp.database

import com.aitrainingapp.domain.model.User

class UserRepository(private val queries: UserQueries) {
    fun insertUser(user: User) {
        queries.insertUser(
            username = user.username,
            aiIdentifier = user.aiIdentifier ?: "",
            profileId = user.profileId?.toLong(),
            active = if (user.active) 1 else 0,
            notificationOn = if (user.notificationOn) 1 else 0
        )
    }

    fun getFirstUser(): User? {
        return queries.getFirstUser().executeAsOneOrNull()?.let {
            User(
                id = it.id.toInt(),
                username = it.username,
                aiIdentifier = it.aiIdentifier,
                profileId = it.profileId?.toInt(),
                active = it.active.toInt() == 1,
                notificationOn = it.notificationOn.toInt() == 1
            )
        }
    }
}
