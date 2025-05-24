import Shared
import Combine

class ExerciseViewModel: ObservableObject {
    let controller: SharedExerciseController
    private var cancellables = Set<AnyCancellable>()

    @Published var series: [ExerciseSeries] = []
    @Published var exercises: [String] = []
    @Published var recommendation: String? = nil
    @Published var feedbackSent: Bool = false

    init(
        repository: ExerciseRepository,
        userRepo: UserLocalRepository,
        typeRepo: TrainingTypeRepository,
        historyRepo: TrainingHistoryRepository,
        qLearningRepo: QLearningRepository
    ) {
        controller = SharedExerciseController(
            repository: repository,
            userRepo: userRepo,
            typeRepo: typeRepo,
            historyRepo: historyRepo,
            qLearningRepo: qLearningRepo,
            coroutineScope: MainScope()
        )

        controller.seriesList.watch { [weak self] items in
            self?.series = items as? [ExerciseSeries] ?? []
        }.store(in: &cancellables)

        controller.exercises.watch { [weak self] list in
            self?.exercises = list as? [String] ?? []
        }.store(in: &cancellables)

        controller.recommendation.watch { [weak self] value in
            self?.recommendation = value as? String
        }.store(in: &cancellables)

        controller.feedbackSent.watch { [weak self] value in
            self?.feedbackSent = value as? Bool ?? false
        }.store(in: &cancellables)
    }

    // Delegacje do metody KMP:
    func loadData() {
        controller.loadExercises()
    }

    func fetchRecommendation() {
        controller.fetchRecommendation()
    }

    func sendFeedback(successful: Bool) {
        controller.sendFeedback(successful: successful)
    }

    func addSeries(...) {
        controller.addSeries(...)
    }

    func saveAll() {
        controller.saveAll()
    }
}
