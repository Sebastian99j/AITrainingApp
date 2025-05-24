package com.aitrainingapp.data.repository

import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.data.remote.model.TrainingSeriesDto
import com.aitrainingapp.domain.model.ExerciseSeries
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.UserLocalRepository

class ExerciseRepositoryImpl(
    private val api: ApiConnection,
    private val userRepo: UserLocalRepository
) : ExerciseRepository {

    override suspend fun saveSeries(series: ExerciseSeries): Boolean {
        val userId = userRepo.getUserId() ?: return false

        val dto = TrainingSeriesDto(
            id = 0,
            trainingType = series.exercise ?: "",
            userId = userId,
            weight = series.weight,
            reps = series.reps,
            sets = series.sets,
            rpe = series.rpe,
            dateTime = series.date,
            breaksInSeconds = series.durationSeconds,
            trained = false
        )

        return api.addTrainingSeries(dto)
    }

    override suspend fun getQLearningRecommendation(userId: Int): String {
        return api.runQLearningPredictionAndWait(userId)
    }
}
