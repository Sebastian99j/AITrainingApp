package com.aitrainingapp.android.room

import com.aitrainingapp.domain.model.User
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
