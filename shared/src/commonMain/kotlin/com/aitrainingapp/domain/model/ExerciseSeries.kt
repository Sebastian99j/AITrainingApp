package com.aitrainingapp.domain.model

data class ExerciseSeries(
    val weight: Float,
    val reps: Int,
    val sets: Int,
    val rpe: Int,
    val durationSeconds: Int,
    val date: String,
    val userId: Int? = null,
    val exercise: String? = null
)
