package com.aitrainingapp.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RegressionTaskResult(
    val result: RegressionResult,
    val status: String
)

@Serializable
data class RegressionResult(
    val data_count: Int,
    val dates: List<String>,
    val weights: List<Double>,
    val user_name: String
)