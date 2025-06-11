package com.aitrainingapp.controller

import com.aitrainingapp.domain.repository.ProgressionRepository
import com.aitrainingapp.domain.repository.ProgressionRepositoryIOS
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import com.aitrainingapp.model.RegressionPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class ProgressionController(
    private val repository: ProgressionRepositoryIOS,
    private val userRepo: UserLocalRepository,
    private val trainingTypeRepo: TrainingTypeRepository,
    private val scope: CoroutineScope
) {
    private val _regressionData = MutableStateFlow<List<RegressionPoint>>(emptyList())
    val regressionData: StateFlow<List<RegressionPoint>> = _regressionData

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
                    val value = regression.find { it.key == futureDate.toString() }?.value ?: 0.0
                    key to value
                }

                _forecastMap.value = forecast
            }.onFailure {
                _regressionData.value = emptyList()
                _forecastMap.value = emptyMap()
            }
        }
    }

    fun observeRegressionData(callback: (List<RegressionPoint>) -> Unit): Job {
        return scope.launch(Dispatchers.Main) {
            regressionData.collect { callback(it) }
        }
    }

    fun observeForecastMap(callback: (Map<String, Double>) -> Unit): Job {
        return scope.launch(Dispatchers.Main) {
            forecastMap.collect { callback(it) }
        }
    }

    fun observeExercises(callback: (List<String>) -> Unit): Job {
        return scope.launch(Dispatchers.Main) {
            exercises.collect { callback(it) }
        }
    }
}
