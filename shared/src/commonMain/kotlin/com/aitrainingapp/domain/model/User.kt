package com.aitrainingapp.domain.model

data class User(
    val id: Int,
    val username: String,
    val aiIdentifier: String,
    val profileId: Int?,
    val active: Boolean,
    val notificationOn: Boolean
)
