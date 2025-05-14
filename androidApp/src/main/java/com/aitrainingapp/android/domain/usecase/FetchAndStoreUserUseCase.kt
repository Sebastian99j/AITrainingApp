package com.aitrainingapp.android.domain.usecase

import com.aitrainingapp.domain.repository.UserLocalRepository
import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.util.Result

class FetchAndStoreUserUseCase(
    private val remoteRepo: UserRepository,
    private val localRepo: UserLocalRepository
) {
    suspend operator fun invoke(username: String): Result<User> {
        return when (val result = remoteRepo.fetchUserFromApi(username)) {
            is Result.Success -> {
                localRepo.insertUser(result.data)
                result
            }
            is Result.Failure -> result
        }
    }
}