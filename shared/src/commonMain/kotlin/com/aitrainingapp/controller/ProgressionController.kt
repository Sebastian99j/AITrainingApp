package com.aitrainingapp.controller

import com.aitrainingapp.domain.repository.ProgressionRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class ProgressionController(
    private val repository: ProgressionRepository,
    private val userRepo: UserLocalRepository,
    private val trainingTypeRepo: TrainingTypeRepository,
    private val scope: CoroutineScope
) {
    private val _regressionData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val regressionData: StateFlow<List<Pair<String, Double>>> = _regressionData

    private val _forecastMap = MutableStateFlow<Map<String, Double>>(emptyMap())
    val forecastMap: StateFlow<Map<String, Double>> = _forecastMap

    private val _exercises = MutableStateFlow<List<String>>(emptyList())
    val exercises: StateFlow<List<String>> = _exercises

    fun loadData() {
        scope.launch {
            _exercises.value = trainingTypeRepo.getAll().map { it.name }
        }
    }

    fun analyzeProgression(type: String) {
        scope.launch {
            val userId = userRepo.getUserId() ?: run {
                _regressionData.value = emptyList()
                _forecastMap.value = emptyMap()
                return@launch
            }

            val result = repository.runAnalysis(userId, type)
            result.onSuccess { regression ->
                _regressionData.value = regression

                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                val forecast = listOf(30, 60, 90, 120).associate { days ->
                    val futureDate = today.plus(days, DateTimeUnit.DAY)
                    val key = "Za $days dni"
                    val value = regression.find { it.first == futureDate.toString() }?.second ?: 0.0
                    key to value
                }

                _forecastMap.value = forecast
            }.onFailure {
                _regressionData.value = emptyList()
                _forecastMap.value = emptyMap()
            }
        }
    }
}
