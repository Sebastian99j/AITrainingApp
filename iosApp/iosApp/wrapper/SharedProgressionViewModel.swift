import Shared
import Combine

class SharedProgressionViewModel: ObservableObject {
    private let repository: ProgressionRepository
    private let userRepo: UserLocalRepository
    private let trainingTypeRepo: TrainingTypeRepository

    @Published var regressionData: [Pair<String, Double>] = []
    @Published var forecastMap: [String: Double] = [:]
    @Published var exercises: [String] = []

    init(
        repository: ProgressionRepository,
        userRepo: UserLocalRepository,
        trainingTypeRepo: TrainingTypeRepository
    ) {
        self.repository = repository
        self.userRepo = userRepo
        self.trainingTypeRepo = trainingTypeRepo
    }

    func loadData() {
        Task {
            let types = try? await trainingTypeRepo.getAll()
            await MainActor.run {
                self.exercises = types?.map { $0.name } ?? []
            }
        }
    }

    func analyzeProgression(type: String) {
        Task {
            let userId = try? await userRepo.getUserId()
            guard let uid = userId else { return }

            let result = try? await repository.runAnalysis(userId: Int32(uid), type: type)
            guard let result = result else { return }

            await MainActor.run {
                regressionData = result

                let today = Date()
                let formatter = DateFormatter()
                formatter.dateFormat = "yyyy-MM-dd"

                let forecastDays = [30, 60, 90, 120]
                var forecast: [String: Double] = [:]

                for days in forecastDays {
                    if let future = Calendar.current.date(byAdding: .day, value: days, to: today) {
                        let label = "Za \(days) dni"
                        let key = formatter.string(from: future)
                        let val = result.first(where: { $0.first == key })?.second ?? 0.0
                        forecast[label] = val
                    }
                }

                forecastMap = forecast
            }
        }
    }
}
