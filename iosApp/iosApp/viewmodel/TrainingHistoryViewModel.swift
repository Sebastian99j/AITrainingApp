import Shared
import Combine

class TrainingHistoryViewModel: ObservableObject {
    let controller: SharedTrainingHistoryController
    private var cancellables = Set<AnyCancellable>()

    @Published var history: [TrainingSeries] = []

    init(repository: TrainingHistoryRepository, userRepository: UserLocalRepository) {
        controller = SharedTrainingHistoryController(
            repository: repository,
            userRepository: userRepository,
            scope: MainScope()
        )

        controller.history.watch { [weak self] seriesList in
            self?.history = seriesList as? [TrainingSeries] ?? []
        }.store(in: &cancellables)
    }

    func load() {
        controller.loadHistory()
    }
}
