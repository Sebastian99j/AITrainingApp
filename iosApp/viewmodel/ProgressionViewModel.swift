import Combine
import shared

class ProgressionViewModel: ObservableObject {
    let controller: ProgressionController
    private var jobs: [Kotlinx_coroutines_coreJob] = []

    @Published var regressionData: [(String, Double)] = []
    @Published var forecastMap: [String: Double] = [:]
    @Published var exercises: [String] = []

    init(repo: ProgressionRepositoryIOS, userRepo: UserLocalRepository, typeRepo: TrainingTypeRepository) {
        controller = ProgressionController(
            repository: repo,
            userRepo: userRepo,
            trainingTypeRepo: typeRepo,
            scope: IOSScope.shared.scope
        )

        jobs.append(controller.observeRegressionData { [weak self] value in
            guard let list = value as? [(String, Double)] else { return }
            DispatchQueue.main.async {
                self?.regressionData = list
            }
        })

        jobs.append(controller.observeForecastMap { [weak self] value in
            guard let map = value as? [String: Double] else { return }
            DispatchQueue.main.async {
                self?.forecastMap = map
            }
        })

        jobs.append(controller.observeExercises { [weak self] value in
            guard let list = value as? [String] else { return }
            DispatchQueue.main.async {
                self?.exercises = list
            }
        })
    }

    func analyze(type: String) {
        controller.analyzeProgression(type: type)
    }
}
