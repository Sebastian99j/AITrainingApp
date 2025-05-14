package com.aitrainingapp.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val aiIdentifier: String
)
