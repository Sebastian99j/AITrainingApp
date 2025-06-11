import shared
import Foundation

class ProgressionViewModelWrapper: ObservableObject {
    private let viewModel: ProgressionViewModel

    @Published var exercises: [String] = []
    @Published var regressionData: [(String, Double)] = []
    @Published var forecast: [String: Double] = [:]

    init(repository: ProgressionRepositoryIOS, localUser: UserLocalRepository, trainingRepo: TrainingTypeRepository) {
        viewModel = ProgressionViewModel(
            repo: repository,
            userRepo: localUser,
            typeRepo: trainingRepo
        )
        observeFlows()
    }

    private func observeFlows() {
        viewModel.$exercises.assign(to: &$exercises)
        viewModel.$regressionData.assign(to: &$regressionData)
        viewModel.$forecastMap.assign(to: &$forecast)
    }

    func loadData() {
        // if needed
    }

    func analyzeProgression(type: String) {
        viewModel.analyze(type: type)
    }
}
