package com.aitrainingapp.android.di

import android.content.Context
import com.aitrainingapp.android.data.repository.ExerciseRepositoryImpl
import com.aitrainingapp.android.data.repository.ProfileRepositoryImpl
import com.aitrainingapp.android.data.repository.ProgressionRepositoryImpl
import com.aitrainingapp.android.data.repository.QLearningRepositoryImpl
import com.aitrainingapp.android.data.repository.TrainingHistoryRepositoryImpl
import com.aitrainingapp.android.data.repository.TrainingTypeRepositoryImpl
import com.aitrainingapp.domain.repository.UserLocalRepository
import com.aitrainingapp.android.data.repository.UserLocalRepositoryImpl
import com.aitrainingapp.android.domain.usecase.FetchAndStoreUserUseCase
import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.data.repository.UserRepositoryImpl
import com.aitrainingapp.database.DatabaseDriverFactory
import com.aitrainingapp.database.DatabaseHelper
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.ProfileRepository
import com.aitrainingapp.domain.repository.ProgressionRepository
import com.aitrainingapp.domain.repository.QLearningRepository
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase

class AppModule(context: Context) {

    private val dbHelper = DatabaseHelper(DatabaseDriverFactory(context)).apply {
        seedDefaultData()
    }

    fun provideUserQueries() = dbHelper.userQueries
    fun provideProfileQueries() = dbHelper.profileQueries

    private val api = ApiConnection()
    private val userRepository: UserRepository = UserRepositoryImpl(api, provideProfileQueries())
    private val localUserRepository: UserLocalRepository = UserLocalRepositoryImpl(provideUserQueries(), provideProfileQueries())
    private val trainingTypeRepository: TrainingTypeRepository = TrainingTypeRepositoryImpl(api)
    private val trainingHistoryRepository: TrainingHistoryRepository = TrainingHistoryRepositoryImpl(api)
    private val progressionRepository: ProgressionRepository = ProgressionRepositoryImpl(api)
    private val exerciseRepository: ExerciseRepository = ExerciseRepositoryImpl(api, localUserRepository)
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl(dbHelper.profileQueries)
    private val qLearningRepository: QLearningRepository = QLearningRepositoryImpl(api)

    fun provideLoginUseCase() = LoginUseCase(userRepository)

    fun provideRegisterUseCase() = RegisterUseCase(userRepository)

    fun provideFetchAndStoreUserUseCase() = FetchAndStoreUserUseCase(
        userRepository,
        localUserRepository
    )

    fun provideTrainingTypeRepository() = trainingTypeRepository

    fun provideTrainingHistoryRepository() = trainingHistoryRepository

    fun provideLocalUserRepository() = localUserRepository

    fun provideProgressionRepository() = progressionRepository

    fun provideExerciseRepository() = exerciseRepository

    fun provideQLearningRepository() = qLearningRepository
}
