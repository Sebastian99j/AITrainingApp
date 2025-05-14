package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.User

interface UserLocalRepository {
    suspend fun insertUser(user: User)
    suspend fun getUserById(): User?
    suspend fun getUserId(): Int?
}