package com.aitrainingapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitrainingapp.android.domain.usecase.FetchAndStoreUserUseCase
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase
import com.aitrainingapp.presentation.login.LoginEvent
import com.aitrainingapp.presentation.login.LoginState
import com.aitrainingapp.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AndroidLoginViewModel(
    loginUseCase: LoginUseCase,
    registerUseCase: RegisterUseCase,
    fetchAndStoreUserUseCase: FetchAndStoreUserUseCase
) : ViewModel() {

    private val delegate = LoginViewModel(
        loginUseCase = loginUseCase,
        registerUseCase = registerUseCase,
        coroutineScope = viewModelScope,
        onUserRegistered = { user ->
            viewModelScope.launch {
                fetchAndStoreUserUseCase(user.username)
            }
        }
    )

    val state: StateFlow<LoginState> = delegate.state

    fun onEvent(event: LoginEvent) {
        delegate.onEvent(event)
    }
}

