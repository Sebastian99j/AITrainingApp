package com.aitrainingapp.data.remote.model

import com.aitrainingapp.domain.model.TrainingSeries
import kotlinx.serialization.Serializable

@Serializable
data class TrainingSeriesDto(
    val id: Int,
    val trainingType: String,
    val weight: Float,
    val reps: Int,
    val sets: Int,
    val rpe: Int,
    val dateTime: String,
    val breaksInSeconds: Int,
    val trained: Boolean,
    val userId: Int?
)

fun TrainingSeriesDto.toDomain(): TrainingSeries = TrainingSeries(
    id = id,
    trainingType = trainingType,
    weight = weight,
    reps = reps,
    sets = sets,
    rpe = rpe,
    dateTime = dateTime,
    breaksInSeconds = breaksInSeconds,
    trained = trained
)
