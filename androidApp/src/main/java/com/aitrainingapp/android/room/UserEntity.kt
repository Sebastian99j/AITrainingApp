package com.aitrainingapp.android.room

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
