package com.aitrainingapp.controller

import com.aitrainingapp.domain.model.TrainingType
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingTypeController(
    private val repository: TrainingTypeRepository,
    private val scope: CoroutineScope
) {
    private val _types = MutableStateFlow<List<TrainingType>>(emptyList())
    val types: StateFlow<List<TrainingType>> = _types

    fun types(): StateFlow<List<TrainingType>> = _types

    fun loadTypes() {
        scope.launch {
            _types.value = repository.getAll()
        }
    }

    fun addTrainingType(name: String, onSuccess: () -> Unit) {
        scope.launch {
            if (repository.add(name)) {
                loadTypes()
                onSuccess()
            }
        }
    }
}
