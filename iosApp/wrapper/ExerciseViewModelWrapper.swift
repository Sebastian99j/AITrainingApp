import Combine
import shared

class ExerciseViewModelWrapper: ObservableObject {
    private let viewModel: ExerciseViewModel

    @Published var exercises: [String] = []
    @Published var series: [ExerciseSeries] = []
    @Published var recommendation: String? = nil
    @Published var plan: String? = nil
    @Published var feedbackSent: Bool = false
    @Published var elapsedSeconds: Int32 = 0
    @Published var timerRunning: Bool = false

    init(
        repository: ExerciseRepository,
        localRepo: UserLocalRepository,
        typeRepo: TrainingTypeRepository,
        historyRepo: TrainingHistoryRepository,
        qLearningRepo: QLearningRepository
    ) {
        self.viewModel = ExerciseViewModel(
            repository: repository,
            userRepo: localRepo,
            typeRepo: typeRepo,
            historyRepo: historyRepo,
            qLearningRepo: qLearningRepo,
            coroutineScope: IOSScope.shared.scope // <- dodaj to jeśli wymagane
        )
        refresh()
    }

    func refresh() {
        self.series = viewModel.series
        self.exercises = viewModel.exercises
        self.recommendation = viewModel.recommendation
        self.feedbackSent = viewModel.feedbackSent
        self.elapsedSeconds = viewModel.elapsedSeconds
        self.timerRunning = viewModel.timerRunning
    }

    func loadData() {
        viewModel.refreshData()
        refresh()
    }

    func fetchRecommendation() {
        viewModel.fetchRecommendation()
        refresh()
    }

    func toggleTimer() {
        viewModel.toggleTimer()
        refresh()
    }

    func setSelectedExercise(_ name: String) {
        viewModel.setSelectedExercise(name: name)
        refresh()
    }

    func sendFeedback(successful: Bool) {
        viewModel.sendFeedback(successful: successful)
        refresh()
    }

    func addSeries(exercise: String, weight: Float, reps: Int32, sets: Int32, rpe: Int32) {
        viewModel.addSeries(
            exercise: exercise,
            weight: weight,
            reps: reps,
            sets: sets,
            rpe: rpe
        )
        refresh()
    }

    func saveAll() {
        viewModel.saveAll()
        refresh()
    }

    func totalWeight() -> Int32 {
        viewModel.totalWeight()
    }

    func totalReps() -> Int32 {
        viewModel.totalReps()
    }

    func totalSets() -> Int32 {
        viewModel.totalSets()
    }

    func averageDuration() -> Int32 {
        viewModel.averageDuration()
    }
}
