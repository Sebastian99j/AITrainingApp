import SwiftUI
import shared
import iosApp

enum AppScreen {
    case login
    case dashboard
    case trainingTypes
    case trainingHistory
    case profile
    case registerExercise
    case analysis
    case settings
}

struct ContentView: View {
    @State private var currentScreen: AppScreen = .login

    let loginViewModel = SwiftLoginViewModel()
    let trainingTypeVM = SwiftTrainingTypeViewModel()
    let trainingHistoryVM = SwiftTrainingHistoryViewModel()
    let profileVM = SwiftProfileViewModel()
    let exerciseVM = SwiftExerciseViewModel()
    let progressionVM = SwiftProgressionViewModel()
    let settingsVM = SwiftUserSettingsViewModel()

    var body: some View {
        NavigationStack {
            switch currentScreen {
            case .login:
                LoginScreen(
                    viewModel: loginViewModel,
                    onLoginSuccess: {
                        currentScreen = .dashboard
                    }
                )

            case .dashboard:
                DashboardScreen(
                    onNavigateToTrainingTypes: { currentScreen = .trainingTypes },
                    onNavigateToProfile: { currentScreen = .profile },
                    onNavigateToSettings: { currentScreen = .settings },
                    onNavigateToHistory: { currentScreen = .trainingHistory },
                    onNavigateToProgressionAnalysis: { currentScreen = .analysis },
                    onNavigateToRegisterExercise: { currentScreen = .registerExercise }
                )

            case .trainingTypes:
                TrainingTypeScreen(viewModel: trainingTypeVM)

            case .trainingHistory:
                TrainingHistoryScreen(viewModel: trainingHistoryVM)

            case .profile:
                ProfileScreen(viewModel: profileVM)

            case .registerExercise:
                RegisterExerciseScreen(viewModel: exerciseVM)

            case .analysis:
                ProgressionAnalysisScreen(viewModel: progressionVM)

            case .settings:
                SettingsScreen(
                    currentUsername: settingsVM.username,
                    onUsernameChange: { newName in
                        settingsVM.updateUsername(newName: newName)
                    },
                    viewModel: settingsVM
                )
            }
        }
    }
}
