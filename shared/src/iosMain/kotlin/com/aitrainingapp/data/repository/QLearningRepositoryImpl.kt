package com.aitrainingapp.data.repository

import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.data.remote.model.QLearningFeedbackDto
import com.aitrainingapp.domain.repository.QLearningRepository

class QLearningRepositoryImpl(
    private val api: ApiConnection
) : QLearningRepository {

    override suspend fun sendFeedback(feedback: QLearningFeedbackDto): Boolean {
        return api.sendQLearningFeedback(feedback)
    }
}
