package com.aitrainingapp.controller

import com.aitrainingapp.data.remote.model.QLearningFeedbackDto
import com.aitrainingapp.domain.model.ExerciseSeries
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.repository.*
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ExerciseController(
    private val repository: ExerciseRepository,
    private val userRepo: UserLocalRepository,
    private val typeRepo: TrainingTypeRepository,
    private val historyRepo: TrainingHistoryRepository,
    private val qLearningRepo: QLearningRepository,
    private val coroutineScope: CoroutineScope = MainScope()
) {
    // Backing properties
    private val _seriesList = MutableStateFlow<List<ExerciseSeries>>(emptyList())
    private val _exercises = MutableStateFlow<List<String>>(emptyList())
    private val _recommendation = MutableStateFlow<String?>(null)
    private val _nextTrainingPlan = MutableStateFlow<String?>(null)
    private val _feedbackSent = MutableStateFlow(false)
    private val _selectedExercise = MutableStateFlow<String?>(null)
    private val _elapsedSeconds = MutableStateFlow(0)
    private val _timerRunning = MutableStateFlow(false)

    // Exposed as read-only StateFlows with annotation
    @NativeCoroutinesState val seriesList: StateFlow<List<ExerciseSeries>> get() = _seriesList
    @NativeCoroutinesState val exercises: StateFlow<List<String>> get() = _exercises
    @NativeCoroutinesState val recommendation: StateFlow<String?> get() = _recommendation
    @NativeCoroutinesState val nextTrainingPlan: StateFlow<String?> get() = _nextTrainingPlan
    @NativeCoroutinesState val feedbackSent: StateFlow<Boolean> get() = _feedbackSent
    @NativeCoroutinesState val selectedExercise: StateFlow<String?> get() = _selectedExercise
    @NativeCoroutinesState val elapsedSeconds: StateFlow<Int> get() = _elapsedSeconds
    @NativeCoroutinesState val timerRunning: StateFlow<Boolean> get() = _timerRunning

    fun getSeriesList(): List<ExerciseSeries> = _seriesList.value
    fun getExercises(): List<String> = _exercises.value
    fun getRecommendation(): String? = _recommendation.value
    fun isFeedbackSent(): Boolean = _feedbackSent.value
    fun isTimerRunning(): Boolean = _timerRunning.value
    fun getElapsedSeconds(): Int = _elapsedSeconds.value
    fun getSelectedExercise(): String? = _selectedExercise.value

    private var lastRawRecommendation: String? = null
    private var lastDuration: Int = 0

    fun setSelectedExercise(name: String) {
        _selectedExercise.value = name
    }

    fun addSeries(
        exercise: String,
        weight: Float,
        reps: Int,
        sets: Int,
        rpe: Int,
        duration: Int
    ) {
        lastDuration = duration
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val new = ExerciseSeries(
            weight = weight,
            reps = reps,
            sets = sets,
            rpe = rpe,
            durationSeconds = duration,
            date = now,
            exercise = exercise
        )
        _seriesList.value = _seriesList.value + new
    }

    fun saveAll() {
        coroutineScope.launch {
            val userId = userRepo.getUserId() ?: return@launch
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

            grouped.forEach { repository.saveSeries(it) }
            _seriesList.value = emptyList()
        }
    }

    fun fetchRecommendation() {
        coroutineScope.launch {
            val userId = userRepo.getUserId() ?: return@launch
            val profile = userRepo.getUserProfile()
            val raw = try {
                repository.getQLearningRecommendation(userId)
            } catch (e: Exception) {
                _recommendation.value = "Błąd: ${e.message}"
                return@launch
            }

            lastRawRecommendation = raw
            _recommendation.value = mapRecommendation(raw, profile)

            val lastTraining = historyRepo.getTrainingHistory(userId).lastOrNull()
            if (lastTraining == null || profile == null) {
                _nextTrainingPlan.value = "Brak danych do zaplanowania treningu (nie ustawiono profilu)"
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

        coroutineScope.launch {
            val user = userRepo.getUserById() ?: return@launch
            val last = historyRepo.getTrainingHistory(user.id).lastOrNull() ?: return@launch

            val feedback = QLearningFeedbackDto(
                user_id = user.id,
                type = _selectedExercise.value ?: "unknown",
                action = lastRawRecommendation ?: "unknown",
                successful = successful,
                weight = last.weight,
                reps = last.reps,
                sets = last.sets,
                RPE = last.rpe,
                training_goal = "hypertrophy"
            )

            val result = qLearningRepo.sendFeedback(feedback)
            if (result) _feedbackSent.value = true
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

    fun toggleTimer() {
        _timerRunning.value = !_timerRunning.value
    }

    fun totalWeight(): Int = _seriesList.value.sumOf { it.weight.toInt() * it.reps * it.sets }
    fun totalReps(): Int = _seriesList.value.sumOf { it.reps * it.sets }
    fun totalSets(): Int = _seriesList.value.sumOf { it.sets }
    fun averageDuration(): Int =
        if (_seriesList.value.isEmpty()) 0 else _seriesList.value.map { it.durationSeconds }.average().toInt()
}