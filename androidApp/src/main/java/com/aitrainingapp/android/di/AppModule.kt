package com.aitrainingapp.android.di

import android.content.Context
import androidx.room.Room
import com.aitrainingapp.android.data.repository.ExerciseRepositoryImpl
import com.aitrainingapp.android.data.repository.ProfileRepositoryImpl
import com.aitrainingapp.android.data.repository.ProgressionRepositoryImpl
import com.aitrainingapp.android.data.repository.QLearningRepositoryImpl
import com.aitrainingapp.android.data.repository.TrainingHistoryRepositoryImpl
import com.aitrainingapp.android.data.repository.TrainingTypeRepositoryImpl
import com.aitrainingapp.domain.repository.UserLocalRepository
import com.aitrainingapp.android.data.repository.UserLocalRepositoryImpl
import com.aitrainingapp.android.domain.usecase.FetchAndStoreUserUseCase
import com.aitrainingapp.android.room.ProfileEntity
import com.aitrainingapp.android.room.database.AppDatabase
import com.aitrainingapp.data.remote.ApiConnection
import com.aitrainingapp.data.repository.UserRepositoryImpl
import com.aitrainingapp.domain.repository.ExerciseRepository
import com.aitrainingapp.domain.repository.ProfileRepository
import com.aitrainingapp.domain.repository.ProgressionRepository
import com.aitrainingapp.domain.repository.QLearningRepository
import com.aitrainingapp.domain.repository.TrainingHistoryRepository
import com.aitrainingapp.domain.repository.TrainingTypeRepository
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppModule(context: Context) {

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "ai_training_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    fun insertDefaultProfilesIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            val profileDao = db.profileDao()
            val existing = profileDao.getAll()
            if (existing.isEmpty()) {
                profileDao.insert(ProfileEntity(name = "Początkujący", weightChance = 2.5f, repsChance = 1, setsChance = 1))
                profileDao.insert(ProfileEntity(name = "Średniozaawansowany", weightChance = 1.5f, repsChance = 1, setsChance = 1))
                profileDao.insert(ProfileEntity(name = "Zaawansowany", weightChance = 0.5f, repsChance = 1, setsChance = 1))
            }
        }
    }

    private val api = ApiConnection()
    private val userRepository: UserRepository = UserRepositoryImpl(api)
    private val localUserRepository: UserLocalRepository = UserLocalRepositoryImpl(provideUserDao(), provideProfileDao())
    private val trainingTypeRepository: TrainingTypeRepository = TrainingTypeRepositoryImpl(api)
    private val trainingHistoryRepository: TrainingHistoryRepository = TrainingHistoryRepositoryImpl(api)
    private val progressionRepository: ProgressionRepository = ProgressionRepositoryImpl(api)
    private val exerciseRepository: ExerciseRepository = ExerciseRepositoryImpl(api, localUserRepository)
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl(provideProfileDao())
    private val qLearningRepository: QLearningRepository = QLearningRepositoryImpl(api)

    fun provideLoginUseCase() = LoginUseCase(userRepository)

    fun provideRegisterUseCase() = RegisterUseCase(userRepository)

    fun provideFetchAndStoreUserUseCase() = FetchAndStoreUserUseCase(
        userRepository,
        localUserRepository
    )

    fun provideUserDao() = db.userDao()

    fun provideProfileDao() = db.profileDao()

    fun provideTrainingTypeRepository() = trainingTypeRepository

    fun provideTrainingHistoryRepository() = trainingHistoryRepository

    fun provideLocalUserRepository() = localUserRepository

    fun provideProgressionRepository() = progressionRepository

    fun provideExerciseRepository() = exerciseRepository

    fun provideProfileRepository() = profileRepository

    fun provideQLearningRepository() = qLearningRepository
}
