import shared

class IosAppModule {

    let shared: SharedModule

    init() {
        // Utwórz multiplatformową bazę danych
        let databaseHelper = DatabaseHelperIOS(factory: DatabaseDriverFactory())

        // Utwórz dostęp do API
        var api: ApiConnection? = nil
        if let api = ApiConnection.companion.create() {
            // używaj api
        } else {
            print("❌ Nie udało się utworzyć ApiConnection")
        }

        // Repozytoria (implementacje z KMP)
        let userRepo = UserRepositoryImpl(api: api!, queries: databaseHelper.profileQueries)
        let localRepo = UserLocalRepositoryImpl(
            userQueries: databaseHelper.userQueries,
            profileQueries: databaseHelper.profileQueries
        )
        let trainingTypeRepo = TrainingTypeRepositoryImpl(api: api!)
        let historyRepo = TrainingHistoryRepositoryImpl(api: api!)
        let progressionRepo = ProgressionRepositoryIOSImpl(api: api!)
        let exerciseRepo = ExerciseRepositoryImpl(api: api!, userRepo: localRepo)
        let profileRepo = ProfileRepositoryImpl(queries: databaseHelper.profileQueries)
        let qLearningRepo = QLearningRepositoryImpl(api: api!)

        // Wstrzyknij do SharedModule
        self.shared = SharedModule(
            userRepository: userRepo,
            localUserRepository: localRepo,
            trainingTypeRepository: trainingTypeRepo,
            trainingHistoryRepository: historyRepo,
            progressionRepository: nil,
            progressionRepositoryIOS: progressionRepo,
            exerciseRepository: exerciseRepo,
            profileRepository: profileRepo,
            qLearningRepository: qLearningRepo
        )
    }
}
