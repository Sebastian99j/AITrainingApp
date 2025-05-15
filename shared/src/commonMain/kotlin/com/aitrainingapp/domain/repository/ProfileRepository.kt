package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.Profile

interface ProfileRepository {
    suspend fun getUserProfile(): Profile?
}