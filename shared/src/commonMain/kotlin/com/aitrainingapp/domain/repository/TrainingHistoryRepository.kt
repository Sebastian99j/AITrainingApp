package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.TrainingSeries

interface TrainingHistoryRepository {
    suspend fun getTrainingHistory(userId: Int): List<TrainingSeries>
}
