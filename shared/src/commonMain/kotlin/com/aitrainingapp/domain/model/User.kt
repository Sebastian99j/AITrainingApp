package com.aitrainingapp.domain.model

import com.aitrainingapp.database.Users

data class User(
    val id: Int,
    val username: String,
    val aiIdentifier: String?,
    val profileId: Int?,
    val active: Boolean,
    val notificationOn: Boolean
)

fun Users.toDomain(): User {
    return User(
        id = id.toInt(),
        username = username,
        aiIdentifier = aiIdentifier,
        profileId = profileId?.toInt(),
        active = active == 1L,
        notificationOn = notificationOn == 1L
    )
}