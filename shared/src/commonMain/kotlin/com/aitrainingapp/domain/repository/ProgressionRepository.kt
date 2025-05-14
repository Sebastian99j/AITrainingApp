package com.aitrainingapp.domain.repository

interface ProgressionRepository {
    suspend fun runAnalysis(userId: Int): Result<List<Pair<String, Double>>>
}