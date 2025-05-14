package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.domain.model.TrainingType
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingTypeViewModel(
    private val repository: TrainingTypeRepository
) : ViewModel() {

    private val _types = MutableStateFlow<List<TrainingType>>(emptyList())
    val types: StateFlow<List<TrainingType>> = _types

    fun loadTypes() {
        viewModelScope.launch {
            _types.value = repository.getAll()
        }
    }

    fun addTrainingType(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (repository.add(name)) {
                loadTypes()
                onSuccess()
            }
        }
    }
}
