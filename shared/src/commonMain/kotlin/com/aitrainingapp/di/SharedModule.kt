package com.aitrainingapp.di

import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.ProfileRepository
import com.aitrainingapp.domain.repository.ProgressionRepository
import com.aitrainingapp.domain.repository.QLearningRepository
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserLocalRepository
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.domain.usecase.FetchAndStoreUserUseCase
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase

class SharedModule(
    private val userRepository: UserRepository,
    private val localUserRepository: UserLocalRepository,
    private val trainingTypeRepository: TrainingTypeRepository,
    private val trainingHistoryRepository: TrainingHistoryRepository,
    private val progressionRepository: ProgressionRepository,
    private val exerciseRepository: ExerciseRepository,
    private val profileRepository: ProfileRepository,
    private val qLearningRepository: QLearningRepository
) {
    fun provideLoginUseCase() = LoginUseCase(userRepository)
    fun provideRegisterUseCase() = RegisterUseCase(userRepository)
    fun provideFetchAndStoreUserUseCase() = FetchAndStoreUserUseCase(userRepository, localUserRepository)

    fun provideTrainingTypeRepository() = trainingTypeRepository
    fun provideTrainingHistoryRepository() = trainingHistoryRepository
    fun provideLocalUserRepository() = localUserRepository
    fun provideProgressionRepository() = progressionRepository
    fun provideExerciseRepository() = exerciseRepository
    fun provideQLearningRepository() = qLearningRepository
}

