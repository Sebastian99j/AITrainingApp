package com.aitrainingapp.domain.repository

import com.aitrainingapp.data.remote.model.QLearningFeedbackDto

interface QLearningRepository {
    suspend fun sendFeedback(feedback: QLearningFeedbackDto): Boolean
}