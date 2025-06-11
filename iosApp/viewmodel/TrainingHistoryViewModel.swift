import Combine
import shared

class TrainingHistoryViewModel: ObservableObject {
    let controller: TrainingHistoryController
    private var job: Kotlinx_coroutines_coreJob?

    @Published var history: [TrainingSeries] = []

    init(repository: TrainingHistoryRepository, userRepository: UserLocalRepository) {
        controller = TrainingHistoryController(
            repository: repository,
            userRepository: userRepository,
            scope: IOSScope.shared.scope
        )

        job = controller.observeHistory { [weak self] series in
            guard let series = series as? [TrainingSeries] else { return }
            DispatchQueue.main.async {
                self?.history = series
            }
        }
    }

    func load() {
        controller.loadHistory()
    }
}
