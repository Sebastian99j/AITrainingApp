import Combine
import shared

class TrainingHistoryViewModelWrapper: ObservableObject {
    private let viewModel: TrainingHistoryViewModel

    @Published var history: [TrainingSeries] = []

    init(repository: TrainingHistoryRepository, userRepo: UserLocalRepository) {
        self.viewModel = TrainingHistoryViewModel(
            repository: repository,
            userRepository: userRepo
        )

        observe()
    }

    private func observe() {
        viewModel.$history
            .receive(on: DispatchQueue.main)
            .assign(to: &$history)
    }

    func load() {
        viewModel.load()
    }
}
