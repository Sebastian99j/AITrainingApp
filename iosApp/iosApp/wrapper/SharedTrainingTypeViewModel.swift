import shared
import Foundation

class SharedTrainingTypeViewModel: ObservableObject {
    private let repository: TrainingTypeRepository

    @Published var types: [TrainingType] = []

    init(repository: TrainingTypeRepository) {
        self.repository = repository
    }

    func loadTypes() {
        Task {
            let loaded = try? await repository.getAll()
            DispatchQueue.main.async {
                self.types = loaded ?? []
            }
        }
    }

    func addTrainingType(name: String, onSuccess: @escaping () -> Void) {
        Task {
            let success = try? await repository.add(name: name)
            if success == true {
                await MainActor.run {
                    onSuccess()
                    self.loadTypes()
                }
            }
        }
    }
}
