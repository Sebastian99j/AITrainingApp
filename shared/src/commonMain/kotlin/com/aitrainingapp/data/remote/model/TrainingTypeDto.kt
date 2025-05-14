package com.aitrainingapp.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TrainingTypeDto(
    val id: Int,
    val name: String
)
