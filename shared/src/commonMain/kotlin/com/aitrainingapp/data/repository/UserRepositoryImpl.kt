package com.aitrainingapp.data.repository

import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.database.ProfileQueries
import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.util.Cache
import com.aitrainingapp.util.Result
import io.ktor.client.statement.bodyAsText

class UserRepositoryImpl(
    private val api: ApiConnection,
    private val queries: ProfileQueries
) : UserRepository {
    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val loginResponse = api.login(username, password)

            val user = User(
                id = 0,
                username = username,
                aiIdentifier = null,
                profileId = null,
                active = true,
                notificationOn = false
            )

            Cache.accessToken = loginResponse.access_token

            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(Exception("Niepoprawna nazwa użytkownika lub hasło"))
        }
    }

    override suspend fun registerAndLogin(username: String, password: String): Result<User> {
        return try {
            val registerResponse = api.register(username, password)

            if (registerResponse.status.value == 201) {
                val loginResponse = api.login(username, password)

                val user = User(
                    id = 0,
                    username = username,
                    aiIdentifier = null,
                    profileId = null,
                    active = true,
                    notificationOn = false
                )

                Cache.accessToken = loginResponse.access_token

                Result.Success(user)
            } else {
                println("RegisterDebug: Status: ${registerResponse.status}, Body: ${registerResponse.bodyAsText()}")
                Result.Failure(Exception("Register failed: ${registerResponse.status}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun fetchUserFromApi(username: String): Result<User> {
        return try {
            val userDto = api.getUser(username)

            val user = User(
                id = userDto.id,
                username = userDto.username,
                aiIdentifier = userDto.aiIdentifier,
                profileId = queries.getAllProfiles().executeAsList().firstOrNull()?.id?.toInt() ?: 1,
                active = true,
                notificationOn = false
            )

            Result.Success(user)

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
