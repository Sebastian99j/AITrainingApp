package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.TrainingType

interface TrainingTypeRepository {
    suspend fun getAll(): List<TrainingType>
    suspend fun add(name: String): Boolean
}
