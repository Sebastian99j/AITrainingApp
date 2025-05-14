package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.User
import com.aitrainingapp.util.Result

interface UserRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun registerAndLogin(username: String, password: String): Result<User>
    suspend fun fetchUserFromApi(username: String): Result<User>
}
