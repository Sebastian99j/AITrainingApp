import Combine
import shared

class TrainingTypeViewModel: ObservableObject {
    let controller: TrainingTypeController
    private var cancellables = Set<AnyCancellable>()

    @Published var types: [TrainingType] = []

    init(repository: TrainingTypeRepository,
         coroutineScope: Kotlinx_coroutines_coreCoroutineScope) {
        controller = TrainingTypeController(
            repository: repository,
            scope: coroutineScope
        )

        FlowWatcher.shared.watch(flow: controller.types()) { [weak self] value in
            self?.types = value as? [TrainingType] ?? []
        }
    }

    func load() {
        controller.loadTypes()
    }

    func add(name: String, onSuccess: @escaping () -> Void) {
        controller.addTrainingType(name: name, onSuccess: onSuccess)
    }
}
