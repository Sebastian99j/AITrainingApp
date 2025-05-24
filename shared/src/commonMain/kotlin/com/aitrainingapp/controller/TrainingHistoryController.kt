package com.aitrainingapp.controller

import com.aitrainingapp.domain.model.TrainingSeries
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingHistoryController(
    private val repository: TrainingHistoryRepository,
    private val userRepository: UserLocalRepository,
    private val scope: CoroutineScope
) {
    private val _history = MutableStateFlow<List<TrainingSeries>>(emptyList())
    val history: StateFlow<List<TrainingSeries>> = _history

    fun loadHistory() {
        scope.launch {
            val id = userRepository.getUserById()?.id
            _history.value = if (id == null) {
                emptyList()
            } else {
                repository.getTrainingHistory(id)
            }
        }
    }
}
