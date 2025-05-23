package com.aitrainingapp.android.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.domain.repository.ProgressionRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProgressionViewModel(
    private val repository: ProgressionRepository,
    private val localUserRepository: UserLocalRepository,
    private val trainingTypeRepository: TrainingTypeRepository
) : ViewModel() {

    private val _regressionData = mutableStateOf<List<Pair<String, Double>>>(emptyList())
    val regressionData: State<List<Pair<String, Double>>> = _regressionData

    private val _forecastMap = mutableStateOf<Map<String, Double>>(emptyMap())
    val forecastMap: State<Map<String, Double>> = _forecastMap

    private val _exercises = MutableStateFlow<List<String>>(emptyList())
    val exercises: StateFlow<List<String>> = _exercises

    fun loadData(){
        viewModelScope.launch {
            _exercises.value = trainingTypeRepository.getAll().map { x -> x.name }
        }
    }

    fun analyzeProgression(type: String) {
        viewModelScope.launch {
            val userId = localUserRepository.getUserId()
            if (userId == null) {
                _regressionData.value = emptyList()
                _forecastMap.value = emptyMap()
                return@launch
            }

            val result = repository.runAnalysis(userId, type)
            result.onSuccess { regression ->
                _regressionData.value = regression

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val today = LocalDate.now()

                val forecast = listOf(30, 60, 90, 120).associate { days ->
                    val date = today.plusDays(days.toLong()).format(formatter)
                    "Za $days dni" to (regression.find { it.first == date }?.second ?: 0.0)
                }

                _forecastMap.value = forecast
            }.onFailure {
                _regressionData.value = emptyList()
                _forecastMap.value = emptyMap()
            }
        }
    }
}
