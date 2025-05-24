import Shared
import Combine

class TrainingHistoryViewModelWrapper: ObservableObject {
    private let viewModel: TrainingHistoryViewModel

    @Published var history: [TrainingSeries] = []

    init(repository: TrainingHistoryRepository, userRepo: UserLocalRepository) {
        self.viewModel = TrainingHistoryViewModel(repository: repository, userRepository: userRepo)
        observe()
    }

    private func observe() {
        viewModel.history.watch { [weak self] items in
            guard let list = items as? [TrainingSeries] else { return }
            DispatchQueue.main.async {
                self?.history = list
            }
        }
    }

    func load() {
        viewModel.loadHistory()
    }
}
