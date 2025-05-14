package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.domain.model.TrainingSeries
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingHistoryViewModel(
    private val repository: TrainingHistoryRepository,
    private val userRepository: UserLocalRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<TrainingSeries>>(emptyList())
    val history: StateFlow<List<TrainingSeries>> = _history

    fun loadHistory() {
        viewModelScope.launch {
            val id: Int? = userRepository.getUserById()?.id
            if (id == null) {
                _history.value = emptyList()
                return@launch
            }
            _history.value = repository.getTrainingHistory(id)
        }
    }
}
