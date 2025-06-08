package com.aitrainingapp.data.repository

import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.domain.repository.ProgressionRepositoryIOS
import com.aitrainingapp.model.RegressionPoint

class ProgressionRepositoryIOSImpl(
    private val api: ApiConnection
) : ProgressionRepositoryIOS {

    override suspend fun runAnalysis(userId: Int, type: String): Result<List<RegressionPoint>> {
        return try {
            val result = api.runPolynomialRegressionAndWait(userId, type)
            val transformed = result.map { RegressionPoint(it.first, it.second) }
            Result.success(transformed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
