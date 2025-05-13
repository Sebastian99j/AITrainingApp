package com.aitrainingapp.presentation.login

sealed class LoginEvent {
    data class Login(val username: String, val password: String) : LoginEvent()
    data class Register(val username: String, val password: String) : LoginEvent()
}
