package com.aitrainingapp.android.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.domain.model.ExerciseSeries
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExerciseViewModel(
    private val repository: ExerciseRepository,
    private val localUserRepository: UserLocalRepository
) : ViewModel() {

    private val _seriesList = MutableStateFlow<List<ExerciseSeries>>(emptyList())
    val seriesList: StateFlow<List<ExerciseSeries>> = _seriesList

    private var _lastDuration: Int = 0
    private var _startTime: Long = 0

    fun setDuration(seconds: Int) {
        _lastDuration = seconds
    }

    private val _timerRunning = mutableStateOf(false)
    val timerRunning: State<Boolean> = _timerRunning

    fun toggleTimer() {
        _timerRunning.value = !_timerRunning.value
        if (_timerRunning.value) {
            _startTime = System.currentTimeMillis()
        } else {
            _lastDuration = ((System.currentTimeMillis() - _startTime) / 1000).toInt()
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
