package com.aitrainingapp.domain.repository

import com.aitrainingapp.model.RegressionPoint

interface ProgressionRepositoryIOS {
    suspend fun runAnalysis(userId: Int, type: String): Result<List<RegressionPoint>>
}