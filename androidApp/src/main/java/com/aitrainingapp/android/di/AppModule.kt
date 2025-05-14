package com.aitrainingapp.android.di

import android.content.Context
import androidx.room.Room
import com.aitrainingapp.android.data.repository.TrainingTypeRepositoryImpl
import com.aitrainingapp.domain.repository.UserLocalRepository
import com.aitrainingapp.android.data.repository.UserLocalRepositoryImpl
import com.aitrainingapp.android.domain.usecase.FetchAndStoreUserUseCase
import com.aitrainingapp.android.room.database.AppDatabase
import com.aitrainingapp.data.remote.AuthApi
import com.aitrainingapp.data.repository.UserRepositoryImpl
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase

class AppModule(context: Context) {

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "ai_training_db"
    ).build()

    private val api = AuthApi()
    private val userRepository: UserRepository = UserRepositoryImpl(api)
    private val localUserRepository: UserLocalRepository = UserLocalRepositoryImpl(provideUserDao())
    private val trainingTypeRepository: TrainingTypeRepository = TrainingTypeRepositoryImpl(api)

    fun provideLoginUseCase() = LoginUseCase(userRepository)

    fun provideRegisterUseCase() = RegisterUseCase(userRepository)

    fun provideFetchAndStoreUserUseCase() = FetchAndStoreUserUseCase(
        userRepository,
        localUserRepository
    )

    fun provideUserDao() = db.userDao()

    fun provideProfileDao() = db.profileDao()

    fun provideTrainingTypeRepository() = trainingTypeRepository;
}
