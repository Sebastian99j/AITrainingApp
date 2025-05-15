package com.aitrainingapp.android.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.domain.model.ExerciseSeries
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExerciseViewModel(
    private val repository: ExerciseRepository,
    private val localUserRepository: UserLocalRepository,
    private val trainingTypeRepository: TrainingTypeRepository
) : ViewModel() {

    private val _seriesList = MutableStateFlow<List<ExerciseSeries>>(emptyList())
    val seriesList: StateFlow<List<ExerciseSeries>> = _seriesList

    private val _exercises = MutableStateFlow<List<String>>(emptyList())
    val exercises: StateFlow<List<String>> = _exercises

    private val _recommendation = mutableStateOf<String?>(null)
    val recommendation: State<String?> = _recommendation

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private var _lastDuration: Int = 0
    private var _startTime: Long = 0
    private var timerJob: Job? = null

    fun setDuration(seconds: Int) {
        _lastDuration = seconds
    }

    private val _timerRunning = mutableStateOf(false)
    val timerRunning: State<Boolean> = _timerRunning

    fun toggleTimer() {
        _timerRunning.value = !_timerRunning.value
        if (_timerRunning.value) {
            _startTime = System.currentTimeMillis()
            timerJob = viewModelScope.launch {
                while (true) {
                    _elapsedSeconds.value = ((System.currentTimeMillis() - _startTime) / 1000).toInt()
                    delay(1000)
                }
            }
        } else {
            timerJob?.cancel()
            _lastDuration = _elapsedSeconds.value
            _elapsedSeconds.value = 0
        }
    }

    fun loadData(){
        viewModelScope.launch {
            _exercises.value = trainingTypeRepository.getAll().map { x -> x.name }
        }
    }

    fun fetchRecommendation() {
        viewModelScope.launch {
            val userId = localUserRepository.getUserId() ?: return@launch
            try {
                _recommendation.value = repository.getQLearningRecommendation(userId)
            } catch (e: Exception) {
                _recommendation.value = "Błąd: ${e.message}"
            }
        }
    }

    fun addSeries(weight: Float, reps: Int, sets: Int, rpe: Int) {
        val now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val new = ExerciseSeries(weight, reps, sets, rpe, _lastDuration, now)
        _seriesList.value = _seriesList.value + new
    }

    fun totalWeight(): Int = _seriesList.value.sumOf { (it.weight * it.reps * it.sets).toInt() }
    fun totalReps() = _seriesList.value.sumOf { it.reps }
    fun totalSets() = _seriesList.value.sumOf { it.sets }
    fun averageDuration() =
        if (_seriesList.value.isNotEmpty()) _seriesList.value.sumOf { it.durationSeconds } / _seriesList.value.size
        else 0

    fun saveAll(exercise: String) {
        viewModelScope.launch {
            val userId = localUserRepository.getUserId() ?: return@launch

            _seriesList.value.forEach {
                repository.saveSeries(it.copy(userId = userId, exercise = exercise), exercise)
            }
        }
    }
}
