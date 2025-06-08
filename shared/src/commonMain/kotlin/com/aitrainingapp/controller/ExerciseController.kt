package com.aitrainingapp.controller

import com.aitrainingapp.data.remote.model.QLearningFeedbackDto
import com.aitrainingapp.domain.model.ExerciseSeries
import com.aitrainingapp.domain.model.Profile
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.QLearningRepository
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ExerciseController(
    private val repository: ExerciseRepository,
    private val userRepo: UserLocalRepository,
    private val typeRepo: TrainingTypeRepository,
    private val historyRepo: TrainingHistoryRepository,
    private val qLearningRepo: QLearningRepository,
    private val coroutineScope: CoroutineScope
) {

    val seriesList = MutableStateFlow<List<ExerciseSeries>>(emptyList())
    val exercises = MutableStateFlow<List<String>>(emptyList())
    val recommendation = MutableStateFlow<String?>(null)
    val nextTrainingPlan = MutableStateFlow<String?>(null)
    val feedbackSent = MutableStateFlow(false)
    val selectedExercise = MutableStateFlow<String?>(null)

    private var lastRawRecommendation: String? = null
    private var lastDuration: Int = 0

    fun setSelectedExercise(name: String) {
        selectedExercise.value = name
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

        seriesList.value = seriesList.value + new
    }

    fun saveAll() {
        coroutineScope.launch {
            val userId = userRepo.getUserId() ?: return@launch

            val grouped = seriesList.value
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

            seriesList.value = emptyList()
        }
    }

    fun fetchRecommendation() {
        coroutineScope.launch {
            val userId = userRepo.getUserId() ?: return@launch
            val profile = userRepo.getUserProfile()

            val raw = try {
                repository.getQLearningRecommendation(userId)
            } catch (e: Exception) {
                recommendation.value = "Błąd: ${e.message}"
                return@launch
            }

            lastRawRecommendation = raw
            recommendation.value = mapRecommendation(raw, profile)

            val lastTraining = historyRepo.getTrainingHistory(userId).lastOrNull()
            if (lastTraining == null || profile == null) {
                nextTrainingPlan.value = "Brak danych do zaplanowania treningu (nie ustawiono profilu)"
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

            nextTrainingPlan.value = """
                📝 Kolejny trening:
                • Ciężar: ${adjusted.weight} kg
                • Powtórzenia: ${adjusted.reps}
                • Serie: ${adjusted.sets}
                • RPE: ${adjusted.rpe}
            """.trimIndent()
        }
    }

    fun sendFeedback(successful: Boolean) {
        if (feedbackSent.value) return

        coroutineScope.launch {
            val user = userRepo.getUserById() ?: return@launch
            val history = historyRepo.getTrainingHistory(user.id)
            val last = history.lastOrNull() ?: return@launch

            val feedback = QLearningFeedbackDto(
                user_id = user.id,
                type = selectedExercise.value ?: "unknown",
                action = lastRawRecommendation ?: "unknown",
                successful = successful,
                weight = last.weight,
                reps = last.reps,
                sets = last.sets,
                RPE = last.rpe,
                training_goal = "hypertrophy"
            )

            val result = qLearningRepo.sendFeedback(feedback)
            if (result) {
                feedbackSent.value = true
            }
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

    val elapsedSeconds = MutableStateFlow(0)
    val timerRunning = MutableStateFlow(false)

    fun toggleTimer() {
        timerRunning.value = !timerRunning.value
    }

    fun totalWeight(): Int {
        return seriesList.value.sumOf { it.weight.toInt() * it.reps * it.sets }
    }

    fun totalReps(): Int {
        return seriesList.value.sumOf { it.reps * it.sets }
    }

    fun totalSets(): Int {
        return seriesList.value.sumOf { it.sets }
    }

    fun averageDuration(): Int {
        return if (seriesList.value.isEmpty()) 0 else
            seriesList.value.map { it.durationSeconds }.average().toInt()
    }
}

