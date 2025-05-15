package com.aitrainingapp.domain.model

data class TrainingSeries(
    val id: Int,
    val trainingType: String,
    val weight: Float,
    val reps: Int,
    val sets: Int,
    val rpe: Int,
    val dateTime: String,
    val breaksInSeconds: Int,
    val trained: Boolean
)
