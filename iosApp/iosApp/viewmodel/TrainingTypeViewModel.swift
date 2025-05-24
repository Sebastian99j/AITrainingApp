import Shared
import Combine

class TrainingTypeViewModel: ObservableObject {
    let controller: SharedTrainingTypeController
    private var cancellables = Set<AnyCancellable>()

    @Published var types: [TrainingType] = []

    init(repository: TrainingTypeRepository) {
        controller = SharedTrainingTypeController(
            repository: repository,
            scope: MainScope()
        )

        controller.types.watch { [weak self] list in
            self?.types = list as? [TrainingType] ?? []
        }.store(in: &cancellables)
    }

    func load() {
        controller.loadTypes()
    }

    func add(name: String, onSuccess: @escaping () -> Void) {
        controller.addTrainingType(name: name, onSuccess: onSuccess)
    }
}
