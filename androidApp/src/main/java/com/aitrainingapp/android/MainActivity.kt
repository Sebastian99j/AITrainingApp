package com.aitrainingapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.aitrainingapp.android.di.AppModule
import com.aitrainingapp.android.ui.login.LoginScreen
import com.aitrainingapp.android.viewmodel.AndroidLoginViewModel

class MainActivity : ComponentActivity() {
    private val loginViewModel by lazy {
        AndroidLoginViewModel(AppModule.provideLoginUseCase(), AppModule.provideRegisterUseCase())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(viewModel = loginViewModel)
                }
            }
        }
    }
}
