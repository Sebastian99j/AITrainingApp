package com.aitrainingapp.domain.usecase

import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.util.Result

class RegisterUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        return repo.registerAndLogin(username, password)
    }
}

