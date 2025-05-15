package com.aitrainingapp.data.remote

import com.aitrainingapp.data.remote.model.QLearningFeedbackDto
import com.aitrainingapp.data.remote.model.RegressionTaskResult
import com.aitrainingapp.data.remote.model.TrainingSeriesDto
import com.aitrainingapp.data.remote.model.TrainingTypeDto
import com.aitrainingapp.data.remote.model.UserDto
import com.aitrainingapp.domain.model.TrainingSeries
import com.aitrainingapp.domain.model.TrainingType
import com.aitrainingapp.util.Cache
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiConnection {

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

    suspend fun getAllTrainingSeriesByUser(userId: Int): List<TrainingSeriesDto> {
        val token = Cache.accessToken

        val response: HttpResponse = client.post("http://10.0.2.2:8333/data/api/TrainingSeries/user") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("Id" to userId))
            header("Authorization", "Bearer $token")
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                val jsonString = response.bodyAsText()
                Json.decodeFromString(ListSerializer(TrainingSeriesDto.serializer()), jsonString)
            }
            HttpStatusCode.NotFound -> emptyList()
            else -> {
                println("Unexpected error: ${response.status}")
                emptyList()
            }
        }
    }

    suspend fun runPolynomialRegressionAndWait(userId: Int): List<Pair<String, Double>> {
        val token = Cache.accessToken

        // 1. Start async regression task
        val taskStartResponse: JsonObject = client.post("http://10.0.2.2:8333/ai/polynomial_regression_async") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("user_id" to userId))
            header("Authorization", "Bearer $token")
        }.body()

        val taskId = taskStartResponse["task_id"]?.jsonPrimitive?.content
            ?: throw Exception("Brak task_id w odpowiedzi")

        // 2. Poll /task_status/<id> until completed
        repeat(30) {
            delay(1000)

            val statusResponse: HttpResponse = client.get("http://10.0.2.2:8333/ai/task_status/$taskId") {
                header("Authorization", "Bearer $token")
            }

            if (statusResponse.status == HttpStatusCode.OK) {
                val parsed = statusResponse.body<RegressionTaskResult>()

                if (parsed.status == "completed") {
                    val dates = parsed.result.dates
                    val weights = parsed.result.weights

                    if (dates.size != weights.size) throw Exception("Liczba dat ≠ liczba wag")

                    return dates.zip(weights) // List<Pair<String, Double>>
                }
            } else if (statusResponse.status == HttpStatusCode.InternalServerError) {
                throw Exception("Błąd serwera podczas pobierania statusu")
            }
        }

        throw Exception("Timeout – analiza nie została ukończona w czasie")
    }

    suspend fun runQLearningPredictionAndWait(userId: Int): String {
        val token = Cache.accessToken

        // 1. Start async Q-learning prediction task
        val taskStartResponse: JsonObject = client.post("http://10.0.2.2:8333/ai/q_learning_predict_async") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("user_id" to userId))
            header("Authorization", "Bearer $token")
        }.body()

        val taskId = taskStartResponse["task_id"]?.jsonPrimitive?.content
            ?: throw Exception("Brak task_id w odpowiedzi")

        // 2. Poll /task_status/<id> until completed
        repeat(30) {
            delay(1000)

            val statusResponse: HttpResponse = client.get("http://10.0.2.2:8333/ai/task_status/$taskId") {
                header("Authorization", "Bearer $token")
            }

            if (statusResponse.status == HttpStatusCode.OK) {
                val responseJson = statusResponse.body<JsonObject>()
                val status = responseJson["status"]?.jsonPrimitive?.content

                if (status == "completed") {
                    return responseJson["result"]?.jsonPrimitive?.content
                        ?: throw Exception("Brak result w odpowiedzi zakończonego taska")
                }
            } else if (statusResponse.status == HttpStatusCode.InternalServerError) {
                throw Exception("Błąd serwera podczas pobierania statusu predykcji")
            }
        }

        throw Exception("Timeout – predykcja nie została ukończona w czasie")
    }

    suspend fun addTrainingSeries(series: TrainingSeriesDto): Boolean {
        val token = Cache.accessToken
        return try {
            client.post("http://10.0.2.2:8333/data/api/TrainingSeries/new") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(series)
            }
            true
        } catch (e: Exception) {
            println("Błąd dodawania serii: ${e.message}")
            false
        }
    }

    suspend fun sendQLearningFeedback(feedback: QLearningFeedbackDto): Boolean {
        val token = Cache.accessToken

        val response = client.post("http://10.0.2.2:8333/ai/q_learning_predict_feedback") {
            contentType(ContentType.Application.Json)
            setBody(feedback)
            header("Authorization", "Bearer $token")
        }

        return response.status == HttpStatusCode.OK
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

@Serializable
data class TaskResponse(val task_id: String)

@Serializable
data class TaskStatusResponse(val status: String, val result: List<Pair<String, Double>> = emptyList())