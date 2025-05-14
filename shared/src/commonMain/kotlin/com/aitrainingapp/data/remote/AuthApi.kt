package com.aitrainingapp.data.remote

import com.aitrainingapp.data.remote.model.TrainingTypeDto
import com.aitrainingapp.data.remote.model.UserDto
import com.aitrainingapp.domain.model.TrainingType
import com.aitrainingapp.util.Cache
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AuthApi {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun register(username: String, password: String): HttpResponse {
        println("🔵 Registering user: $username with password: $password")

        return client.post("http://10.0.2.2:8333/auth/register"){
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }
    }

    suspend fun login(username: String, password: String): LoginResponse {
        return client.post("http://10.0.2.2:8333/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }.body()
    }

    suspend fun getUser(username: String): UserDto {
        return client.post("http://10.0.2.2:8333/data/api/User") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("Username" to username))

            val token = Cache.accessToken
            if (!token.isNullOrBlank()) {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getAll(): List<TrainingType> {
        val token = Cache.accessToken
        val result: List<TrainingTypeDto> = client.get("http://10.0.2.2:8333/data/api/TrainingType") {
            header("Authorization", "Bearer $token")
        }.body()

        return result.map { TrainingType(it.id, it.name) }
    }

    suspend fun add(name: String): Boolean {
        val token = Cache.accessToken
        return try {
            client.post("http://10.0.2.2:8333/data/api/TrainingType") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf("name" to name))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
data class LoginResponse(val access_token: String)
