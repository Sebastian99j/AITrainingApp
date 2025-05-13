package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase
import com.aitrainingapp.presentation.login.LoginEvent
import com.aitrainingapp.presentation.login.LoginState
import com.aitrainingapp.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.StateFlow

class AndroidLoginViewModel(
    loginUseCase: LoginUseCase,
    registerUseCase: RegisterUseCase
) : ViewModel() {

    private val delegate = LoginViewModel(
        loginUseCase = loginUseCase,
        registerUseCase = registerUseCase,
        coroutineScope = viewModelScope
    )

    val state: StateFlow<LoginState> = delegate.state

    fun onEvent(event: LoginEvent) {
        delegate.onEvent(event)
    }
}

