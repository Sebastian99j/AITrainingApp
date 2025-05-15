package com.aitrainingapp.android.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.data.remote.model.QLearningFeedbackDto
import com.aitrainingapp.domain.model.ExerciseSeries
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.QLearningRepository
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
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
    private val trainingTypeRepository: TrainingTypeRepository,
    private val trainingHistoryRepository: TrainingHistoryRepository,
    private val qLearningRepository: QLearningRepository
) : ViewModel() {

    private val _seriesList = MutableStateFlow<List<ExerciseSeries>>(emptyList())
    val seriesList: StateFlow<List<ExerciseSeries>> = _seriesList

    private val _exercises = MutableStateFlow<List<String>>(emptyList())
    val exercises: StateFlow<List<String>> = _exercises

    private val _recommendation = mutableStateOf<String?>(null)
    val recommendation: State<String?> = _recommendation

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private val _nextTrainingPlan = mutableStateOf<String?>(null)
    val nextTrainingPlan: State<String?> = _nextTrainingPlan

    private val _feedbackSent = mutableStateOf(false)
    val feedbackSent: State<Boolean> = _feedbackSent

    private var _lastDuration: Int = 0
    private var _startTime: Long = 0
    private var timerJob: Job? = null
    private var lastRawRecommendation: String? = null

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
            val profile = localUserRepository.getUserProfile()

            val raw = try {
                repository.getQLearningRecommendation(userId)
            } catch (e: Exception) {
                _recommendation.value = "Błąd: ${e.message}"
                return@launch
            }

            lastRawRecommendation = raw

            _recommendation.value = mapRecommendation(raw, profile)

            val lastTraining = trainingHistoryRepository.getTrainingHistory(userId).lastOrNull()
            if (lastTraining == null || profile == null) {
                _nextTrainingPlan.value = "Brak danych do zaplanowania treningu"
                return@launch
            }

            val adjusted = when (raw) {
                "increase_weight" -> lastTraining.copy(weight = lastTraining.weight + profile.weightStep)
                "decrease_weight" -> lastTraining.copy(weight = (lastTraining.weight - profile.weightStep).coerceAtLeast(0f))
                "increase_reps" -> lastTraining.copy(reps = lastTraining.reps + profile.repsStep)
                "decrease_reps" -> lastTraining.copy(reps = (lastTraining.reps - profile.repsStep).coerceAtLeast(1))
                "increase_sets" -> lastTraining.copy(sets = lastTraining.sets + profile.setsStep)
                "decrease_sets" -> lastTraining.copy(sets = (lastTraining.sets - profile.setsStep).coerceAtLeast(1))
                else -> lastTraining
            }

            _nextTrainingPlan.value = """
            📝 Kolejny trening:
            • Ciężar: ${adjusted.weight} kg
            • Powtórzenia: ${adjusted.reps}
            • Serie: ${adjusted.sets}
            • RPE: ${adjusted.rpe}
        """.trimIndent()
        }
    }

    fun sendFeedback(successful: Boolean) {
        if (_feedbackSent.value) return

        viewModelScope.launch {
            val user = localUserRepository.getUserById() ?: return@launch
            val history = trainingHistoryRepository.getTrainingHistory(user.id)
            val last = history.lastOrNull() ?: return@launch

            val feedback = QLearningFeedbackDto(
                user_id = user.id,
                type = "Bench press",
                action = lastRawRecommendation ?: "unknown",
                successful = successful,
                weight = last.weight,
                reps = last.reps,
                sets = last.sets,
                RPE = last.rpe,
                training_goal = "hypertrophy"
            )

            val result = qLearningRepository.sendFeedback(feedback)
            if (result) {
                _feedbackSent.value = true
            }
        }
    }

    fun addSeries(exercise: String, weight: Float, reps: Int, sets: Int, rpe: Int) {
        val now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val new = ExerciseSeries(
            weight = weight,
            reps = reps,
            sets = sets,
            rpe = rpe,
            durationSeconds = _lastDuration,
            date = now,
            exercise = exercise
        )
        _seriesList.value = _seriesList.value + new
    }

    fun totalWeight(): Int = _seriesList.value.sumOf { (it.weight * it.reps * it.sets).toInt() }
    fun totalReps() = _seriesList.value.sumOf { it.reps }
    fun totalSets() = _seriesList.value.sumOf { it.sets }
    fun averageDuration() =
        if (_seriesList.value.isNotEmpty()) _seriesList.value.sumOf { it.durationSeconds } / _seriesList.value.size
        else 0

    fun saveAll() {
        viewModelScope.launch {
            val userId = localUserRepository.getUserId() ?: return@launch

            val grouped = _seriesList.value
                .groupBy { it.weight }
                .map { (weight, group) ->
                    val avgReps = group.map { it.reps }.average().toInt()
                    val totalSets = group.sumOf { it.sets }
                    val avgRpe = group.map { it.rpe }.average().toInt()
                    val avgDuration = group.map { it.durationSeconds }.average().toInt()
                    val date = group.first().date
                    val exercise = group.first().exercise

                    ExerciseSeries(
                        userId = userId,
                        weight = weight,
                        reps = avgReps,
                        sets = totalSets,
                        rpe = avgRpe,
                        durationSeconds = avgDuration,
                        date = date,
                        exercise = exercise
                    )
                }

            grouped.forEach { series ->
                repository.saveSeries(series)
            }

            _seriesList.value = emptyList()
        }
    }

    private fun mapRecommendation(raw: String, profile: Profile?): String {
        if (profile == null) return "Brak danych profilu"

        return when (raw) {
            "keep_same" -> "Utrzymaj parametry treningowe"
            "increase_weight" -> "Zwiększ ciężar o ${profile.weightStep} kg względem poprzedniego treningu"
            "decrease_weight" -> "Zmniejsz ciężar o ${profile.weightStep} kg względem poprzedniego treningu"
            "increase_reps" -> "Zwiększ liczbę powtórzeń o ${profile.repsStep} względem poprzedniego treningu"
            "decrease_reps" -> "Zmniejsz liczbę powtórzeń o ${profile.repsStep} względem poprzedniego treningu"
            "increase_sets" -> "Zwiększ liczbę serii o ${profile.setsStep} względem poprzedniego treningu"
            "decrease_sets" -> "Zmniejsz liczbę serii o ${profile.setsStep} względem poprzedniego treningu"
            else -> "Nieznana rekomendacja: $raw"
        }
    }
}
