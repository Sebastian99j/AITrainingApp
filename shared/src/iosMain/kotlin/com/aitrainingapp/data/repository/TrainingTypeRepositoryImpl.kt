package com.aitrainingapp.data.repository

import com.aitrainingapp.domain.model.TrainingType
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.data.remote.ApiConnection

class TrainingTypeRepositoryImpl(
    private val api: ApiConnection
) : TrainingTypeRepository {

    override suspend fun getAll(): List<TrainingType> {
        return api.getAll()
    }

    override suspend fun add(name: String): Boolean {
        return api.add(name)
    }
}
