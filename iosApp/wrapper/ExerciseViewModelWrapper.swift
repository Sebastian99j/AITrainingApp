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
        viewModel = ExerciseViewModel(
            repository: repository,
            localUserRepository: localRepo,
            trainingTypeRepository: typeRepo,
            trainingHistoryRepository: historyRepo,
            qLearningRepository: qLearningRepo
        )
        observe()
    }

    private func observe() {
        viewModel.exercises.watch { [weak self] list in
            self?.exercises = list as? [String] ?? []
        }
        viewModel.seriesList.watch { [weak self] list in
            self?.series = list as? [ExerciseSeries] ?? []
        }
        viewModel.recommendation.watch { [weak self] rec in
            self?.recommendation = rec as? String
        }
        viewModel.nextTrainingPlan.watch { [weak self] plan in
            self?.plan = plan as? String
        }
        viewModel.feedbackSent.watch { [weak self] sent in
            self?.feedbackSent = sent as? Bool ?? false
        }
        viewModel.elapsedSeconds.watch { [weak self] sec in
            self?.elapsedSeconds = sec?.int32Value ?? 0
        }
        viewModel.timerRunning.watch { [weak self] state in
            self?.timerRunning = state as? Bool ?? false
        }
    }

    func loadData() {
        viewModel.loadData()
    }

    func fetchRecommendation() {
        viewModel.fetchRecommendation()
    }

    func toggleTimer() {
        viewModel.toggleTimer()
    }

    func setSelectedExercise(_ name: String) {
        viewModel.setSelectedExercise(name: name)
    }

    func sendFeedback(successful: Bool) {
        viewModel.sendFeedback(successful: successful)
    }

    func addSeries(exercise: String, weight: Float, reps: Int32, sets: Int32, rpe: Int32) {
        viewModel.addSeries(
            exercise: exercise,
            weight: weight,
            reps: Int(reps),
            sets: Int(sets),
            rpe: Int(rpe)
        )
    }

    func saveAll() {
        viewModel.saveAll()
    }

    func totalWeight() -> Int32 {
        Int32(viewModel.totalWeight())
    }

    func totalReps() -> Int32 {
        Int32(viewModel.totalReps())
    }

    func totalSets() -> Int32 {
        Int32(viewModel.totalSets())
    }

    func averageDuration() -> Int32 {
        Int32(viewModel.averageDuration())
    }
}
