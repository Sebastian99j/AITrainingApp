import SwiftUI
import shared
import iosApp

enum AppScreen {
    case login, dashboard, trainingTypes, trainingHistory, profile, registerExercise, analysis, settings
}

struct ContentView: View {
    @State private var currentScreen: AppScreen = .login

    var loginViewModel: LoginViewModelWrapper? = nil
    var trainingTypeVM: TrainingTypeViewModel? = nil
    var trainingHistoryVM: TrainingHistoryViewModel? = nil
    var trainingHistoryVMWr: TrainingHistoryViewModelWrapper? = nil
    var progressionVM: ProgressionViewModel? = nil
    var progressionVMWr: ProgressionViewModelWrapper? = nil
    var progressionVMsh: SharedProgressionViewModel? = nil
    var profileVM: ProfileViewModel? = nil
    var profileVMWr: ProfileViewModelWrapper? = nil
    var exerciseVM: ExerciseViewModel? = nil
    var exerciseVMWr: ExerciseViewModelWrapper? = nil
    var settingsVM: UserSettingsViewModel? = nil
    var settingsVMWr: UserSettingsViewModelWrapper? = nil
    var trainingTypeVMSh: SharedTrainingTypeViewModel? = nil

    init() {
        // KMP: Baza danych
        let dbHelper = DatabaseHelper(factory: DatabaseDriverFactory())

        // KMP: API & Repositories
        if let api = ApiConnection.companion.create() {
            // używaj api
            let userRepo = UserRepositoryImpl(
                api: api,
                queries: dbHelper.profileQueries
            )
            let trainingHistoryRepo = TrainingHistoryRepositoryImpl(api: api)
            let localRepo = UserLocalRepositoryImpl(
                userQueries: dbHelper.userQueries,
                profileQueries: dbHelper.profileQueries
            )

            self.trainingHistoryVM = TrainingHistoryViewModel(
                repository: trainingHistoryRepo,
                userRepository: localRepo
            )
            let trainingTypeRepo = TrainingTypeRepositoryImpl(api: api)
            let qLearningRepo = QLearningRepositoryImpl(api: api)
            let exerciseRepo = ExerciseRepositoryImpl(api: api, userRepo: localRepo)

            self.profileVM = ProfileViewModel(
                profileQueries: dbHelper.profileQueries,
                userQueries: dbHelper.userQueries
            )
            
            self.profileVMWr = ProfileViewModelWrapper(
                profileQueries: dbHelper.profileQueries,
                userQueries: dbHelper.userQueries
            )

            self.exerciseVM = ExerciseViewModel(
                repository: exerciseRepo,
                userRepo: localRepo,
                typeRepo: trainingTypeRepo,
                historyRepo: trainingHistoryRepo,
                qLearningRepo: qLearningRepo,
                coroutineScope: IOSScope.shared.scope
            )
            
            self.exerciseVMWr = ExerciseViewModelWrapper(
                repository: exerciseRepo,
                localRepo: localRepo,
                typeRepo: trainingTypeRepo,
                historyRepo: trainingHistoryRepo,
                qLearningRepo: qLearningRepo
            )
            
            let loginUseCase = LoginUseCase(repo: userRepo)
            let registerUseCase = RegisterUseCase(repo: userRepo)
            let fetchUserUseCase = FetchAndStoreUserUseCase(
                remoteRepo: userRepo,
                localRepo: localRepo
            )

            self.loginViewModel = LoginViewModelWrapper(
                loginUseCase: loginUseCase,
                registerUseCase: registerUseCase,
                fetchAndStoreUser: fetchUserUseCase
            )
            
            let progressionRepo = ProgressionRepositoryIOSImpl(api: api)

            self.progressionVM = ProgressionViewModel(
                repo: progressionRepo,
                userRepo: localRepo,
                typeRepo: trainingTypeRepo
            )
            
            self.progressionVMWr = ProgressionViewModelWrapper(
                repository: progressionRepo,
                localUser: localRepo,
                trainingRepo: trainingTypeRepo
            )
            
            self.trainingTypeVM = TrainingTypeViewModel(
                repository: trainingTypeRepo,
                coroutineScope: IOSScope.shared.scope
            )
            
            self.trainingHistoryVMWr = TrainingHistoryViewModelWrapper(
                repository: trainingHistoryRepo,
                userRepo: localRepo
            )
            
            self.progressionVMsh = SharedProgressionViewModel(repository: progressionRepo, userRepo: localRepo, trainingTypeRepo: trainingTypeRepo)

            self.settingsVM = UserSettingsViewModel(userQueries: dbHelper.userQueries)
            
            self.settingsVMWr = UserSettingsViewModelWrapper(userQueries: dbHelper.userQueries)
            
            self.trainingTypeVMSh = SharedTrainingTypeViewModel(repository: trainingTypeRepo)
        } else {
            print("❌ Nie udało się utworzyć ApiConnection")
        }
    }

    var body: some View {
        NavigationStack {
            switch currentScreen {
            case .login:
                LoginScreen(
                    viewModel: loginViewModel!,
                    onLoginSuccess: { currentScreen = .dashboard }
                )
            case .dashboard:
                DashboardScreen(dashboardItems: [
                    DashboardItem(label: "Rodzaje treningów", systemIcon: "bolt.heart", action: {
                        currentScreen = .trainingTypes
                    }),
                    DashboardItem(label: "Historia treningów", systemIcon: "clock.arrow.circlepath", action: {
                        currentScreen = .trainingHistory
                    }),
                    DashboardItem(label: "Profil", systemIcon: "person.crop.circle", action: {
                        currentScreen = .profile
                    }),
                    DashboardItem(label: "Progresja", systemIcon: "chart.line.uptrend.xyaxis", action: {
                        currentScreen = .analysis
                    }),
                    DashboardItem(label: "Zarejestruj ćwiczenie", systemIcon: "plus.circle", action: {
                        currentScreen = .registerExercise
                    }),
                    DashboardItem(label: "Ustawienia", systemIcon: "gearshape", action: {
                        currentScreen = .settings
                    })
                ])
            case .trainingTypes:
                TrainingTypeScreen(viewModel: trainingTypeVMSh!)
            case .trainingHistory:
                TrainingHistoryScreen(viewModel: trainingHistoryVMWr!)
            case .profile:
                ProfileScreen(viewModel: profileVMWr!)
            case .registerExercise:
                RegisterExerciseScreen(viewModel: exerciseVMWr!)
            case .analysis:
                ProgressionAnalysisScreen(viewModel: progressionVMsh!)
            case .settings:
                SettingsScreen(
                    viewModel: settingsVMWr!
                )
            }
        }
    }
}

