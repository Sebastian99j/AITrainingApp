package com.aitrainingapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aitrainingapp.android.di.AppModule
import com.aitrainingapp.android.ui.dashboard.DashboardScreen
import com.aitrainingapp.android.ui.login.LoginScreen
import com.aitrainingapp.android.viewmodel.AndroidLoginViewModel

class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: AndroidLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appModule = AppModule(applicationContext)

        loginViewModel = AndroidLoginViewModel(
            appModule.provideLoginUseCase(),
            appModule.provideRegisterUseCase(),
            appModule.provideProfileDao(),
            appModule.provideUserDao()
        )

        setContent {
            val navController = rememberNavController()
            val loginSuccess = remember { mutableStateOf(false) }

            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { navController.navigate("dashboard") }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen()
                        }
                    }
                }
            }
        }
    }
}

