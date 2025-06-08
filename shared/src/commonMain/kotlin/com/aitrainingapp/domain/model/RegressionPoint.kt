package com.aitrainingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class RegressionPoint(
    val key: String,
    val value: Double
)
