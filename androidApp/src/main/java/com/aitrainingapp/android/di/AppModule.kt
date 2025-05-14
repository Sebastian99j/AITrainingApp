package com.aitrainingapp.android.di

import android.content.Context
import androidx.room.Room
import com.aitrainingapp.android.room.database.AppDatabase
import com.aitrainingapp.data.remote.AuthApi
import com.aitrainingapp.data.repository.UserRepositoryImpl
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
    private val repo: UserRepository = UserRepositoryImpl(api)

    fun provideLoginUseCase() = LoginUseCase(repo)

    fun provideRegisterUseCase() = RegisterUseCase(repo)

    fun provideUserDao() = db.userDao()

    fun provideProfileDao() = db.profileDao()
}
