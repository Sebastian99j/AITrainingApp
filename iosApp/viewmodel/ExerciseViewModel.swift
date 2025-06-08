import Combine
import shared

class ExerciseViewModel: ObservableObject {
    let controller: ExerciseController
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
        qLearningRepo: QLearningRepository,
        coroutineScope: Kotlinx_coroutines_coreCoroutineScope
    ) {
        controller = ExerciseController(
            repository: repository,
            userRepo: userRepo,
            typeRepo: typeRepo,
            historyRepo: historyRepo,
            qLearningRepo: qLearningRepo,
            coroutineScope: coroutineScope
        )

        controller.seriesList.watch { [weak self] (items: Any?) in
            self?.series = items as? [ExerciseSeries] ?? []
        }.store(in: &cancellables)

        controller.exercises.watch { [weak self] (list: Any?) in
            self?.exercises = list as? [String] ?? []
        }.store(in: &cancellables)

        controller.recommendation.watch { [weak self] (value: Any?) in
            self?.recommendation = value as? String
        }.store(in: &cancellables)

        controller.feedbackSent.watch { [weak self] (value: Any?) in
            self?.feedbackSent = value as? Bool ?? false
        }.store(in: &cancellables)
    }

    // Computed Swift-friendly properties
    var nextTrainingPlan: String? {
        controller.nextTrainingPlan.value as? String
    }

    var elapsedSeconds: Int32 {
        (controller.elapsedSeconds.value as? KotlinInt)?.int32Value ?? 0
    }

    var timerRunning: Bool {
        (controller.timerRunning.value as? Bool) ?? false
    }

    // Public API methods
    func toggleTimer() {
        controller.toggleTimer()
    }

    func setSelectedExercise(name: String) {
        controller.setSelectedExercise(name: name)
    }

    func addSeries(exercise: String, weight: Float, reps: Int, sets: Int, rpe: Int) {
        controller.addSeries(
            exercise: exercise,
            weight: weight,
            reps: reps,
            sets: sets,
            rpe: rpe,
            duration: elapsedSeconds
        )
    }

    func totalWeight() -> Int32 {
        controller.totalWeight()
    }

    func totalReps() -> Int32 {
        controller.totalReps()
    }

    func totalSets() -> Int32 {
        controller.totalSets()
    }

    func averageDuration() -> Int32 {
        controller.averageDuration()
    }

    func fetchRecommendation() {
        controller.fetchRecommendation()
    }

    func sendFeedback(successful: Bool) {
        controller.sendFeedback(successful: successful)
    }

    func saveAll() {
        controller.saveAll()
    }

    func loadData() {
        controller.loadExercises()
    }
}
