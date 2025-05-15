package com.aitrainingapp.domain.repository

interface ProgressionRepository {
    suspend fun runAnalysis(userId: Int, type: String): Result<List<Pair<String, Double>>>
}