import Combine
import shared

final class SharedProgressionViewModel: ObservableObject {

    // MARK: – Repositories
    private let repository: ProgressionRepositoryIOS
    private let userRepo: UserLocalRepository
    private let trainingTypeRepo: TrainingTypeRepository

    // MARK: – Published state
    @Published var regressionData: [RegressionPoint] = []
    @Published var forecastMap:  [String: Double] = [:]
    @Published var exercises:    [String] = []

    // MARK: – Init
    init(
        repository: ProgressionRepositoryIOS,
        userRepo: UserLocalRepository,
        trainingTypeRepo: TrainingTypeRepository
    ) {
        self.repository        = repository
        self.userRepo          = userRepo
        self.trainingTypeRepo  = trainingTypeRepo
    }

    // MARK: – Public API
    func loadData() {
        Task {
            let types = try? await trainingTypeRepo.getAll()
            await MainActor.run {
                self.exercises = types?.map(\.name) ?? []
            }
        }
    }

    func analyzeProgression(type: String) {
        Task {
            guard
                let uid          = try? await userRepo.getUserId(),
                let rawResult    = try? await repository.runAnalysis(userId: Int32(uid), type: type),
                let points       = rawResult as? [RegressionPoint]
            else { return }

            await MainActor.run {
                self.regressionData = points

                let today      = Date()
                let formatter  = DateFormatter()
                formatter.dateFormat = "yyyy-MM-dd"
                let forecastDays = [30, 60, 90, 120]

                let forecast: [String: Double] = Dictionary(
                    uniqueKeysWithValues: forecastDays.compactMap { days in
                        guard
                            let futureDate = Calendar.current.date(byAdding: .day,
                                                                   value: days,
                                                                   to: today)
                        else { return ("", 0.0) }

                        let key   = formatter.string(from: futureDate)
                        let label = "Za \(days) dni"
                        let value = points.first { $0.date == key}?.value ?? 0.0

                        return (label, value) as! (String, Double)
                    }
                )

                // 4. Publikujemy słownik prognoz
                self.forecastMap = forecast
            }
        }
    }
}
