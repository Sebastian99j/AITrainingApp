package com.aitrainingapp.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class QLearningFeedbackDto(
    val user_id: Int,
    val type: String,
    val action: String,
    val successful: Boolean,
    val weight: Int,
    val reps: Int,
    val sets: Int,
    val RPE: Int,
    val training_goal: String
)
