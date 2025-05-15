package com.aitrainingapp.domain.repository

import com.aitrainingapp.domain.model.ExerciseSeries

interface ExerciseRepository {
    suspend fun saveSeries(series: ExerciseSeries, exercise: String): Boolean
}
