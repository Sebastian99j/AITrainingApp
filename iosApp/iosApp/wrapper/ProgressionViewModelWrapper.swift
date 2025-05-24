import Shared

class ProgressionViewModelWrapper: ObservableObject {
    private let viewModel: ProgressionViewModel

    @Published var exercises: [String] = []
    @Published var regressionData: [(String, Double)] = []
    @Published var forecast: [String: Double] = [:]

    init(repository: ProgressionRepository, localUser: UserLocalRepository, trainingRepo: TrainingTypeRepository) {
        viewModel = ProgressionViewModel(
            repository: repository,
            localUserRepository: localUser,
            trainingTypeRepository: trainingRepo
        )
        observeFlows()
    }

    private func observeFlows() {
        viewModel.exercises.watch { [weak self] newList in
            self?.exercises = newList as? [String] ?? []
        }
        viewModel.regressionData.watch { [weak self] data in
            self?.regressionData = data as? [(String, Double)] ?? []
        }
        viewModel.forecastMap.watch { [weak self] map in
            self?.forecast = map as? [String: Double] ?? [:]
        }
    }

    func loadData() {
        viewModel.loadData()
    }

    func analyzeProgression(type: String) {
        viewModel.analyzeProgression(type: type)
    }
}
