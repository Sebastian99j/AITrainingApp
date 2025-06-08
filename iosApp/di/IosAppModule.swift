import shared

class IosAppModule {

    let shared: SharedModule

    init() {
        // Utwórz multiplatformową bazę danych
        let databaseHelper = DatabaseHelperIOS(factory: DatabaseDriverFactory())

        // Utwórz dostęp do API
        let api = ApiConnection()

        // Repozytoria (implementacje z KMP)
        let userRepo = UserRepositoryImpl(api: api, queries: <#any ProfileQueries#>)
        let localRepo = UserLocalRepositoryImpl(
            userQueries: databaseHelper.userQueries,
            profileQueries: databaseHelper.profileQueries
        )
        let trainingTypeRepo = TrainingTypeRepositoryImpl(api: api)
        let historyRepo = TrainingHistoryRepositoryImpl(api: api)
        let progressionRepo = ProgressionRepositoryImpl(api: api)
        let exerciseRepo = ExerciseRepositoryImpl(api: api, userRepo: localRepo)
        let profileRepo = ProfileRepositoryImpl(profileQueries: databaseHelper.profileQueries)
        let qLearningRepo = QLearningRepositoryImpl(api: api)

        // Wstrzyknij do SharedModule
        self.shared = SharedModule(
            userRepository: userRepo,
            localUserRepository: localRepo,
            trainingTypeRepository: trainingTypeRepo,
            trainingHistoryRepository: historyRepo,
            progressionRepository: progressionRepo,
            exerciseRepository: exerciseRepo,
            profileRepository: profileRepo,
            qLearningRepository: qLearningRepo
        )
    }
}
