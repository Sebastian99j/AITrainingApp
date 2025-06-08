class ProgressionViewModel: ObservableObject {
    let controller: SharedProgressionController
    private var cancellables = Set<AnyCancellable>()

    @Published var regressionData: [SharedPair] = []
    @Published var forecastMap: [String: Double] = [:]
    @Published var exercises: [String] = []

    init(repo: ProgressionRepository, userRepo: UserLocalRepository, typeRepo: TrainingTypeRepository) {
        controller = SharedProgressionController(
            repository: repo,
            userRepo: userRepo,
            trainingTypeRepo: typeRepo,
            scope: MainScope()
        )

        controller.regressionData.watch { [weak self] list in
            self?.regressionData = list as? [SharedPair] ?? []
        }.store(in: &cancellables)

        controller.forecastMap.watch { [weak self] map in
            self?.forecastMap = map as? [String: Double] ?? [:]
        }.store(in: &cancellables)

        controller.exercises.watch { [weak self] list in
            self?.exercises = list as? [String] ?? []
        }.store(in: &cancellables)
    }

    func analyze(type: String) {
        controller.analyzeProgression(type: type)
    }
}
