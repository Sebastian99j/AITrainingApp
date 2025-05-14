package com.aitrainingapp.presentation.login

import com.aitrainingapp.domain.model.User
import com.aitrainingapp.domain.usecase.LoginUseCase
import com.aitrainingapp.domain.usecase.RegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.aitrainingapp.util.Result

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> {
                coroutineScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    when (val result: Result<User> = loginUseCase(event.username, event.password)) {
                        is Result.Success -> {
                            _state.value = LoginState(success = true)
                        }
                        is Result.Failure -> {
                            _state.value = LoginState(error = result.exception.message)
                        }
                    }

                }
            }
            is LoginEvent.Register -> {
                coroutineScope.launch {
                    _state.value = _state.value.copy(isLoading = true)

                    when (val result: Result<User> = registerUseCase(event.username, event.password)) {
                        is Result.Success -> {
                            _state.value = LoginState(success = true)
                        }
                        is Result.Failure -> {
                            _state.value = LoginState(error = result.exception.message)
                        }
                    }
                }
            }
        }
    }
}
