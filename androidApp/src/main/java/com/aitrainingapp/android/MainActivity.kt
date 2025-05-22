package com.aitrainingapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aitrainingapp.android.di.AppModule
import com.aitrainingapp.android.ui.analysis.ProgressionAnalysisScreen
import com.aitrainingapp.android.ui.dashboard.DashboardScreen
import com.aitrainingapp.android.ui.exercise.RegisterExerciseScreen
import com.aitrainingapp.android.ui.login.LoginScreen
import com.aitrainingapp.android.ui.profile.ProfileScreen
import com.aitrainingapp.android.ui.settings.SettingsScreen
import com.aitrainingapp.android.ui.training_history.TrainingHistoryScreen
import com.aitrainingapp.android.ui.training_types.TrainingTypeScreen
import com.aitrainingapp.android.viewmodel.AndroidLoginViewModel
import com.aitrainingapp.android.viewmodel.ExerciseViewModel
import com.aitrainingapp.android.viewmodel.ProfileViewModel
import com.aitrainingapp.android.viewmodel.ProgressionViewModel
import com.aitrainingapp.android.viewmodel.TrainingHistoryViewModel
import com.aitrainingapp.android.viewmodel.TrainingTypeViewModel
import com.aitrainingapp.android.viewmodel.UserSettingsViewModel

class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: AndroidLoginViewModel
    private lateinit var trainingTypeViewModel: TrainingTypeViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var settingsViewModel: UserSettingsViewModel
    private lateinit var trainingHistoryViewModel: TrainingHistoryViewModel
    private lateinit var progressionViewModel: ProgressionViewModel
    private lateinit var exerciseViewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appModule = AppModule(applicationContext)
        appModule.insertDefaultProfilesIfNeeded()

        loginViewModel = AndroidLoginViewModel(
            appModule.provideLoginUseCase(),
            appModule.provideRegisterUseCase(),
            appModule.provideFetchAndStoreUserUseCase()
        )

        trainingTypeViewModel = TrainingTypeViewModel(
            appModule.provideTrainingTypeRepository()
        )

        profileViewModel = ProfileViewModel(
            appModule.provideProfileDao(),
            appModule.provideUserDao()
        )

        settingsViewModel = UserSettingsViewModel(
            appModule.provideUserDao()
        )

        trainingHistoryViewModel = TrainingHistoryViewModel(
            appModule.provideTrainingHistoryRepository(),
            appModule.provideLocalUserRepository()
        )

        progressionViewModel = ProgressionViewModel(
            appModule.provideProgressionRepository(),
            appModule.provideLocalUserRepository(),
            appModule.provideTrainingTypeRepository()
        )

        exerciseViewModel = ExerciseViewModel(
            appModule.provideExerciseRepository(),
            appModule.provideLocalUserRepository(),
            appModule.provideTrainingTypeRepository(),
            appModule.provideTrainingHistoryRepository(),
            appModule.provideQLearningRepository()
        )

        setContent {
            val navController = rememberNavController()
            val isDarkMode = settingsViewModel.darkMode.collectAsState().value

            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { navController.navigate("dashboard") }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToTrainingTypes = { navController.navigate("trainingTypes") },
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToHistory = { navController.navigate("trainingHistory") },
                                onNavigateToProgressionAnalysis = { navController.navigate("progression") },
                                onNavigateToRegisterExercise = { navController.navigate("registerExercise") }
                            )
                        }
                        composable("trainingTypes") {
                            TrainingTypeScreen(viewModel = trainingTypeViewModel)
                        }
                        composable("profile") {
                            ProfileScreen(viewModel = profileViewModel)
                        }
                        composable("settings") {
                            SettingsScreen(
                                currentUsername = settingsViewModel.username.collectAsState().value,
                                onUsernameChange = { newName -> settingsViewModel.updateUsername(newName) },
                                viewModel = settingsViewModel
                            )
                        }
                        composable("trainingHistory") {
                            TrainingHistoryScreen(viewModel = trainingHistoryViewModel)
                        }
                        composable("progression") {
                            ProgressionAnalysisScreen(viewModel = progressionViewModel)
                        }
                        composable("registerExercise") {
                            RegisterExerciseScreen(viewModel = exerciseViewModel)
                        }
                    }
                }
            }
        }
    }
}

