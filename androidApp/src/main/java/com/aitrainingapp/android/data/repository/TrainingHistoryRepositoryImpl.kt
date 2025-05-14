package com.aitrainingapp.android.data.repository

import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.data.remote.model.toDomain
import com.aitrainingapp.domain.model.TrainingSeries
import com.aitrainingapp.domain.repository.TrainingHistoryRepository

class TrainingHistoryRepositoryImpl(
    private val api: ApiConnection
) : TrainingHistoryRepository {
    override suspend fun getTrainingHistory(userId: Int): List<TrainingSeries> {
        return api.getAllTrainingSeriesByUser(userId).map { it.toDomain() }
    }
}
