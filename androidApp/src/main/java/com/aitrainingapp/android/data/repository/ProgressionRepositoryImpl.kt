package com.aitrainingapp.android.data.repository

import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.domain.repository.ProgressionRepository

class ProgressionRepositoryImpl(
    private val api: ApiConnection
) : ProgressionRepository {
    override suspend fun runAnalysis(userId: Int, type: String): Result<List<Pair<String, Double>>> {
        return try {
            val result = api.runPolynomialRegressionAndWait(userId, type)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
