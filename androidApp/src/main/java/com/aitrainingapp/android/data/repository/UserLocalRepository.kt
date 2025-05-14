package com.aitrainingapp.android.data.repository

import com.aitrainingapp.domain.model.User

interface UserLocalRepository {
    suspend fun insertUser(user: User)
}