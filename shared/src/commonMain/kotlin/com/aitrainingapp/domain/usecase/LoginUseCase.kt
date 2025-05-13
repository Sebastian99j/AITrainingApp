package com.aitrainingapp.domain.usecase

import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.util.Result

class LoginUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        return repo.login(username, password)
    }
}
