package com.aitrainingapp.data.remote.model

import com.aitrainingapp.domain.model.User

data class UserEntity(
    val id: Int = 0,
    val username: String,
    val aiIdentifier: String,
    val profileId: Int? = null,
    val active: Boolean,
    val notificationOn: Boolean
)

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        aiIdentifier = this.aiIdentifier ?: "",
        profileId = this.profileId,
        active = this.active,
        notificationOn = this.notificationOn
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        aiIdentifier = this.aiIdentifier,
        profileId = this.profileId,
        active = this.active,
        notificationOn = this.notificationOn
    )
}
