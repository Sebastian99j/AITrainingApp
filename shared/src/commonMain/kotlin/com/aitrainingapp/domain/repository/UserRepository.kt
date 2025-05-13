package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.User

interface UserRepository {
    suspend fun login(username: String, password: String): com.aitrainingapp.util.Result<User>
    suspend fun registerAndLogin(username: String, password: String): com.aitrainingapp.util.Result<User>
}
