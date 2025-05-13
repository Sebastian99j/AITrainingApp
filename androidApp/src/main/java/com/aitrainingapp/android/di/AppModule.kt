package com.aitrainingapp.android.di

import com.aitrainingapp.data.remote.AuthApi
import com.aitrainingapp.data.repository.UserRepositoryImpl
import com.aitrainingapp.domain.repository.UserRepository
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase

object AppModule {
    private val api = AuthApi()
    private val repo: UserRepository = UserRepositoryImpl(api)

    fun provideLoginUseCase(): LoginUseCase {
        return LoginUseCase(repo)
    }

    fun provideRegisterUseCase(): RegisterUseCase {
        return RegisterUseCase(repo)
    }
}
